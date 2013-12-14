package com.fandg.santashuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Main activity!
 * 
 */
public class Main extends ListActivity {
    
    private static final int PICK_CONTACT    = 42;
    private ListView         nameListView    = null;
    private NameListAdapter  nameListAdapter = null;
    private AdView           adView          = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Setup the banner ad
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Log.d("test", "uh oh, no play services");
            // Not sure the best way to handle this. The following dialog didn't
            // seem to do anything.
            GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this, 43);
        } else {
            Log.d("test", "we got play services");
        }
        
        // Initiate a generic ad request.
        
        // XXX: This is the production ad request. Uncomment this line for all
        // submissions to the play store.
        AdRequest adRequest = new AdRequest.Builder().build();
        // XXX: Dan's Infuse test ad builder. Leave this here for testing.
        // AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        // "096E6F3C7FF285DF1A11D96519DB5CF1").build();
        // XXX: Dan's S4 test ad builder. Leave this here for testing.
        // AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        // "AF2E1D29685A5C2961BCD01C4F4E71A7").build();
        
        this.adView = (AdView) findViewById(R.id.adView);
        
        // Load the adView with the ad request.
        this.adView.loadAd(adRequest);
        
        // setup the list adapter
        nameListAdapter = new NameListAdapter(this, Family.getNames(),
                Family.getRecipients());
        setListAdapter(nameListAdapter);
        
        nameListView = getListView();
        nameListView.setTextFilterEnabled(true);
        
        // handle list actions
        nameListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Family.setCurrentSelection(position);
                startEditDialog(view);
            }
        });
        
        // TODO: keyboard covers the name you are typing in if there are several
        // Family.getNames() already.
        EditText editText = (EditText) findViewById(R.id.edit_name);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // The user just entered a new name, add it to the list
                    String newPerson = v.getText().toString();
                    if (Main.this.isNameValid(newPerson) == true) {
                        Family.getNames().add(newPerson);
                        Family.getRecipients().add("none");
                        nameListAdapter.notifyDataSetChanged();
                        // clear out the text for the next person
                        v.setText(null);
                        Family.setCurrentSelection(Family.getNames().size());
                        nameListView.smoothScrollToPosition(Family.getNames()
                                .size());
                    }
                }
                
                // return false so the keyboard will disappear
                return false;
            }
        });
        editText.setVisibility(View.VISIBLE);
        
        // Hide the bottom two rows of buttons and the banner ad when the
        // softkeyboard is visible so the name list doesn't shrink to nothing on
        // low resolution screens
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        
                        Rect r = new Rect();
                        // r will be populated with the coordinates of your view
                        // that area still visible.
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        
                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);
                        
                        TableRow pairChooseRow = (TableRow) findViewById(R.id.pair_choose_row);
                        TableRow resetHideRow = (TableRow) findViewById(R.id.reset_hide_row);
                        
                        // if more than 100 pixels, its probably a keyboard...
                        if (heightDiff > 100) {
                            // With a keyboard
                            pairChooseRow.setVisibility(View.GONE);
                            resetHideRow.setVisibility(View.GONE);
                            Main.this.adView.setVisibility(View.GONE);
                            
                        } else {
                            // Without a keyboard
                            pairChooseRow.setVisibility(View.VISIBLE);
                            resetHideRow.setVisibility(View.VISIBLE);
                            Main.this.adView.setVisibility(View.VISIBLE);
                            
                        }
                    }
                });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /** Called when the user clicks the Pair Everyone button */
    public void pairPeople(View view) {
        List<String> shuffledList = new ArrayList<String>(Family.getNames());
        // algorithm from
        // stackoverflow.com/questions/8609644/secret-santa-generating-valid-permutations
        Collections.shuffle(shuffledList);
        if (Family.isPaired()) {
            Family.setPaired(false);
        } else {
            Family.setPaired(true);
        }
        for (int i = 0; i < shuffledList.size() - 1; i++) {
            // recipient for giver at position 'i' is giver at position 'i+1' in
            // shuffled list.
            
            // find giver 'i'
            for (int j = 0; j < Family.getNames().size(); j++) {
                if (Family.getNames().get(j).equals(shuffledList.get(i)) == true) {
                    // set the recipient to next giver in the shuffled list
                    Family.getRecipients().set(j, shuffledList.get(i + 1));
                    break;
                }
            }
        }
        // recipient for giver at the end of the shuffled list is the first
        // giver in the shuffled list
        for (int j = 0; j < Family.getNames().size(); j++) {
            if (Family.getNames().get(j)
                    .equals(shuffledList.get(shuffledList.size() - 1)) == true) {
                Family.getRecipients().set(j, shuffledList.get(0));
                break;
            }
        }
        nameListAdapter.notifyDataSetChanged();
        
    }
    
    private boolean verify() {
        
        for (int i = 0; i < Family.getNames().size(); i++) {
            if (!Family.getChosenIndicies().contains(i)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void onclickChoose(View view) {
        String chosenName = "";
        final int randomInt;
        Random randomGenerator = new Random();
        int index = -1;
        boolean newValFound = true;
        while (verify() && newValFound) {
            
            index = randomGenerator.nextInt(Family.getNames().size());
            if (!Family.getChosenIndicies().contains(index)) {
                break;
            }
            
        }
        if (index == -1) {
            int duration = Toast.LENGTH_SHORT;
            
            Toast toast = Toast.makeText(this, "No Names left to choose",
                    duration * 2);
            toast.setGravity(Gravity.TOP, 0, 50);
            toast.show();
            
            return;
        }
        randomInt = index;
        chosenName = Family.getNames().get(randomInt);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View choseView = inflater.inflate(R.layout.choosen_dialog, null);
        
        TextView choseTextView = (TextView) choseView
                .findViewById(R.id.chose_value);
        choseTextView.setText(chosenName);
        
        builder.setTitle("Chosen Name From List ");
        builder.setView(choseView);
        builder.setPositiveButton("Choose and mark",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        
                        processChoose(randomInt);
                        nameListAdapter.notifyDataSetChanged();
                        
                    }
                });
        builder.setNeutralButton("Choose and delete",
                new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Family.getNames().remove(randomInt);
                        nameListAdapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton(R.string.app_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        
        builder.create();
        builder.show();
        
    }
    
    public void onClickOk(View view) {
        EditText nameView = (EditText) findViewById(R.id.edit_name);
        
        String newName = nameView.getText().toString();
        
        if (this.isNameValid(newName) == true) {
            Family.getNames().add(nameView.getText().toString());
            Family.getRecipients().add(getResources().getString(R.string.none));
            nameListAdapter.notifyDataSetChanged();
            // clear out the text for the next person
            nameView.setText("");
            nameView.setHint(R.string.new_name_hint);
            Family.setCurrentSelection(Family.getNames().size());
            nameListView.smoothScrollToPosition(Family.getNames().size());
        }
        
    }
    
    private void processChoose(int index) {
        Family.getChosenIndicies().add(index);
    }
    
    public void onClickReset(View view) {
        this.clearRecipients();
        
    }
    
    public void onClickHideGifters(View view) {
        if (Family.isHideGifters()) {
            Family.setHideGifters(false);
        } else {
            Family.setHideGifters(true);
        }
        nameListAdapter.notifyDataSetChanged();
    }
    
    private void startEditDialog(View view2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        
        View view = inflater.inflate(R.layout.edit_dialog, null);
        final EditText editName = (EditText) view
                .findViewById(R.id.editNameField);
        editName.setText(Family.getNames().get(Family.getCurrentSelection()));
        final TextView gifterName = (TextView) view2.findViewById(R.id.name);
        editName.setText(gifterName.getText());
        
        builder.setView(view);
        // Add action buttons
        builder.setPositiveButton(R.string.app_change_name,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // remove old name and add this one
                        String oldName = gifterName.getText().toString();
                        String newName = editName.getText().toString();
                        Main.this.changeName(oldName, newName);
                        
                    }
                });
        
        builder.setNegativeButton(R.string.app_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton(R.string.app_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        
                        // TODO: This is a total hack to get it implemented
                        // quickly.
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                Main.this);
                        builder.setMessage(R.string.delete_confirm_title)
                                .setPositiveButton(R.string.app_ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                String gifter = gifterName
                                                        .getText().toString();
                                                Main.this.deleteGifter(gifter);
                                            }
                                        })
                                .setNegativeButton(R.string.app_cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.dismiss();
                                            }
                                        });
                        builder.create();
                        builder.show();
                    }
                });
        builder.setTitle("Edit Gifter Name: ");
        builder.create();
        builder.show();
    }
    
    private void changeName(String oldName, String newName) {
        if (this.isNameValid(newName) == true) {
            // Change the name in the gifter's list
            for (int i = 0; i < Family.getNames().size(); i++) {
                if (Family.getNames().get(i).equals(oldName) == true) {
                    Family.getNames().set(i, newName);
                    break;
                }
            }
            // Change the name in the recipient list
            for (int i = 0; i < Family.getRecipients().size(); i++) {
                if (Family.getRecipients().get(i).equals(oldName) == true) {
                    Family.getRecipients().set(i, newName);
                    break;
                }
            }
            nameListAdapter.notifyDataSetChanged();
        }
    }
    
    private void clearRecipients() {
        
        for (int i = 0; i < Family.getRecipients().size(); i++) {
            Family.getRecipients().set(i,
                    getResources().getString(R.string.none));
        }
        Family.getChosenIndicies().clear();
        Family.setPaired(false);
        nameListAdapter.notifyDataSetChanged();
        
    }
    
    private void deleteGifter(String gifterName) {
        
        boolean gifterDeleted = false;
        
        for (int i = 0; i < Family.getNames().size(); i++) {
            if (Family.getNames().get(i).equals(gifterName) == true) {
                Family.getNames().remove(i);
                gifterDeleted = true;
                break;
            }
        }
        
        if (gifterDeleted == true) {
            this.clearRecipients();
            nameListAdapter.notifyDataSetChanged();
        }
    }
    
    private boolean isNameValid(String newName) {
        boolean isNameValid = true;
        if (newName == null) {
            isNameValid = false;
        } else if (newName.trim().equals("") == true) {
            isNameValid = false;
        } else {
            // verify name is unique
            for (String gifter : Family.getNames()) {
                if (gifter.equals(newName) == true) {
                    isNameValid = false;
                    break;
                }
            }
        }
        return isNameValid;
    }
    
    public void requestContact(View v) {
        try {
            // creates the contact list
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            // intent.setType(ContactsContract.Contacts.DISPLAY_NAME);
            startActivityForResult(intent, PICK_CONTACT);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        
        switch (reqCode) {
        case (PICK_CONTACT):
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData(); // has the uri for picked
                                                  // contact
                try{
                String[] display = { ContactsContract.Contacts.DISPLAY_NAME };
                
                Cursor c = getContentResolver().query(contactData, display,
                        null, null, null); // creates the contact cursor with
                                           // the
                                           // returned uri
                if (c.moveToFirst()) {
                    String name = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (this.isNameValid(name) == true) {
                        Family.getNames().add(name);
                        Family.getRecipients().add("none");
                        nameListAdapter.notifyDataSetChanged();
                        Family.setCurrentSelection(Family.getNames().size());
                        nameListView.smoothScrollToPosition(Family.getNames()
                                .size());
                        
                    }
                }
                }catch(Exception ex){
                	Log.e(this.getClass().getSimpleName(), "Issue with contacts" + ex.getMessage());
                }
            }
            break;
        }
        
    }
    
    @Override
    public void onPause() {
        this.adView.pause();
        super.onPause();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        this.adView.resume();
    }
    
    @Override
    public void onDestroy() {
        this.adView.destroy();
        super.onDestroy();
    }
    
}
