package com.example.family;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    
    ArrayList<String> names           = new ArrayList<String>();
    ArrayList<String> recipients      = new ArrayList<String>();
    ListView          nameListView    = null;
    NameListAdapter   nameListAdapter = null;
    private int currentSelection = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // setup the list adapter
        nameListAdapter = new NameListAdapter(this, this.names, this.recipients);
        setListAdapter(nameListAdapter);
        
        nameListView = getListView();
        nameListView.setTextFilterEnabled(true);
        
        // handle list actions
        nameListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                currentSelection = position;
                startEditDialog(view);
            }
        });
        
        // TODO: keyboard covers the name you are typing in if there are several
        // names already.
        EditText editText = (EditText) findViewById(R.id.edit_name);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // The user just entered a new name, add it to the list
                    String newPerson = v.getText().toString();
                    if (Main.this.isNameValid(newPerson) == true) {
                        Main.this.names.add(newPerson);
                        Main.this.recipients.add("none");
                        nameListAdapter.notifyDataSetChanged();
                        // clear out the text for the next person
                        v.setText(null);
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
        List<String> shuffledList = new ArrayList<String>(this.names);
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
            for (int j = 0; j < this.names.size(); j++) {
                if (this.names.get(j).equals(shuffledList.get(i)) == true) {
                    // set the recipient to next giver in the shuffled list
                    this.recipients.set(j, shuffledList.get(i + 1));
                    break;
                }
            }
        }
        // recipient for giver at the end of the shuffled list is the first
        // giver in the shuffled list
        for (int j = 0; j < this.names.size(); j++) {
            if (this.names.get(j).equals(
                    shuffledList.get(shuffledList.size() - 1)) == true) {
                this.recipients.set(j, shuffledList.get(0));
                break;
            }
        }
        nameListAdapter.notifyDataSetChanged();
        
    }
    
    public void onclickChoose(View view) {
        
    }
    
    public void onClickOk(View view) {
        EditText nameView = (EditText) findViewById(R.id.edit_name);
        
        String newName = nameView.getText().toString();
        
        if (this.isNameValid(newName) == true) {
            Main.this.names.add(nameView.getText().toString());
            Main.this.recipients.add(getResources().getString(R.string.none));
            nameListAdapter.notifyDataSetChanged();
            // clear out the text for the next person
            nameView.setText("");
            nameView.setHint(R.string.new_name_hint);
        }
        
    }
    
    public void onClickReset(View view) {
        this.clearRecipients();
    }
    
    public void onClickDetete(View view) {
        
    }
    
    public void onClickHideGifters(View view){
    	if(Family.isHideGifters()){
    		Family.setHideGifters(false);
    	}
    	else{
    		Family.setHideGifters(true);
    	}
    	 nameListAdapter.notifyDataSetChanged();
    }
    
    
    private void startEditDialog(View view2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        
        View view = inflater.inflate(R.layout.edit_dialog, null);
        final EditText editName = (EditText) view .findViewById(R.id.editNameField);
        editName.setText(names.get(currentSelection));
        final TextView gifterName = (TextView) view2.findViewById(R.id.name);
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
                    	names.remove(currentSelection);
                    	nameListAdapter.notifyDataSetChanged();
                    	
                    	
                        dialog.cancel();
                    }
                });
        builder.setTitle("Edit Gifter Name: ");
        builder.create();
        builder.show();
    }
    
    private void changeName(String oldName, String newName) {
        if (this.isNameValid(newName) == true) {
            // Change the name in the gifter's list
            for (int i = 0; i < this.names.size(); i++) {
                if (this.names.get(i).equals(oldName) == true) {
                    this.names.set(i, newName);
                    break;
                }
            }
            // Change the name in the recipient list
            for (int i = 0; i < this.recipients.size(); i++) {
                if (this.recipients.get(i).equals(oldName) == true) {
                    this.recipients.set(i, newName);
                    break;
                }
            }
            nameListAdapter.notifyDataSetChanged();
        }
    }
    
    private void clearRecipients() {
        for (int i = 0; i < this.recipients.size(); i++) {
            this.recipients.set(i, getResources().getString(R.string.none));
        }
        nameListAdapter.notifyDataSetChanged();
    }
    
    private boolean isNameValid(String newName) {
        boolean isNameValid = true;
        if (newName == null) {
            isNameValid = false;
        } else if (newName.trim().equals("") == true) {
            isNameValid = false;
        } else {
            // verify name is unique
            for (String gifter : this.names) {
                if (gifter.equals(newName) == true) {
                    isNameValid = false;
                    break;
                }
            }
        }
        return isNameValid;
    }
}
