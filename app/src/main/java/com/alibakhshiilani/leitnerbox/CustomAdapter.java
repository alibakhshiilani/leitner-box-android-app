package com.alibakhshiilani.leitnerbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[]  Title;
    private int[] imge;
    private int[] cardCounts;
    private int[] ListIds;

    public CustomAdapter(Context context, String[] text1,int[] imageIds,int[] counts,int[] ids) {
        this.mContext = context;
        this.Title = text1;
        this.imge = imageIds;
        this.ListIds = ids;
        this.cardCounts = counts;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View row;
        row = inflater.inflate(R.layout.listview_custom_row, parent, false);;
        TextView title;
        ImageView i1;
        TextView countl;
        countl = (TextView) row.findViewById(R.id.countTextView);
        i1 = (ImageView) row.findViewById(R.id.imgIcon);
        title = (TextView) row.findViewById(R.id.customTextView);
        title.setText(Title[position]);
        countl.setText(Integer.toString(cardCounts[position]));
        i1.setImageResource(imge[position]);

        return (row);
    }



}