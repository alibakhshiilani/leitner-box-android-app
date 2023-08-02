package com.alibakhshiilani.leitnerbox;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by Ali on 5/24/2017.
 */
public class SearchResultCursor extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private Context context;

    // Default constructor
    public SearchResultCursor(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView onCard = (TextView) view.findViewById(R.id.onCard);
        TextView backCard = (TextView) view.findViewById(R.id.backCard);


        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
        //String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        try {
            name = name.substring(name.length()-10)+"...";
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            value = value.substring(value.length()-10)+"...";
        }catch (Exception e){
            e.printStackTrace();
        }

        onCard.setText(name);
        backCard.setText(value);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.listview_custom_search, parent, false);
    }
}