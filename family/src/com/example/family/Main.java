package com.example.family;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
                // edit/delete?
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
                    // TODO: Verify name is unique.
                    String newPerson = v.getText().toString();
                    Main.this.names.add(newPerson);
                    Main.this.recipients.add("none");
                    nameListAdapter.notifyDataSetChanged();
                    // clear out the text for the next person
                    v.setText(null);
                }
                
                // return false so the keyboard will disappear
                return false;
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
        List<String> shuffledList = new ArrayList<String>(this.names);
        // algorithm from
        // stackoverflow.com/questions/8609644/secret-santa-generating-valid-permutations
        Collections.shuffle(shuffledList);
        
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
    
}
