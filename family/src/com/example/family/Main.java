package com.example.family;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Main extends ListActivity {
    
    ArrayList<String> names           = new ArrayList<String>();
    ListView          nameListView    = null;
    NameListAdapter   nameListAdapter = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // a few names to initialize the list for test purposes
        if (names.size() == 0) {
            names.add("Bob");
            names.add("Fred");
            names.add("Tap To Add A Name");
        }
        
        // setup the list adapter
        nameListAdapter = new NameListAdapter(this, names);
        setListAdapter(nameListAdapter);
        
        nameListView = getListView();
        nameListView.setTextFilterEnabled(true);
        
        // handle list actions
        nameListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get gifter name in selected row
                String selectedValue = (String)getListAdapter().getItem(position);
                
                // When clicked, duplicate a name in the list
                Main.this.names.add(selectedValue);
                nameListAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), selectedValue, Toast.LENGTH_SHORT).show();
                
            }
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
