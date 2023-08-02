package com.alibakhshiilani.leitnerbox.Gateway;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.alibakhshiilani.leitnerbox.Tools.Md5;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by user on 11/1/16.
 */
public class InformationModel extends SQLiteOpenHelper {

    private  String DB_PATH = "";

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "leitnerbox.db";
    private Context context;

    /**
     * @param context The context of the application.
     * @param factory In most cases, pass null for this argument.
     */
    public InformationModel(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context=context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases";

        try{
            this.createDataBase();
        }catch (IOException e){
            Log.e("DB ERROR : ",e.getMessage());
        }

    }


    /**
     * Create a new database.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * Drop existing tables and create new tables.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //myDataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        // onCreate(db);
    }

    public boolean checkPass(String inputPassword){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM information WHERE id = 1",null);
        cursor.moveToFirst();
        String dbPass = cursor.getString(cursor.getColumnIndexOrThrow("value"));
        Md5 md5 = new Md5();
        try {
            inputPassword = md5.get(inputPassword);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        cursor.close();
        db.close();

        return inputPassword.equals(dbPass);

    }


    public boolean hasPassword(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM information WHERE id = 2",null);
        cursor.moveToFirst();
        Integer dbPass = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
        Log.i("passcheck",Integer.toString(dbPass));
        cursor.close();
        db.close();

        return (dbPass != 0);

    }


    public void setPassword(String md5){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("value",md5);
        db.update("information", cv, "id=1", null);
        db.close();
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();
        if(!dbExist){
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            //this.getReadableDatabase();

            Log.i("DbStatus :: ","False");

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database : "+e.getMessage());

            }
        }else{
            Log.i("DbStatus :: ","True");
        }
        String myPath = DB_PATH + "/" + DATABASE_NAME;
        SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + "/" + DATABASE_NAME;
            File file = new File(myPath);
            File folder = new File(DB_PATH);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                if (file.exists() && !file.isDirectory()){
                    checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
                }
            }

        }catch(SQLiteException e){

            Log.i("error creating db : ",e.getMessage());

        }


        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open("db/"+DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + "/" + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    @Override
    public synchronized void close() {

        /*if(myDataBase != null)
            myDataBase.close();
*/
        super.close();

    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
