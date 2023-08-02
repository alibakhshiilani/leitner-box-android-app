package com.alibakhshiilani.leitnerbox.Fragments;

/**
 * Created by Ali on 6/5/2017.
 */
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.ListCartsAdapter;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.MyCursorAdapter;
import com.alibakhshiilani.leitnerbox.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class FragmentListCards extends Fragment {

    private View view;
    private ListView carts;
    private CartModel cartModel;
    private CategoryModel categoryModel;
    private Cursor cartsCursor;
    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private AlertDialog.Builder alertEdit;
    private AlertDialog OptionDialogEdit;

    private long currentCartId;
    private int currentCartPosition;
    private ListCartsAdapter todoAdapter;
    private long catId;
    private Cursor categories;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.list_cart_fragment, container, false);
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        catId = bundle.getLong("categoryId");
        cartModel = new CartModel(getActivity().getApplicationContext(),null);
        categoryModel = new CategoryModel(getActivity().getApplicationContext(),null);
        carts = (ListView) view.findViewById(R.id.cartListView);
        cartsCursor=cartModel.getCartsByCatId(catId);
        cartsCursor.moveToFirst();
        todoAdapter = new ListCartsAdapter(getContext(), cartsCursor , 0);
        carts.setAdapter(todoAdapter);

        carts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cartsCursor.moveToPosition(position);
                currentCartPosition=position;
                currentCartId=cartsCursor.getLong(cartsCursor.getColumnIndexOrThrow("id"));
                alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getActivity().getLayoutInflater();
                View myview = inflater.inflate(R.layout.cart_options_dialog, null);
                OptionDialog.setView(myview);
                ListView listView = (ListView) myview.findViewById(R.id.listOptions);
                List<String> list = new ArrayList<String>();
                list.add("ویرایش");
                list.add("حذف");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        list );

                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                                                            int position, long id) {
                        try {
                            OptionDialog.dismiss();
                            if(position==0){
                                alertEdit = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                                OptionDialogEdit = alertEdit.create();
                                LayoutInflater inflater =getActivity().getLayoutInflater();
                                final View myview = inflater.inflate(R.layout.cart_edit_dialog, null);
                                OptionDialogEdit.setView(myview);
                                Button cancelCart = (Button) myview.findViewById (R.id.cancelCart);
                                cancelCart.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        OptionDialogEdit.dismiss();
                                    }
                                });



                                TextView onCard = (TextView) myview.findViewById(R.id.onCard);
                                onCard.setText(cartsCursor.getString(cartsCursor.getColumnIndexOrThrow("name")));
                                TextView backCard = (TextView) myview.findViewById(R.id.backCard);
                                backCard.setText(cartsCursor.getString(cartsCursor.getColumnIndexOrThrow("value")));
                                TextView cartDesc = (TextView) myview.findViewById(R.id.cartDesc);
                                cartDesc.setText(cartsCursor.getString(cartsCursor.getColumnIndexOrThrow("description")));
                                /*categories = categoryModel.getAllCategory(-2);
                                Spinner categoriesSpinner = (Spinner) myview.findViewById(R.id.categories_spinner);
                                List<String> list = new ArrayList<String>();
                                final List<Integer> listIds = new ArrayList<Integer>();
                                int index=0;
                                int selectedIndex=0;
                                for (boolean hasItem = categories.moveToFirst(); hasItem; hasItem = categories.moveToNext()) {
                                    list.add(categories.getString(categories.getColumnIndex("name")));
                                    listIds.add(categories.getInt(categories.getColumnIndex("id")));
                                    Log.i("id test",Integer.toString(categories.getInt(categories.getColumnIndex("id"))));
                                    if(categories.getInt(categories.getColumnIndex("id")) == catId){
                                        selectedIndex=index;
                                    }
                                    index++;
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_item, list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                categoriesSpinner.setSelection(2,false);
                                categoriesSpinner.setAdapter(dataAdapter);

                                categories.close();*/

                                Button editCart = (Button) myview.findViewById (R.id.editCart);
                                editCart.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        EditText onCard = (EditText) myview.findViewById (R.id.onCard);
                                        EditText backCard = (EditText) myview.findViewById (R.id.backCard);
                                        EditText cartDesc = (EditText) myview.findViewById (R.id.cartDesc);
                                        //Spinner categoriesSpinner2 = (Spinner) myview.findViewById(R.id.categories_spinner);
                                        //int catPosition = categoriesSpinner2.getSelectedItemPosition();
                                        if(onCard.getText().toString().equals("") || backCard.getText().toString().equals("")){
                                            Toast.makeText(getActivity(),"پر کردن متن روی کارت و متن پشت کارت الزامی است",Toast.LENGTH_LONG).show();
                                        }else{
                                            cartModel.updateCarts(onCard.getText().toString(),backCard.getText().toString(),cartDesc.getText().toString(),currentCartId);
                                            cartsCursor=cartModel.getCartsByCatId(catId);
                                            cartsCursor.moveToFirst();
                                            todoAdapter = new ListCartsAdapter(getContext(), cartsCursor , 0);
                                            carts.setAdapter(todoAdapter);
                                            OptionDialogEdit.dismiss();
                                            Toast.makeText(getActivity(),"کارت با موفقیت ویرایش شد",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                                OptionDialogEdit.show();


                            }else{
                                boolean deleteStatus = cartModel.deleteCarts(currentCartId,catId);

                                if(deleteStatus){

                                    cartsCursor=cartModel.getCartsByCatId(catId);
                                    cartsCursor.moveToFirst();
                                    todoAdapter = new ListCartsAdapter(getContext(), cartsCursor , 0);
                                    carts.setAdapter(todoAdapter);


                                }else{

                                    Toast.makeText(getActivity().getApplicationContext(),"امکان حذف کارت های پیشفرض اپلیکیشن وجود ندارد",Toast.LENGTH_LONG).show();

                                }

                                OptionDialogEdit.dismiss();

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                   }
                });

                OptionDialog.show();

            }
        });

    }
}
