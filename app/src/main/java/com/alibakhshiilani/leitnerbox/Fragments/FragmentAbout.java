package com.alibakhshiilani.leitnerbox.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;

/**
 * Created by Ali on 4/24/2017.
 */
public class FragmentAbout extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        return view;
    }
}
