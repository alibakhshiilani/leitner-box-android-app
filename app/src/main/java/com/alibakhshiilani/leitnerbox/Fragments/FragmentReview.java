package com.alibakhshiilani.leitnerbox.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ali on 5/26/2017.
 */
public class FragmentReview extends Fragment {


    public class GetNewCartsTask extends AsyncTask<String,Void,String> {


        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public boolean result;

        @Override
        protected String doInBackground(String... path) {

            try {
                FragmentReview.carts = FragmentReview.cartModel.getCartsByCatIdandLevel(FragmentReview.catId,FragmentReview.startPoint);
                if(FragmentReview.carts!=null && !FragmentReview.carts.isAfterLast() && FragmentReview.carts.getCount() > 0){
                    FragmentReview.asyncResult = true;
                    return "true";
                }else{
                    FragmentReview.carts.close();
                    getActivity().onBackPressed();
                    return "false";
                }
            }catch (Exception e){

                return "false";
            }

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("false")){
                Toast.makeText(getActivity().getApplicationContext(),"کارت ها پایان یافتند",Toast.LENGTH_LONG).show();
                startPoint=0;
                Dialog.dismiss();
                try{
                    getActivity().onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                startJobs();
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



    public class StartReviewTask extends AsyncTask<String,Void,String> {


        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public boolean result;

        @Override
        protected String doInBackground(String... path) {

            cartModel = new CartModel(getActivity().getApplicationContext(),null);
            Log.i("cartsreview",Long.toString(catId)+" "+Integer.toString(startPoint));
            carts = cartModel.getCartsByCatIdandLevel(catId,startPoint);

            CartsCount = carts.getCount();
            Log.i("CartCount",Long.toString(CartsCount));
            carts.moveToFirst();
            if(carts!=null && !carts.isAfterLast() && carts.getCount() > 0){
                return "true";
            }else{
                if(carts!=null) {
                    carts.close();
                }
                try {
                    getActivity().onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                }

                return "false";
            }

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("false")){
                Toast.makeText(getActivity().getApplicationContext(),"کارت ها پایان یافتند",Toast.LENGTH_LONG).show();
                startPoint=0;
                Dialog.dismiss();
                try{
                    getActivity().onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                startJobs();
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


    private View view;

    public static Cursor carts;
    public static long catId;
    private TextView cartText;
    private TextView cartDescription;
    private View line;
    private static boolean asyncResult = false;
    private int CartsCount;
    private long currentCart;
    private int currentLevel;
    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    public static CartModel cartModel;
    private int s = 0;
    private int m = 0; // the total number
    public TextView answers;
    private TextView timerText;
    private Timer timer = new Timer();
    private TimerTask timerTask=null;
    public static int startPoint = 0;
    public android.os.Handler mHandler;
    public int ok=0;
    public int notOk=0;
    TextToSpeech t1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        view = inflater.inflate(R.layout.review_prompt, container, false);
        Bundle b = this.getArguments();
        catId = b.getLong("categoryId");

        timerText = (TextView) view.findViewById(R.id.timer);
        answers = (TextView) view.findViewById(R.id.answers);
        final SharedPreferences shp = getActivity().getApplicationContext().getSharedPreferences("nobegheloghat", Context.MODE_PRIVATE);
        boolean timerStatus = shp.getBoolean("timerSwitch", true);
        if(timerStatus){
            timerText.setText("00:00");
            timerTask = new TimerTask() {
                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            s++;
                            if(s>59){
                                m++;
                                s=0;
                            }

                            String t="";
                            if(m<10){
                                t+="0"+m;
                            }else{
                                t+=m;
                            }

                            if(s<10){
                                t+=" : 0"+s;
                            }else{
                                t+=" : "+s;
                            }

                            timerText.setText(t);
                        }
                    });

                }
            };
        }else{
            timerText.setVisibility(View.GONE);
        }

        String text = " <span> پاسخ صحیح : 0 <br> پاسخ نادرست : 0 </span> ";

        if(Build.VERSION.SDK_INT >= 24){
            answers.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        }else{
            answers.setText(Html.fromHtml(text));
        }

        //timer.schedule(timerTask, 0, 1000);

        cartText = (TextView) view.findViewById(R.id.cartText);
        cartDescription = (TextView) view.findViewById(R.id.cartDescription);
        line = view.findViewById(R.id.cartLine);



        Button b1 = (Button) view.findViewById(R.id.speaker);

        t1=new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = cartText.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mHandler = new android.os.Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                StartReviewTask startReviewTask = new StartReviewTask();
                startReviewTask.execute();

            }
        }, 300);


    }


    @Override
    public void onStart() {
        super.onStart();
        if(timerTask!=null){
            try {
                timer.schedule(timerTask, 0, 1000);
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Timer schedule Error : ",e.getMessage());
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        carts.close();
        if(timerTask!=null){
            timer.cancel();
        }
    }


    private void nextCart(boolean status){

        if(status){
            ok++;
        }else{
            notOk++;
        }

        String text = " <span> پاسخ صحیح : "+ok+" <br> پاسخ نادرست : "+notOk+" </span> ";

        if(Build.VERSION.SDK_INT >= 24){
            answers.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        }else{
            answers.setText(Html.fromHtml(text));
        }

        cartModel.answerCart(status,currentCart,currentLevel);

        if(carts!=null && !carts.isAfterLast() && carts.getCount() > 0){
            startJobs();
        }else{
            startPoint+=40;
            GetNewCartsTask getNewCartsTask = new GetNewCartsTask();
            getNewCartsTask.execute();

        }

        if(asyncResult){
            startJobs();
        }
    }



    private void startJobs(){
        String desc;
        if(carts!=null && carts.getCount()>0  && !carts.isAfterLast()){
            cartText.setText(carts.getString(carts.getColumnIndexOrThrow("name")));
            desc=carts.getString(carts.getColumnIndexOrThrow("description"));
            currentCart=carts.getLong(carts.getColumnIndexOrThrow("id"));
            currentLevel=carts.getInt(carts.getColumnIndexOrThrow("level"));

            if(desc != null && !desc.equals("") && !desc.equals("null")){
                cartDescription.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                cartDescription.setText(desc);
            }else{
                cartDescription.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            }
        }



        Button showResult = (Button) view.findViewById(R.id.showResult);
        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                    alert.setCancelable(false);
                    OptionDialog = alert.create();
                    OptionDialog.setCanceledOnTouchOutside(false);
                    OptionDialog.setCancelable(false);
                    LayoutInflater inflater =getActivity().getLayoutInflater();
                    View myview = inflater.inflate(R.layout.ask_prompt, null);
                    OptionDialog.setView(myview);
                    final EditText edittext = new EditText(getActivity());
                    TextView cartBack = (TextView) myview.findViewById(R.id.cartText);
                    cartBack.setText(carts.getString(carts.getColumnIndexOrThrow("value")));
                    Button no = (Button) myview.findViewById (R.id.no);
                    no.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            nextCart(false);
                            OptionDialog.dismiss();
                        }
                    });
                    Button yes = (Button) myview.findViewById (R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            nextCart(true);
                            OptionDialog.dismiss();
                        }
                    });


                    OptionDialog.show();

                    carts.moveToNext();
                }catch (Exception e){
                    e.printStackTrace();
                    getActivity().onBackPressed();
                }


            }
        });


        /*Button cancelCart = (Button) view.findViewById(R.id.cancelCart);
        cancelCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                FragmentCategoryShow fragmentCategoryShow = new FragmentCategoryShow();
                Bundle bundle = new Bundle();
                bundle.putLong("categoryId",catId);
                fragmentCategoryShow.setArguments(bundle);
                ft.replace(R.id.mainframe, fragmentCategoryShow);
                ft.commit();
            }
        });*/
    }

}
