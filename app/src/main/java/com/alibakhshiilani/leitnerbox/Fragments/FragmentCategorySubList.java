package com.alibakhshiilani.leitnerbox.Fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.MyCursorAdapter;
import com.alibakhshiilani.leitnerbox.R;
import com.alibakhshiilani.leitnerbox.dbo.Category;


public class FragmentCategorySubList extends Fragment {


    private ListView categoryList;
    private View view;
    private TextView title;
    private Button addCategory;
    private Cursor categories;
    private Category category;
    private long categoryId;
    private CategoryModel categoryModel;

    private void addCategory(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        FragmentCategoryAdd fragmentCategoryAdd = new FragmentCategoryAdd();
        ft.addToBackStack("tag").replace(R.id.mainframe, fragmentCategoryAdd);
        ft.commit();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        view = inflater.inflate(R.layout.home_page_fragment, container, false);

        Bundle bundle = this.getArguments();
        categoryId=bundle.getLong("categoryId");
        categoryModel = new CategoryModel(getContext(),null);
        categories = categoryModel.getAllCategory(categoryId);
        category = categoryModel.getCategoryById(categoryId);
                    /*int parentId = categories.getInt(categories.getColumnIndex("parent_id"));
                    if(parentId == 0 && currentPage == 12){
                        MainActivity.fab.setVisibility(View.VISIBLE);
                    }*/
        title = (TextView) view.findViewById(R.id.cartListTitle);
        categoryList = (ListView) view.findViewById(R.id.categoryList);
        addCategory = (Button) view.findViewById(R.id.addCategory);
        title.setText(category.getName());
        addCategory = (Button) view.findViewById(R.id.addCategory);
        addCategory.setText("افزودن گروه");
        if(categoryId != 12){
            addCategory.setVisibility(View.GONE);

        }else{
            TextView moreDescription = (TextView) view.findViewById(R.id.moreDescription);
            moreDescription.setVisibility(View.VISIBLE);

            moreDescription.setClickable(true);
            moreDescription.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "در این بخش می توانید به صورت رایگان گروه های جدید ایجاد نموده و لغات خود را به صورت دستی و یا از طریق فایل اکسل وارد برنامه نمایید. و از تمام امکانات نرم افزار از جمله مرور با اپلیکیشن اپ جعبه لایتنر و همچنین تلفظ تمام لغات و جملاتی که وارد کرده اید و نیز جستجو ی لغات استفاده کنید. لازم به ذکر است این اپلیکیشن فقط مخصوص مرور لغات نیست شما می توانید دروس دیگر خود را نیز با آن مرور کنید. جهت اطلاعات بیشتر به کانال تلگرامی زیر مراجعه نمایید:\n" +
                    "<br><a href=\"https://t.me/joinchat/AAAAAEFzUcd1z3Of98YtjQ\">https://t.me/joinchat/AAAAAEFzUcd1z3Of98YtjQ</a>";

            if(Build.VERSION.SDK_INT >= 24){
                moreDescription.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
            }else{
                moreDescription.setText(Html.fromHtml(text));
            }

            addCategory.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    addCategory();
                }
            });

        }

        categories = categoryModel.getAllCategory(categoryId);
        MyCursorAdapter todoAdapter = new MyCursorAdapter(getContext(), categories , 0);

        title.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
        addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
        categoryList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out));

        categoryList.setAdapter(todoAdapter);

        title.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
        addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
        categoryList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));

        //categoryList.setAdapter(categories);


        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                categories.moveToPosition(position);
                //MainActivity.showSimpleProgressDialog(getActivity().getApplicationContext(),"بارگزاری ...","لطفا منتظر بمانید",false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                FragmentCategoryShow fragmentCategoryShow = new FragmentCategoryShow();
                try{
                    Bundle bundle = new Bundle();
                    bundle.putLong("categoryId",id);
                    bundle.putLong("parentId",categoryId);
                    fragmentCategoryShow.setArguments(bundle);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ft.addToBackStack("tag").replace(R.id.mainframe, fragmentCategoryShow);
                ft.commit();

            }
        });



        return view;

    }



    @Override
    public void onStop() {
        super.onStop();
        //categories.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if(categories==null){
            categories = categoryModel.getAllCategory(0);
        }*/
    }
}
