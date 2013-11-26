package com.example.family;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Main extends ListActivity {
    
    private ListView        nameListView    = null;
    private NameListAdapter nameListAdapter = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
        View recipienHeader = findViewById(R.id.recipient_header);
        if (Family.isPaired()) {
            Family.setPaired(false);
            
            recipienHeader.setVisibility(View.GONE);
        } else {
            Family.setPaired(true);
            recipienHeader.setVisibility(View.VISIBLE);
        }
        recipienHeader.invalidate();
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
    
    public void onclickChoose(View view) {
        String chosenName = "";
        final int randomInt;
        if (Family.getNames().size() > 1) {
            
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(Family.getNames().size() - 1);
            
            chosenName = Family.getNames().get(randomInt);
        } else {
            chosenName = Family.getNames().get(0);
            randomInt = 0;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View choseView = inflater.inflate(R.layout.choosen_dialog, null);
        
        TextView choseTextView = (TextView) choseView
                .findViewById(R.id.chose_value);
        choseTextView.setText(chosenName);
        
        builder.setTitle("Chosen Name From List ");
        builder.setView(choseView);
        builder.setPositiveButton(R.string.app_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        
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
    
    public void onClickReset(View view) {
        this.clearRecipients();
    }
    
    public void onClickDetete(View view) {
        
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
        
        // nameListAdapter.notifyDataSetChanged();
        pairPeople(null);
        
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
}
