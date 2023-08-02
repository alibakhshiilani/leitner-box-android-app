package com.alibakhshiilani.leitnerbox;

import android.content.Context;
import android.database.Cursor;
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
public class MyCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private Context context;

    // Default constructor
    public MyCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView text = (TextView) view.findViewById(R.id.customTextView);
        TextView countText = (TextView) view.findViewById(R.id.countTextView);
        ImageView image = (ImageView) view.findViewById(R.id.imgIcon);
        ImageView free = (ImageView) view.findViewById(R.id.free);
        // Extract properties from cursor
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        if(id == 12){
            free.setVisibility(View.VISIBLE);
        }else{
            free.setVisibility(View.GONE);
        }
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String imagename = cursor.getString(cursor.getColumnIndexOrThrow("image"));
        String count = cursor.getString(cursor.getColumnIndexOrThrow("count"));
        //int count = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));
        // Populate fields with extracted properties
        text.setText(name);
        image.setImageResource(this.getResId(imagename));
        countText.setText(count);
    }

    private int getResId(String resName) {
        resName=resName.split("\\.")[0];
        try {
            return context.getResources().getIdentifier(resName , "drawable" , context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.google_translate_text_language_translation;
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.listview_custom_row, parent, false);
    }
}