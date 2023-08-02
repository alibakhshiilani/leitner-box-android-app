package com.alibakhshiilani.leitnerbox.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.MyCursorAdapter;
import com.alibakhshiilani.leitnerbox.R;


public class HomePageFragment extends Fragment {

    private ListView categoryList;
    private View view;
    private TextView title;
    private int currentPage=0;
    private Cursor categories;
    private CategoryModel category;

    private Button addCategory;
    private MyCursorAdapter todoAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        view = inflater.inflate(R.layout.home_page_fragment, container, false);
        //categoryList = (ListView) view.findViewById(R.id.categoryList);
        categoryList = (ListView) view.findViewById(R.id.categoryList);
        title = (TextView) view.findViewById(R.id.cartListTitle);
        addCategory = (Button) view.findViewById(R.id.addCategory);
        addCategory.setVisibility(View.GONE);
        //if(categoryList != null){



            category = new CategoryModel(getContext(),null);
            categories = category.getAllCategory(0);
            todoAdapter = new MyCursorAdapter(getContext(), categories , 0);
            categoryList.setAdapter(todoAdapter);
            //categoryList.changeCursor(newCursor);


        /*categoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);

                categories.moveToPosition(pos);
                //currentCartPosition=position;
                currentCatId=categories.getLong(categories.getColumnIndexOrThrow("id"));
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
                                final View myview = inflater.inflate(R.layout.edit_category_dialog, null);
                                OptionDialogEdit.setView(myview);
                                Button cancelCategory = (Button) myview.findViewById (R.id.cancelCategory);
                                cancelCategory.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        OptionDialogEdit.dismiss();
                                    }
                                });

                                TextView name = (TextView) myview.findViewById(R.id.category_name);
                                name.setText(categories.getString(categories.getColumnIndexOrThrow("name")));
                                categories = category.getAllCategory(-2);


                                //categories.close();

                                Button editCategory = (Button) myview.findViewById (R.id.editCategory);
                                editCategory.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        EditText name = (EditText) myview.findViewById (R.id.category_name);

                                        if(name.getText().toString().equals("")){
                                            Toast.makeText(getActivity(),"نام دسته بندی الزامی است",Toast.LENGTH_LONG).show();
                                        }else{
                                            category.updateCategory(name.getText().toString(),currentCatId);
                                            categories = category.getAllCategory(currentPage);
                                            categories.moveToFirst();
                                            todoAdapter =new MyCursorAdapter(getContext(), categories , 0);
                                            categoryList.setAdapter(todoAdapter);
                                            OptionDialogEdit.dismiss();
                                            Toast.makeText(getActivity(),"کارت با موفقیت ویرایش شد",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                Log.i("test dialog","true");
                                OptionDialogEdit.show();


                            }else{
                                boolean status = category.deleteCategory(currentCatId);
                                if(status){
                                    categories = category.getAllCategory(currentPage);
                                    categories.moveToFirst();
                                    todoAdapter =new MyCursorAdapter(getContext(), categories , 0);
                                    categoryList.setAdapter(todoAdapter);
                                }else{
                                    Toast.makeText(getActivity(),"امکان حذف دسته بندی های اصلی نرم افزار وجود ندارد",Toast.LENGTH_LONG).show();
                                }
                                OptionDialog.dismiss();

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });

                OptionDialog.show();

                return true;
            }
        });*/

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {


                    categories.moveToPosition(position);
                    currentPage =categories.getInt(categories.getColumnIndex("id"));

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentCategorySubList fragmentCategorySubList = new FragmentCategorySubList();
                    Bundle bundle = new Bundle();
                    bundle.putLong("categoryId",currentPage);
                    fragmentCategorySubList.setArguments(bundle);
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentCategorySubList);
                    ft.commit();

                    /*int parentId = categories.getInt(categories.getColumnIndex("parent_id"));
                    if(parentId == 0 && currentPage == 12){
                        MainActivity.fab.setVisibility(View.VISIBLE);
                    }*/
                    /*title.setText(categories.getString(categories.getColumnIndex("name")));
                    addCategory.setText("بازگشت به صفحه اصلی");
                    categories = category.getAllCategory(currentPage);
                    MyCursorAdapter todoAdapter = new MyCursorAdapter(getContext(), categories , 0);

                    title.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
                    addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
                    categoryList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out));

                    categoryList.setAdapter(todoAdapter);

                    title.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
                    addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
                    categoryList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));

                    //categoryList.setAdapter(categories);

                    addCategory.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out));
                            addCategory.setText("افزودن گروه جدید");
                            addCategory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
                            backCategory(lastPage);
                        }
                    });


                    categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            //MainActivity.showSimpleProgressDialog(getActivity().getApplicationContext(),"بارگزاری ...","لطفا منتظر بمانید",false);
                            categories.moveToPosition(position);
                            currentPage =categories.getInt(categories.getColumnIndex("id"));
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            FragmentCategoryShow fragmentCategoryShow = new FragmentCategoryShow();
                            Bundle bundle = new Bundle();
                            bundle.putLong("categoryId",currentPage);
                            fragmentCategoryShow.setArguments(bundle);
                            ft.addToBackStack("tag").replace(R.id.mainframe, fragmentCategoryShow);
                            ft.commit();
                        }
                    });
                */

                }
            });



        /*addCategory.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                addCategory();
            }
        });*/

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
        if(categories==null){
            categories = category.getAllCategory(0);
        }
    }
}
