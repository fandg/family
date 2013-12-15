package com.fandg.santashuffle;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NameListAdapter extends ArrayAdapter<String> {
    private final Context           context;
    private final ArrayList<String> giverList;
    private final ArrayList<String> recipientList;
    
    public NameListAdapter(Context context, ArrayList<String> giverList,
            ArrayList<String> recipientList) {
        super(context, R.layout.name_list, giverList);
        this.context = context;
        this.giverList = giverList;
        this.recipientList = recipientList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // get the text views for the gifter and the recipient
        View rowView = inflater.inflate(R.layout.name_list, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView recipientView = (TextView) rowView
                .findViewById(R.id.recipient);
        int textColor = Color.BLACK;
        if ((Family.getChosenIndicies().contains(position) == true)
                && (Family.isChosen() == true)) {
            textColor = Color.RED;
        }
        
        if (Family.isHideGifters()) {
            try {
                String name = giverList.get(position);
                
                String newString = "";
                for (int i = 0; i < name.length(); i++) {
                    newString += "-";
                }
                
                nameView.setText(newString);
                nameView.setTextColor(textColor);
                
            } catch (IllegalArgumentException ex) {
                Log.d(this.getClass().getSimpleName(), ex.getMessage());
            }
        } else {
            nameView.setText(giverList.get(position));
            nameView.setTextColor(textColor);
        }
        
        if (Family.isPaired()) {
            recipientView.setText(recipientList.get(position));
        } else {
            if (Family.getChosenIndicies().contains(position)) {
                recipientView.setText(R.string.name_is_chosen);
            } else {
                recipientView.setText("");
            }
            recipientView.setTextColor(textColor);
        }
        
        return rowView;
    }
    
}