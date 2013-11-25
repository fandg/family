package com.example.family;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NameListAdapter extends ArrayAdapter<String> {
    private final Context           context;
    private final ArrayList<String> giverList;
    private final ArrayList<String> recipientList;
    
    public NameListAdapter(Context context,
                           ArrayList<String> giverList,
                           ArrayList<String> recipientList) {
        super(context, R.layout.name_list, giverList);
        this.context = context;
        this.giverList = giverList;
        this.recipientList = recipientList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // get the text views for the gifter and the recipient
        View rowView = inflater.inflate(R.layout.name_list, parent, false);
        TextView nameView = (TextView)rowView.findViewById(R.id.name);
        TextView recipientView = (TextView)rowView.findViewById(R.id.recipient);
        
        nameView.setText(giverList.get(position));
        
        if(Family.isPaired()){
        
        recipientView.setText(recipientList.get(position));
        }
        
        return rowView;
    }
    
}