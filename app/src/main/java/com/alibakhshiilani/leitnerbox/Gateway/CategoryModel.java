package com.alibakhshiilani.leitnerbox.Gateway;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.alibakhshiilani.leitnerbox.dbo.Carts;
import com.alibakhshiilani.leitnerbox.dbo.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Created by user on 11/1/16.
 */
public class CategoryModel extends SQLiteOpenHelper implements Serializable {

    private  String DB_PATH = "";

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "leitnerbox.db";
    private String TABLE_CATEGORY = "categories";
    private String TABLE_CATEGORY_COLUMN_ID = "id";
    private String TABLE_CATEGORY_COLUMN_NAME = "name";
    private String TABLE_CATEGORY_COLUMN_IMAGE = "image";
    private String TABLE_CATEGORY_COLUMN_TYPE = "type";
    private String TABLE_CATEGORY_COLUMN_PARENT_ID= "parent_id";
    private String TABLE_CATEGORY_COLUMN_CREATED_AT= "created_at";
    private Context context;

    /**
     * @param context The context of the application.
     * @param factory In most cases, pass null for this argument.
     */
    public CategoryModel(Context context, SQLiteDatabase.CursorFactory factory) {
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

    /**
     * Delete a CARTS record from the database.
     *
     * @param id
     */
    public boolean deleteCategory(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor categoryCursor=db.rawQuery("SELECT * FROM categories WHERE id="+id, null);
        categoryCursor.moveToFirst();
        int type = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow("type"));
        if(type==0){
            return false;
        }
        int parent_id = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow("parent_id"));
        if(parent_id==0){

            String query = "SELECT * FROM categories WHERE parent_id = "+id;

            // Execute the query, and get the results.
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            // Get the SQL CARTS results. In theory, the SQL query should only return one result.
            // This loop is created just in case there are no CARTS records with the given id.
            while (!cursor.isAfterLast()) {
                db.execSQL("DELETE FROM cards WHERE cat_id = " + cursor.getLong(cursor.getColumnIndex("id")) + ";");
                cursor.moveToNext();
            }

            // Close the database connection.
            cursor.close();
            db.execSQL("DELETE FROM categories WHERE parent_id = " + id + ";");

        }else{
            db.execSQL("DELETE FROM cards WHERE cat_id = " + id + ";");
        }
        db.execSQL("DELETE FROM categories WHERE id = " + id + ";");
        categoryCursor.close();
        db.close();
        return true;
    }


    /**
     * Create a Category record In the database.
     *
     * @param categoryName name of category String
     */
    public void updateCategory(String categoryName,long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",categoryName);
        db.update("categories", cv, "id="+id, null);
        db.close();
    }


    /**
     * Create a Category record In the database.
     *
     * @param categoryName name of category String
     * @param categoryParentId id of parent Category Integer
     */
    public boolean addCategory(String categoryName,Integer categoryParentId) {

        SQLiteDatabase db2 = getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String millisInString  = dateFormat.format(new Date());

        if(categoryParentId!=0){
            Cursor categoryCursor=db2.rawQuery("SELECT * FROM categories WHERE id="+categoryParentId, null);
            categoryCursor.moveToFirst();
            int count = categoryCursor.getInt(categoryCursor.getColumnIndex("count"));
            count++;
            ContentValues cv = new ContentValues();
            cv.put("count",count);
            db2.update("categories", cv, "id="+categoryParentId, null);
            db2.close();
        }

        SQLiteDatabase db = getWritableDatabase();

        try{
            db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name,parent_id,type,image,created_at) VALUES ('"+categoryName+"','"+categoryParentId+"','1','google_translate_text_language_translation.png','"+millisInString+"')");

        }catch (SQLiteException e){
            Log.e("Insert Error : "," CategoryModel.java : addCategory");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    /**
     * Gets a CARTS record from the database with the given id.
     *
     * @param id
     * @return CARTS
     */
    public Category getCategoryById(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        Category category = new Category();
        String query = "SELECT * FROM " + TABLE_CATEGORY
                + " WHERE " + TABLE_CATEGORY_COLUMN_ID + " = '" + id + "';";

        // Execute the query, and get the results.
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        // Get the SQL CARTS results. In theory, the SQL query should only return one result.
        // This loop is created just in case there are no CARTS records with the given id.
        while (!cursor.isAfterLast()) {
            if (cursor.getLong(cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_ID)) != 0) {
                category.setId(cursor.getLong(
                        cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_ID)));
                category.setName(cursor.getString(
                        cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_NAME)));
                category.setImage(cursor.getString(
                        cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_IMAGE)));
                category.setParent_id(cursor.getLong(
                        cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_PARENT_ID)));
                category.setType(cursor.getInt(
                        cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_TYPE)));
            } // end if (cursor.getLong(cursor.getColumnIndex(TABLE_CATEGORY_COLUMN_ID)) != 0)

            // Move the cursor to the next result to prevent infinite looping.
            cursor.moveToNext();
        }

        // Close the database connection.
        cursor.close();
        db.close();

        // Return the search result.
        return category;
    }

    /**
     * Returns a list of all Carts records in the database.
     *
     * @return Carts
     */
    public Cursor getAllCategory(long parent) {
        SQLiteDatabase db = getReadableDatabase();
        if(!db.isOpen()){
            try{
                this.createDataBase();
            }catch (IOException e){
                Log.e("DB ERROR : ",e.getMessage());
            }
        }
        String query;
        if (parent == -1){
            query = "SELECT id as _id,* FROM " + TABLE_CATEGORY;
        }else if(parent == -2){
            query = "SELECT id as _id,* FROM " + TABLE_CATEGORY + " WHERE parent_id != 0";
        }else if(parent == -3){
            query = "SELECT id as _id,* FROM " + TABLE_CATEGORY + " WHERE parent_id != 0 AND type == 1";
        }else{
            query = "SELECT id as _id,* FROM " + TABLE_CATEGORY + " WHERE parent_id = "+parent;
        }

        query += " ORDER BY sort DESC";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        db.close();
        return cursor;
    }



    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */

    public int[] getCountByLevel(int level, int cat_id){

        SQLiteDatabase db = getReadableDatabase();
        double days = 0;

        int[] counts = new int[4];
        if(level != 0){
            days = Math.pow(2,level-1);
            String queryRed = "SELECT id as _id,id FROM cards WHERE cat_id = "+cat_id+" AND readed_at >= date('now','-"+days+" day') AND level = "+level;
            try{
                Cursor cursor = db.rawQuery(queryRed, null);
                cursor.moveToFirst();
                counts[3] = cursor.getCount();
                cursor.close();
            }catch (SQLiteException e){
                Log.e("getCountByLevel()",e.getMessage());
            }

            String queryGreen = "SELECT id as _id,id FROM cards WHERE cat_id = "+cat_id+" AND readed_at < date('now','-"+days+" day') AND level = "+level;
            try{
                Cursor cursor = db.rawQuery(queryGreen, null);
                cursor.moveToFirst();
                counts[2] = cursor.getCount();
                cursor.close();
            }catch (SQLiteException e){
                Log.e("getCountByLevel()",e.getMessage());
            }


            if(level == 7){
                String queryBlue = "SELECT id as _id,id FROM cards WHERE cat_id = "+cat_id+" AND status = 1 AND level = "+level;
                try{
                    Cursor cursor = db.rawQuery(queryBlue, null);
                    cursor.moveToFirst();
                    counts[1] = cursor.getCount();
                    cursor.close();
                }catch (SQLiteException e){
                    Log.e("getCountByLevel()",e.getMessage());
                }
            }
        }else{
            String queryGray = "SELECT id as _id,id FROM cards WHERE level = "+level+" AND cat_id = "+cat_id;
            Cursor cursor = db.rawQuery(queryGray, null);
            cursor.moveToFirst();
            counts[0] = cursor.getCount();
            cursor.close();
        }

        return counts;

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

        /*
        File database=getApplicationContext().getDatabasePath("databasename.db");

if (!database.exists()) {
    // Database does not exist so copy it from assets here
    Log.i("Database", "Not Found");
} else {
    Log.i("Database", "Found");
}
         */

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
