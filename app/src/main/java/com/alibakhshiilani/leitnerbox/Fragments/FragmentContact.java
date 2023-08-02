package com.alibakhshiilani.leitnerbox.Fragments;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ali on 4/24/2017.
 */
public class FragmentContact extends Fragment {

    public class DownloadTask extends AsyncTask<String,Void,String> {

        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public String result = "";

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection HttpConnection;

            try {

                url = new URL(urls[0]);

                HttpConnection = (HttpURLConnection) url.openConnection();

                InputStream in = HttpConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }


            }catch (Exception e){

                Log.i("Error",e.toString());
                return "Fail";
            }


            return result;

        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("Fail")){
                Toast.makeText(getActivity().getApplicationContext(),"مشکلی در دریافت اطلاعات پیش آمده است لطفا دوباره تلاش نمایید",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"پیغام شما با موفقیت ارسال شد",Toast.LENGTH_LONG).show();
            }

            Dialog.dismiss();
        }


        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال دریافت اطلاعات لطفا منتظر بمانید");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.show();
        }


    }


    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        view = inflater.inflate(R.layout.contact_fragment, container, false);


        Button send = (Button) view.findViewById(R.id.sendMessage);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText nameEditText = (EditText) view.findViewById(R.id.contact_name);
                String name = nameEditText.getText().toString();

                EditText phoneEditText = (EditText) view.findViewById(R.id.contact_phone);
                String phone = phoneEditText.getText().toString();

                EditText mailEditText = (EditText) view.findViewById(R.id.contact_mail);
                String mail = mailEditText.getText().toString();

                EditText contactEditText = (EditText) view.findViewById(R.id.contact_body);
                String contact = contactEditText.getText().toString();

                boolean status = true;

                if(name.equals("") || contact.equals("") || mail.equals("") || phone.equals("")){
                    status=false;
                    Toast.makeText(getActivity().getApplicationContext(),"لطفا تمامی فیلد ها را پر نمایید !",Toast.LENGTH_LONG).show();
                }

                if(!isNetworkConnected() && status){
                    status=false;
                    Toast.makeText(getActivity().getApplicationContext(),"اتصال اینترنت برقرار نیست",Toast.LENGTH_LONG).show();
                }


                if(status){


                    DownloadTask downloadtask = new DownloadTask();
                    downloadtask.execute("http://tarazgan.com/appleitnerbox/send/mail/"+name+"/"+mail+"/"+contact+"/"+phone);

                }


            }
        });




        return view;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
