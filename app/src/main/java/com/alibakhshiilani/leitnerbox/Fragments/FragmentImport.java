package com.alibakhshiilani.leitnerbox.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;
import com.alibakhshiilani.leitnerbox.Tools.FileEncryption;
import com.alibakhshiilani.leitnerbox.simplefilechooser.Constants;
import com.alibakhshiilani.leitnerbox.simplefilechooser.ui.FileChooserActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Ali Bakhshi on 5/2/2017.
 * www.ali-bakhshi.ir
 */


public class FragmentImport extends Fragment {



    public class ImportTask extends AsyncTask<String,Void,String> {


        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public boolean result;

        @Override
        protected String doInBackground(String... path) {

            try {
                FileEncryption secure = new FileEncryption();
                result = Decrypt(secure,path[0]);
                return Boolean.toString(result);
            }catch (GeneralSecurityException e){
                e.printStackTrace();
                return "false";
            }

        }

        private void copyFile(String filename) throws IOException {

            InputStream myInput = getActivity().getApplicationContext().getAssets().open("keys/"+filename+".der");

            String outFileName = getActivity().getApplicationContext().getApplicationInfo().dataDir + "/"+filename+".der";

            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        private boolean Decrypt (FileEncryption secure,String backupPath){

            File privateKeyFile = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/private.der");
            if(!privateKeyFile.exists()){
                try {
                    copyFile("private");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            File encryptedBackUp = new File(backupPath);
            File folderAbsolutePath = new File(encryptedBackUp.getAbsolutePath());
            File folder = new File(folderAbsolutePath.getParent());
            File unencryptedFile = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/databases/leitnerbox-new.db");
            File currentDb = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/databases/leitnerbox.db");
            File secureFile = new File (folder,"key.data");
            try {
                secure.loadKey(secureFile, privateKeyFile);
                secure.decrypt(encryptedBackUp, unencryptedFile);
                boolean deleteStatus = currentDb.delete();
                Log.i("DELETE DB STATUS : ",Boolean.toString(deleteStatus));
                File newName = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/databases/leitnerbox.db");
                boolean renameStatus = unencryptedFile.renameTo(newName);
                Log.i("Rename DB STATUS : ",Boolean.toString(renameStatus));
                return true;
            }catch (GeneralSecurityException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return false;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("false")){
                Toast.makeText(getActivity().getApplicationContext(),"مشکلی در بازگردانی فایل پشتیبان پیش امده لطفا بعدا تلاش نمایید",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"فایل پشتیبان باز گردانی شد",Toast.LENGTH_LONG).show();
            }

            Dialog.dismiss();
        }


        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال بازگردانی اطلاعات , لطفا از برنامه خارج نشوید");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.show();
        }


    }


    private View view;
    private String fileSelected="";
    private TextView filenameText;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/

        view = inflater.inflate(R.layout.import_fragment, container, false);
        Button selectBtn = (Button) view.findViewById(R.id.Choose);
        filenameText = (TextView) view.findViewById(R.id.filename);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FileChooserActivity.class);
                startActivityForResult(intent, FILE_CHOOSER);
            }
        });
        Button startImportBtn = (Button) view.findViewById(R.id.startImport);
        startImportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileSelected != null && !fileSelected.equals("")){
                    ImportTask importTask = new ImportTask();
                    importTask.execute(fileSelected);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"فایلی انتخاب نشده است , لطفا فایل بک آپ را انتخاب کنید",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    final int FILE_CHOOSER=1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FILE_CHOOSER) && (resultCode == MainActivity.RESULT_OK)) {
            filenameText = (TextView) view.findViewById(R.id.filename);
            try {
                fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
                String[] splitedFileName = fileSelected.split("/");

                String justName = splitedFileName[splitedFileName.length-1];
                String[] spDot=justName.split("\\.");

                Log.i("Format File : ", spDot[spDot.length-1]);

                if(spDot.length > 1 && spDot[spDot.length-1].equals("backup")){
                    filenameText.setText(splitedFileName[splitedFileName.length-1]);
                }else{
                    fileSelected="";
                    filenameText.setText("هیچ فایلی انتخاب نشده است");

                    Toast.makeText(getActivity().getApplicationContext(),"فایل انتخاب شده باید با فرمت backup باشد",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                fileSelected="";
                filenameText.setText("هیچ فایلی انتخاب نشده است");
                Toast.makeText(getActivity().getApplicationContext(),"مشکلی در انتخاب فایل پیش آمده لطفا دوباره تلاش نمایید",Toast.LENGTH_LONG).show();
            }
        }
    }
}
