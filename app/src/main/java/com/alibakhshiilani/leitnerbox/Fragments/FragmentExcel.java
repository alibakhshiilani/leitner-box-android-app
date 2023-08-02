package com.alibakhshiilani.leitnerbox.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;
import com.alibakhshiilani.leitnerbox.simplefilechooser.Constants;
import com.alibakhshiilani.leitnerbox.simplefilechooser.ui.FileChooserActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Ali Bakhshi on 5/2/2017.
 * www.ali-bakhshi.ir
 */

public class FragmentExcel extends Fragment {

    public class ImportTask extends AsyncTask<String, Void, String> {


        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public boolean result = true;

        private long catId;

        public ImportTask(long catId) {
            this.catId = catId;
        }

        @Override
        protected String doInBackground(String... path) {

            try {
                readXls(path[0]);
                return Boolean.toString(result);
            } catch (BiffException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "false";

        }

        private void readXls(String path) throws IOException, BiffException {
            File xls = new File(path);
            FileInputStream is = new FileInputStream(xls);
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            //int col=s.getColumns();
            CartModel cartModel = new CartModel(getActivity().getApplicationContext(), null);

            String cc;
            for (int i = 0; i < row; i++) {
                ArrayList<String> rowData = new ArrayList<>();
                for (int c = 0; c < 3; c++) {
                    try {
                        Cell z = s.getCell(c, i);
                        cc = z.getContents();
                        if (!cc.equals("")) {
                            //Log.i("test",Long.toString(c));
                            rowData.add(cc);
                        }
                        if (c == 1 && cc.equals("")) {
                            //Log.i("test",Long.toString(c));
                            cc = "";
                            rowData.add(cc);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }

                //Log.i("cat id",Long.toString(rowData.size()));
                if (rowData.size() == 3) {
                    cartModel.saveCarts(rowData.get(0), rowData.get(2), rowData.get(1), catId);
                }

                rowData.clear();
            }
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("false")) {
                Toast.makeText(getActivity().getApplicationContext(), "مشکلی در خواندن فایل پیش امده لطفا با فایل دیگری تلاش کنید", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "فایل Excel به برنامه افزوده شد", Toast.LENGTH_LONG).show();
            }

            Dialog.dismiss();
        }


        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال انتقال اطلاعات از فایل اکسل , لطفا منتظر بمانید");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.setCancelable(false);
            Dialog.show();
        }


    }


    private View view;
    private String fileSelected = "";
    private TextView filenameText;
    public final int FILE_CHOOSER = 1;
    private CategoryModel category;
    private Cursor categories;
    public static Long categoryId;
    private Spinner categoriesSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/

        view = inflater.inflate(R.layout.excel_fragment, container, false);

        try{
            Bundle bundle = this.getArguments();
            categoryId = bundle.getLong("categoryId");
        }catch (NullPointerException e){
            categoryId=0L;
            e.printStackTrace();
        }

        category = new CategoryModel(getActivity().getApplicationContext(), null);
        categories = category.getAllCategory(-3);

        categoriesSpinner = (Spinner) view.findViewById(R.id.categories_spinner);
        List<String> list = new ArrayList<String>();
        final List<Integer> listIds = new ArrayList<Integer>();
        Map<String, String> map = new HashMap<String, String>();
        int i=0;
        for (boolean hasItem = categories.moveToFirst(); hasItem; hasItem = categories.moveToNext()) {
            list.add(categories.getString(categories.getColumnIndex("name")));
            listIds.add(categories.getInt(categories.getColumnIndex("id")));
            map.put(categories.getString(categories.getColumnIndex("id")), Integer.toString(i++));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(dataAdapter);
        try{
            categoriesSpinner.setSelection(Integer.parseInt(map.get(Long.toString(categoryId))));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        categories.close();
        // spinner end

        Button selectBtn = (Button) view.findViewById(R.id.ChooseExcel);
        filenameText = (TextView) view.findViewById(R.id.filenameExcel);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FileChooserActivity.class);
                startActivityForResult(intent, FILE_CHOOSER);
            }
        });
        Button startImportBtn = (Button) view.findViewById(R.id.startImportExcel);
        startImportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int catPosition = categoriesSpinner.getSelectedItemPosition();
                if (catPosition != -1) {
                    if (fileSelected != null && !fileSelected.equals("")) {
                        ImportTask importTask = new ImportTask(listIds.get(catPosition));
                        importTask.execute(fileSelected);
                        fileSelected = "";
                        filenameText.setText("");
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "فایلی انتخاب نشده است , لطفا یک فایل Excel انتخاب نمایید", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "دسته بندی انتخاب شده نا معتبر می باشد , لطفا یک دسته بندی جدید ساخته و دوباره امتحان نمایید", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FILE_CHOOSER) && (resultCode == MainActivity.RESULT_OK)) {
            try {
                fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
                String[] splitedFileName = fileSelected.split("/");

                String justName = splitedFileName[splitedFileName.length - 1];
                String[] spDot = justName.split("\\.");

                Log.i("Format File : ", spDot[spDot.length - 1]);

                if (spDot.length > 1 && spDot[spDot.length - 1].equals("xls")) {
                    filenameText.setText(splitedFileName[splitedFileName.length - 1]);
                } else {
                    fileSelected = "";
                    filenameText.setText("هیچ فایلی انتخاب نشده است");
                    Toast.makeText(getActivity().getApplicationContext(), "فایل انتخاب شده باید با فرمت xls باشد", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileSelected = "";
                filenameText.setText("هیچ فایلی انتخاب نشده است");
                Toast.makeText(getActivity().getApplicationContext(), "مشکلی در انتخاب فایل پیش آمده لطفا دوباره تلاش نمایید", Toast.LENGTH_LONG).show();
            }
        }
    }

}