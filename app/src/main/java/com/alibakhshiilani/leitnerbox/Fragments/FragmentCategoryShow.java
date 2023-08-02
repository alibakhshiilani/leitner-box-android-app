package com.alibakhshiilani.leitnerbox.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.MyAxisValueFormatter;
import com.alibakhshiilani.leitnerbox.MyValueFormatter;
import com.alibakhshiilani.leitnerbox.OverWriteFont;
import com.alibakhshiilani.leitnerbox.R;
import com.alibakhshiilani.leitnerbox.YValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.alibakhshiilani.leitnerbox.dbo.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FragmentCategoryShow extends Fragment {

    private ImageView i1;
    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private CartModel cart;


    public class DbTask extends AsyncTask<Long,Void,List<Object>> {

        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        @Override
        protected List<Object> doInBackground(Long... url) {
            Log.i("doInBackground : => ","ok"+Long.toString(categoryId));
            List<Object> objects = new ArrayList<>();

            try {
                CategoryModel categoryModel=new CategoryModel(getActivity().getApplicationContext(),null);
                objects.add(categoryModel);
                objects.add(categoryModel.getCategoryById(categoryId));
                CartModel cartModel = new CartModel(getActivity().getApplicationContext(),null);
                carts = cartModel.getReadableCarts(categoryId);
                objects.add(cartModel);
                yVals1 = new ArrayList<BarEntry>();
                for (int i = 0; i < 8; i++) {
                    int[] count = categoryModel.getCountByLevel(i,(int)(long)categoryId);
                    Log.i("Analyse : => ", Arrays.toString(count));
                    float[] vals = new float[4];
                    if(i==7){
                        //float val1 = (float) (Math.random() * mult) + mult / 3;
                        vals[1] = Math.round(count[1]);
                        vals[3] = Math.round(count[3]);
                    }else if(i==0){
                        vals[0] = Math.round(count[0]);
                        readable+=Math.ceil(count[0]);
                    }else{
                        vals[2] = Math.round(count[2]);
                        readable+=Math.ceil(count[2]);
                        vals[3] = Math.round(count[3]);
                    }
                    yVals1.add(new BarEntry(
                            i,
                            vals,
                            getResources().getDrawable(R.drawable.add_icon)));
                }
                objects.add(yVals1);
                return objects;
            }catch (Exception e){
                e.printStackTrace();
            }
            return objects;
        }

        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);

            try {
                categoryModel= (CategoryModel) result.get(0);
                category= (Category) result.get(1);
                yVals1= (ArrayList) result.get(3);
                cartModel= (CartModel) result.get(2);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                TextView categoryTitle = (TextView) viewLayout.findViewById(R.id.categoryShowTitle);
                TextView categoryTitleChart = (TextView) viewLayout.findViewById(R.id.chartTitle);
                i1 = (ImageView) viewLayout.findViewById(R.id.catLogo);
                String imagename = category.getImage();
                if (imagename.indexOf(".") > 0){
                    imagename = imagename.substring(0, imagename.lastIndexOf("."));
                }

                int resourceId = getActivity().getResources().getIdentifier(imagename, "drawable", getActivity().getPackageName());
                i1.setImageResource(resourceId);

                Log.i("image",imagename);
                categoryTitle.setText(category.getName());
                categoryTitleChart.setText("آمار یادگیری "+category.getName());

                mChart = (BarChart) viewLayout.findViewById(R.id.barChart);
                typeface=OverWriteFont.setChartFont(getActivity(),"fonts/BHoma.ttf");
                Legend legend = mChart.getLegend();
                legend.setTextSize(13f);
                legend.setTypeface(typeface);
                //mChart.setOnChartValueSelectedListener(getContext());
                mChart.getDescription().setEnabled(false);
                mChart.setMaxVisibleValueCount(40);
                mChart.setPinchZoom(false);
                mChart.setDrawGridBackground(false);
                mChart.setDrawBarShadow(false);
                mChart.setDrawValueAboveBar(false);
                mChart.setHighlightFullBarEnabled(false);
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setTextSize(12f);
                leftAxis.setTypeface(typeface);
                leftAxis.setValueFormatter(new YValueFormatter());
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                mChart.getAxisRight().setEnabled(false);
                XAxis xLabels = mChart.getXAxis();
                xLabels.setTextSize(12f);
                xLabels.setTypeface(typeface);
                xLabels.setPosition(XAxis.XAxisPosition.TOP);
                xLabels.setValueFormatter(new MyAxisValueFormatter());
                BarDataSet set1;
                if (mChart.getData() != null &&
                        mChart.getData().getDataSetCount() > 0) {
                    set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                    set1.setValueTextSize(12f);
                    set1.setValues(yVals1);
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                } else {
                    set1 = new BarDataSet(yVals1, "");
                    set1.setValueTextSize(12f);
                    set1.setDrawIcons(false);
                    set1.setColors(new int[]{R.color.gray , R.color.blue, R.color.red, R.color.green} , getActivity().getApplicationContext());
                    set1.setStackLabels(new String[]{"مرور نشده","مرور کامل","آماده مرور","مرور شده"});

                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);

                    BarData data = new BarData(dataSets);
                    data.setValueFormatter(new MyValueFormatter());
                    data.setValueTextColor(Color.WHITE);

                    mChart.setData(data);
                }

                mChart.setFitBars(true);
                mChart.invalidate();

                if(category.getParent_id() == 12){
                    addFromInput.setVisibility(View.VISIBLE);
                    addFromExcel.setVisibility(View.VISIBLE);

                    addFromInput.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                            OptionDialog = alert.create();
                            LayoutInflater inflater =getActivity().getLayoutInflater();
                            final View myview = inflater.inflate(R.layout.prompt_dialog, null);
                            OptionDialog.setView(myview);
                            final EditText edittext = new EditText(getActivity());
                            Button cancelCart = (Button) myview.findViewById (R.id.cancelCart);
                            cancelCart.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    OptionDialog.dismiss();
                                }
                            });
                            // spinner start
                            /*categories = category.getAllCategory(-2);
                            Spinner categoriesSpinner = (Spinner) myview.findViewById(R.id.categories_spinner);
                            List<String> list = new ArrayList<String>();
                            final List<Integer> listIds = new ArrayList<Integer>();
                            for (boolean hasItem = categories.moveToFirst(); hasItem; hasItem = categories.moveToNext()) {
                                list.add(categories.getString(categories.getColumnIndex("name")));
                                listIds.add(categories.getInt(categories.getColumnIndex("id")));
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
                                    android.R.layout.simple_spinner_item, list);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categoriesSpinner.setAdapter(dataAdapter);
                            categories.close();*/

                            Button saveCart = (Button) myview.findViewById (R.id.saveCart);
                            saveCart.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    EditText onCard = (EditText) myview.findViewById (R.id.onCard);
                                    EditText backCard = (EditText) myview.findViewById (R.id.backCard);
                                    EditText cartDesc = (EditText) myview.findViewById (R.id.cartDesc);
                                    /*Spinner categoriesSpinner2 = (Spinner) myview.findViewById(R.id.categories_spinner);
                                    int catPosition = categoriesSpinner2.getSelectedItemPosition();*/
                                    if(onCard.getText().toString().equals("") || backCard.getText().toString().equals("")){
                                        Toast.makeText(getActivity(),"پر کردن متن روی کارت و متن پشت کارت الزامی است",Toast.LENGTH_LONG).show();
                                    }else{
                                        cartModel = new CartModel(getActivity().getApplicationContext(),null);
                                        cartModel.saveCarts(onCard.getText().toString(),backCard.getText().toString(),cartDesc.getText().toString(), FragmentCategoryShow.categoryId);
                                        OptionDialog.dismiss();
                                        Toast.makeText(getActivity(),"کارت با موفقیت افزوده شد",Toast.LENGTH_LONG).show();

                                        DbTask dbTask = new DbTask();
                                        dbTask.execute();


                                    }
                                }
                            });


                            OptionDialog.show();

                        }
                    });

                    addFromExcel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            FragmentExcel fragmentExcel = new FragmentExcel();
                            Bundle bundle = new Bundle();
                            bundle.putLong("categoryId",categoryId);
                            fragmentExcel.setArguments(bundle);
                            ft.addToBackStack("tag").replace(R.id.mainframe, fragmentExcel);
                            ft.commit();

                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(),"مشکلی در برنامه به وجود امده است لطفا دوباره تلاش نمایید",Toast.LENGTH_LONG).show();
            }

            Dialog.dismiss();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال بارگزاری ...");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.setCancelable(false);
            Dialog.show();
        }


    }

    private BarChart mChart;
    private Typeface typeface;
    public static Long categoryId;
    public static Long parentId;
    private Category category;
    private CategoryModel categoryModel;
    private CartModel cartModel;
    private int readable=0;
    public long carts;
    private ArrayList<BarEntry> yVals1;
    public android.os.Handler mHandler;
    private boolean catStatus=false;
    private View viewLayout;
    public Button addFromInput;
    public Button addFromExcel;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        catStatus=bundle.getBoolean("catStatus");
        Log.i("test",Boolean.toString(catStatus));
        if(catStatus){
            Toast.makeText(getActivity().getApplicationContext(),"این دسته بندی کارت اماده مرور ندارد لطفا در زمان دیگری تلاش کنید",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = this.getArguments();
        categoryId=bundle.getLong("categoryId");
        parentId=bundle.getLong("parentId");

        mHandler = new android.os.Handler();

        mHandler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {

                                     DbTask dbTask = new DbTask();
                                     dbTask.execute();

                                 }
        }, 350);





        viewLayout = view;

        addFromInput = (Button) viewLayout.findViewById(R.id.addFromInput);
        addFromExcel = (Button) viewLayout.findViewById(R.id.addFromExcel);

        Button learn = (Button) viewLayout.findViewById(R.id.startReview);
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean accessManager = false;
                String price = "1500";
                String name = "";

                if(parentId==1){
                    accessManager = MainActivity.userIsSchools;
                    name = "واژگان مدرسه";
                }else if(parentId==4){
                    accessManager = MainActivity.userIsLearners;
                    name = "زبان آموزان";
                    price = "2500";
                }else if(parentId==6){
                    accessManager = MainActivity.userIsMasters;
                    name = "کارشناسی ارشد";
                }else if(parentId==7){
                    accessManager = MainActivity.userIsTechnical;
                    name = "زبان تخصصی";
                }


                if(MainActivity.userIsFull){
                    accessManager = true;
                }

                Log.i("test",Boolean.toString(accessManager));

                if(accessManager || parentId == 12){

                    Log.i("cartsnumber",Long.toString(carts)+" "+Integer.toString(readable));

                    if(readable > 0){

                        if(carts > 0){

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            FragmentReview fragmentReview = new FragmentReview();
                            Bundle bundle = new Bundle();
                            bundle.putLong("categoryId",categoryId);
                            fragmentReview.setArguments(bundle);
                            //ft.addToBackStack("tag").replace(R.id.mainframe, fragmentReview);
                            ft.addToBackStack("tag").replace(R.id.mainframe, fragmentReview);
                            ft.commit();


                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"این دسته بندی کارت اماده مرور ندارد لطفا در زمان دیگری تلاش کنید",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"این دسته بندی کارت اماده مرور ندارد لطفا در زمان دیگری تلاش کنید",Toast.LENGTH_LONG).show();
                    }

                }else{
                    alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                    OptionDialog = alert.create();
                    LayoutInflater inflater =getActivity().getLayoutInflater();
                    View myview = inflater.inflate(R.layout.buy_prompt, null);
                    OptionDialog.setView(myview);
                    //final EditText edittext = new EditText(getActivity());
                    TextView singleBtn = (TextView) myview.findViewById(R.id.single);
                    singleBtn.setText("خرید گروه "+name+" ("+price+" تومان)");
                    Button full = (Button) myview.findViewById (R.id.full);
                    full.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            ((MainActivity)getActivity()).onUpgradeAppButtonClicked(0);
                            OptionDialog.dismiss();
                        }
                    });
                    // spinner end
                    Button single = (Button) myview.findViewById (R.id.single);
                    single.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            ((MainActivity)getActivity()).onUpgradeAppButtonClicked(parentId);
                            OptionDialog.dismiss();
                        }
                    });


                    OptionDialog.show();

                }



            }
        });


        Button list = (Button) viewLayout.findViewById(R.id.listCarts);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                FragmentListCards fragmentListCards = new FragmentListCards();
                Bundle bundle = new Bundle();
                bundle.putLong("categoryId",categoryId);
                fragmentListCards.setArguments(bundle);
                ft.addToBackStack("tag").replace(R.id.mainframe, fragmentListCards);
                ft.commit();

            }
        });


        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {






            }
        });*/



       /* mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {



            }
        }, 1000);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {


            }
        }, 2000);*/


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_show_fragment, container, false);
        mHandler = new android.os.Handler();
        return view;

    }



}
