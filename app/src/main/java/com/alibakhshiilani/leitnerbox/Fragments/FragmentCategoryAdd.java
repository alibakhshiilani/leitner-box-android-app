package com.alibakhshiilani.leitnerbox.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;
import java.util.ArrayList;
import java.util.List;

public class FragmentCategoryAdd extends Fragment {
    private LayoutInflater inflater;
    private Cursor categories;
    private CategoryModel category;
    private View view;
    private List<Integer> listIds;
    private EditText categoryName;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        catStatus=bundle.getBoolean("categoryId");*/

        view = inflater.inflate(R.layout.add_categories_fragment, container, false);
        this.category = new CategoryModel(getActivity().getApplicationContext(),null);

        /*if(view != null){
            Spinner categoriesSpinner2 = (Spinner) view.findViewById(R.id.categories_spinner2);
            if(categoriesSpinner2 != null){
                categories = category.getAllCategory(0);
                List<String> list2 = new ArrayList<String>();
                listIds = new ArrayList<Integer>();
                list2.add("بدون دسته بندی مادر");
                listIds.add(0);
                for (boolean hasItem = categories.moveToFirst(); hasItem; hasItem = categories.moveToNext()) {
                    list2.add(categories.getString(categories.getColumnIndex("name")));
                    listIds.add(categories.getInt(categories.getColumnIndex("id")));
                }
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, list2);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoriesSpinner2.setAdapter(dataAdapter2);
                categories.close();
            }
        }*/

        categoryName = (EditText) view.findViewById(R.id.category_name);

        categoryName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });

        Button addCat = (Button) view.findViewById(R.id.addCategory);
        addCat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Spinner categoriesSpinner2 = (Spinner) view.findViewById(R.id.categories_spinner2);
                String CategotyNameString = categoryName.getText().toString();
                if(!CategotyNameString.equals("")){
                    //Integer catPosition = categoriesSpinner2.getSelectedItemPosition();
                    category.addCategory(CategotyNameString,12);
                    Toast.makeText(getActivity().getApplicationContext(),"گروه جدید در بخش گروه های جدید ایجاد شد",Toast.LENGTH_LONG).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    HomePageFragment homePageFragment = new HomePageFragment();
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ft.replace(R.id.mainframe, homePageFragment);
                    ft.commit();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"لطفا نام گروه را وارد نمایید",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
}
