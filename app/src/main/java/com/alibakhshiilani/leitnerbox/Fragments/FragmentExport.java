package com.alibakhshiilani.leitnerbox.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;
import com.alibakhshiilani.leitnerbox.Tools.FileEncryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ali Bakhshi on 4/24/2017.
 * Export Fragment Class !
 */
public class FragmentExport extends Fragment {

    public class ExportTask extends AsyncTask<String,Void,String> {

        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        public String result = "";

        @Override
        protected String doInBackground(String... urls) {

            try {
                FileEncryption secure = new FileEncryption();
                Encrypt(secure);
                return result;
            }catch (GeneralSecurityException e){
                e.printStackTrace();
                return "false";
            }

        }

        private void copyFile() throws IOException{

            InputStream myInput = getActivity().getApplicationContext().getAssets().open("keys/public.der");

            String outFileName = getActivity().getApplicationContext().getApplicationInfo().dataDir + "/public.der";

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

        private boolean Encrypt(FileEncryption secure){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
            String date = format.format(new Date());
            String filename="leitnerbox";
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File (root.getAbsolutePath() + "/leitnerbox-backup");
            if(!dir.exists()){
                dir.mkdirs();
            }
            File folder = new File (root.getAbsolutePath() + "/leitnerbox-backup/"+date);
            if(!folder.exists()){
                folder.mkdirs();
            }
            File publicKeyData = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/public.der");
            if(!publicKeyData.exists()){
                try {
                    copyFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            File secureFile = new File(folder, filename+".backup");

            File originalFile = new File(getActivity().getApplicationContext().getApplicationInfo().dataDir + "/databases/leitnerbox.db");

            File encryptFile = new File (folder,"key.data");
            try {
                secure.makeKey();
                secure.saveKey(encryptFile, publicKeyData);
                secure.encrypt(originalFile, secureFile);
            }catch (GeneralSecurityException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return true;

        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("false")){
                Toast.makeText(getActivity().getApplicationContext(),"مشکلی در ایجاد فایل پشتیبان پیش امده است لطفا بعدا تلاش نمایید",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"فایل پشتیبان در حافظه دستگاه ایجاد شد",Toast.LENGTH_LONG).show();
            }

            Dialog.dismiss();
        }


        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("در حال ایجاد فایل پشتیبان لطفا منتظر بمانید و برنامه را نبندید !");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.setCancelable(false);
            Dialog.show();
        }


    }



    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*MainActivity.fab.setVisibility(View.GONE);
        MainActivity.fabExcel.setVisibility(View.GONE);*/
        view = inflater.inflate(R.layout.export_fragment, container, false);

        Button exportBtn = (Button) view.findViewById(R.id.exportNow);

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportTask exportTask = new ExportTask();
                exportTask.execute();
            }
        });

        return view;
    }
}
