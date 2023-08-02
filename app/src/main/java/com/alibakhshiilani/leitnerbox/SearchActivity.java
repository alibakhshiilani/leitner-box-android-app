package com.alibakhshiilani.leitnerbox;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Fragments.FragmentCategoryShow;
import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    public class SearchTask extends AsyncTask<String,Void,String> {


        private ProgressDialog Dialog = new ProgressDialog(SearchActivity.this);
        public boolean result;

        @Override
        protected String doInBackground(String... words) {


            String searchQuery = words[0];

            if(searchQuery.isEmpty()){
                return "empty";
            }else{

                cartModel = new CartModel(getApplicationContext(),null);
                carts = cartModel.getCartsByQuery(searchQuery,searchQuery,searchQuery);
                carts.moveToFirst();

                if(carts.getCount() == 0){

                    return "false";

                }else{

                    todoAdapter = new SearchResultCursor(getApplicationContext(), carts , 0);
                    return "true";

                }

            }


        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("false")){
                Toast.makeText(getApplicationContext(),"نتیجه ای یافت نشد",Toast.LENGTH_LONG).show();
            }else if(result.equals("true")){
                searchResultView.setAdapter(todoAdapter);
                searchResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        carts.moveToPosition(position);


                        try {
                            alert = new AlertDialog.Builder(SearchActivity.this, R.style.MyDialogTheme);
                            alert.setCancelable(true);
                            OptionDialog = alert.create();
                            OptionDialog.setCanceledOnTouchOutside(true);
                            OptionDialog.setCancelable(true);
                            LayoutInflater inflater =getLayoutInflater();
                            View myview = inflater.inflate(R.layout.ask_prompt, null);
                            OptionDialog.setView(myview);
                            final EditText edittext = new EditText(SearchActivity.this);
                            TextView cartBack = (TextView) myview.findViewById(R.id.cartText);
                            cartBack.setText(carts.getString(carts.getColumnIndexOrThrow("name")));
                            Button no = (Button) myview.findViewById (R.id.no);
                            Button yes = (Button) myview.findViewById (R.id.yes);
                            TextView cartDescription = (TextView) myview.findViewById (R.id.cartDescription);
                            cartDescription.setText(carts.getString(carts.getColumnIndexOrThrow("value")));
                            //cartDescription.setVisibility(View.GONE);
                            TextView titleTop = (TextView) myview.findViewById (R.id.titleTop);
                            titleTop.setVisibility(View.GONE);
                            no.setVisibility(View.GONE);
                            yes.setVisibility(View.GONE);
                            OptionDialog.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
            }else if(result.equals("empty")){
                Toast.makeText(getApplicationContext(),"لطفا کلمه ای برای جستجو وارد نمایید",Toast.LENGTH_LONG).show();
            }

            View view = SearchActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            Dialog.dismiss();
        }


        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال بارگزاری اطلاعات ...");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.setCancelable(false);
            Dialog.show();
        }


    }

    private ListView searchResultView;
    private CartModel cartModel;
    private Cursor carts;
    private SearchResultCursor todoAdapter;
    private EditText searchInput;
    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView back = (ImageView) findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });


        searchResultView = (ListView) findViewById(R.id.searchResult);
        searchInput = (EditText) findViewById(R.id.searchInput);

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        ImageView searchBtn = (ImageView) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });

        /*searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    searchInput.setHint("");
                else
                    searchInput.setHint("Your hint");
            }
        });*/

    }

    private void performSearch(){
        String[] myStringArray = new String[1];
        myStringArray[0] = searchInput.getText().toString();
        SearchTask searchTask = new SearchTask();
        searchTask.execute(myStringArray);
    }


}
