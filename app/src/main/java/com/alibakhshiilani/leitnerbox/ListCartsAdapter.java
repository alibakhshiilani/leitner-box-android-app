package com.alibakhshiilani.leitnerbox;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


/**
 * Created by Ali on 5/24/2017.
 */
public class ListCartsAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private Context context;

    // Default constructor
    public ListCartsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView text = (TextView) view.findViewById(R.id.customTextView);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        //int count = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));
        // Populate fields with extracted properties
        text.setText(name);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.cart_list, parent, false);
    }
}