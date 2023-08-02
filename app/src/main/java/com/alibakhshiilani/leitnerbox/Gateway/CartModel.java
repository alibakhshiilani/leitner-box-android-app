    package com.alibakhshiilani.leitnerbox.Gateway;
    import android.content.Context;
    import android.database.SQLException;
    import android.database.sqlite.SQLiteException;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.content.Context;
    import android.content.ContentValues;
    import android.database.sqlite.SQLiteStatement;
    import android.os.Environment;
    import android.util.Log;
    import android.widget.Toast;
    import com.alibakhshiilani.leitnerbox.dbo.Carts;
    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.io.Serializable;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    /**
     * Created by user on 11/1/16.
     */
    public class CartModel extends SQLiteOpenHelper  implements Serializable {

        private static String DB_PATH = "";

        private static int DATABASE_VERSION = 1;
        private static String DATABASE_NAME = "leitnerbox.db";
        private static String TABLE_CARTS = "cards";
        private static String TABLE_CARTS_COLUMN_ID = "id";
        private static String TABLE_CARTS_COLUMN_NAME = "name";
        private static String TABLE_CARTS_COLUMN_VALUE = "value";
        private static String TABLE_CARTS_COLUMN_DESCIPTION = "description";
        private static String TABLE_CARTS_COLUMN_CAT_ID= "cat_id";
        private static String TABLE_CARTS_COLUMN_LEVEL= "level";
        private static String TABLE_CARTS_COLUMN_CREATED_AT= "created_at";
        private static String TABLE_CARTS_COLUMN_READED_AT= "readed_at";
        private Context context;
        private SQLiteDatabase myDataBase;

        /**
         * @param context The context of the application.
         * @param factory In most cases, pass null for this argument.
         */
        public CartModel(Context context, SQLiteDatabase.CursorFactory factory) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
            this.context=context;
            DB_PATH = context.getApplicationInfo().dataDir + "/databases";

            try{
                this.createDataBase();
            }catch (IOException e){
                Log.e("DB ERROR : ",e.getMessage());
            }

        }

        /*
        read dn from sql file
         */

        /*public static String readFromAssets(Context context, String filename) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

            // do reading, usually loop until end of file reading
            StringBuilder sb = new StringBuilder();
            String mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();
            return sb.toString();
        }*/

        /**
         * Create a new database.
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE `cards` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT, `value` TEXT, `description` TEXT DEFAULT NULL, `cat_id` INTEGER, `level` INTEGER, `type` BOOLEAN DEFAULT 0, `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP, `readed_at` DATETIME DEFAULT CURRENT_TIMESTAMP, `status` INTEGER DEFAULT 0 )";

            db.execSQL(query);
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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTS);

            onCreate(db);
        }

        /**
         * Delete a CARTS record from the database.
         *
         * @param id
         */
        public boolean deleteCarts(long id,long catId) {
            SQLiteDatabase db = getWritableDatabase();
            Cursor categoryCursor=db.rawQuery("SELECT * FROM categories WHERE id="+catId, null);
            categoryCursor.moveToFirst();
            int count = categoryCursor.getInt(categoryCursor.getColumnIndex("count"));
            int type = categoryCursor.getInt(categoryCursor.getColumnIndex("type"));

            if(type==0){
                return false;
            }

            count--;
            ContentValues cv = new ContentValues();
            cv.put("count",count);
            db.update("categories", cv, "id="+catId, null);
            db.execSQL("DELETE FROM " + TABLE_CARTS + " WHERE " + TABLE_CARTS_COLUMN_ID
                    + " = " + id + ";");
            db.close();

            return true;
        }

        /**
         * Saves a CARTS record to the database.
         * Performs an insert or update operation on the CARTS table.
         *
         * @param name name of cart
         * @param value value of cart
         * @param desc desc of cart
         * @param catId id of category
         */
        public void saveCarts(String name,String value,String desc,long catId) {
            SQLiteDatabase db = getWritableDatabase();


            Cursor categoryCursor=db.rawQuery("SELECT * FROM categories WHERE id="+catId, null);
            categoryCursor.moveToFirst();

            int count = categoryCursor.getInt(categoryCursor.getColumnIndex("count"));
            count++;

            ContentValues cv = new ContentValues();
            cv.put("count",count);
            db.update("categories", cv, "id="+catId, null);

            if(desc.equals("")){
                desc=null;
            }
            db.close();
            categoryCursor.close();
            SQLiteDatabase db2 = getWritableDatabase();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(new Date());

            ContentValues valuesInsert = new ContentValues();
            valuesInsert.put("name", name);
            valuesInsert.put("value", value);
            valuesInsert.put("description", desc);
            valuesInsert.put("cat_id", catId);
            valuesInsert.put("readed_at", date);
            valuesInsert.put("created_at", date);
            valuesInsert.put("type", 1);
            valuesInsert.put("level", 0);

            db2.insert(TABLE_CARTS,null,valuesInsert);

            db2.close();
        }


        /**
         * Update a cart record to the database.
         * Performs an insert or update operation on the CARTS table.
         *
         * @param name name of cart
         * @param value value of cart
         * @param desc desc of cart
         * @param id id of cart
         */
        public void updateCarts(String name,String value,String desc,long id) {
            Log.i("cart Id",Long.toString(id));
            SQLiteDatabase db = getWritableDatabase();

            if(desc.equals("")){
                desc=null;
            }
            ContentValues valuesInsert = new ContentValues();
            valuesInsert.put("name", name);
            valuesInsert.put("value", value);
            valuesInsert.put("description", desc);
            db.update(TABLE_CARTS, valuesInsert, "id="+id, null);
            db.close();
        }

        /**
         * Gets a CARTS record from the database with the given id.
         *
         * @param id
         * @return CARTS
         */
        public Carts getCartById(Long id) {
            Carts carts = new Carts();
            String query = "SELECT * FROM " + TABLE_CARTS
                    + " WHERE " + TABLE_CARTS_COLUMN_ID + " = '" + id + "';";

            // Execute the query, and get the results.
            Cursor cursor = myDataBase.rawQuery(query, null);
            cursor.moveToFirst();

            // Get the SQL CARTS results. In theory, the SQL query should only return one result.
            // This loop is created just in case there are no CARTS records with the given id.
            while (!cursor.isAfterLast()) {
                if (cursor.getLong(cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)) != 0) {
                    carts.setId(cursor.getLong(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)));
                    carts.setName(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_NAME)));
                    carts.setValue(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_VALUE)));
                    carts.setCat_id(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_CAT_ID)));
                    carts.setLevel(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_LEVEL)));
                } // end if (cursor.getLong(cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)) != 0)

                // Move the cursor to the next result to prevent infinite looping.
                cursor.moveToNext();
            }

            cursor.close();

            // Close the database connection.
            myDataBase.close();

            // Return the search result.
            return carts;
        }

        /**
         * Returns a list of all Carts records in the database.
         *
         * @return Carts
         */
        public ArrayList<Carts> getAllCarts() {
            ArrayList<Carts> allCarts = new ArrayList<>();

            // Build the SQL query, and execute it.
            String query = "SELECT * FROM " + TABLE_CARTS + " ;";
            Cursor cursor = myDataBase.rawQuery(query, null);
            cursor.moveToFirst();

            // Loop through each result.
            while (!cursor.isAfterLast()) {
                // Create a new CARTS object.
                Carts carts = new Carts();

                // Add data to the CARTS object.
                if (cursor.getLong(cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)) != 0) {
                    carts.setId(cursor.getLong(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)));
                    carts.setName(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_NAME)));
                    carts.setValue(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_VALUE)));
                    carts.setCat_id(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_CAT_ID)));
                    carts.setLevel(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_LEVEL)));
                } // end if (cursor.getLong(cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID) != 0)

                // Move the cursor to prevent an infinite loop.
                cursor.moveToNext();

                // Add the CARTS object to the list of records.
                allCarts.add(carts);
            }

            cursor.close();

            // Close the database connection.
            myDataBase.close();

            // Return all CARTS records.
            return allCarts;
        }


        /**
         * Returns a list of all Carts records in the database by Caetegory
         *
         * @return Carts
         */
        public Cursor getCartsByCatId(Long catId) {
            SQLiteDatabase db = getReadableDatabase();
            String query;
            query = "SELECT id as _id,* FROM " + TABLE_CARTS + " WHERE cat_id = "+catId;
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            List<Object> objects = new ArrayList<>();
            /*Carts carts;
            while (!cursor.isAfterLast()){
                carts=new Carts();
                carts.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                carts.setValue(cursor.getString(cursor.getColumnIndexOrThrow("value")));
                carts.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                carts.setId(cursor.getLong(cursor.getColumnIndexOrThrow("ID")));
                objects.add(carts);
            }
            db.close();
            cursor.close();*/
            db.close();
            return cursor;
        }



        /**
         * Returns a list of all Carts records in the database by Caetegory
         *
         * @return Carts
         */
        public Cursor getCartsByQuery(String name,String value,String description) {
            SQLiteDatabase db = getReadableDatabase();
            String query;
            query = "SELECT id as _id,* FROM cards WHERE name LIKE '%"+name+"%' OR value LIKE '%"+value+"%' OR description LIKE '%"+description+"%'";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            db.close();
            return cursor;
        }



        /**
         * Returns a list of all Carts records in the database by Caetegory
         *
         * @return Carts
         */
        public Cursor getCartsByCatIdandLevel(Long catId,int startPoint) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor;//UNION
            double days = 0;
            String queryGreen="";
            for (int level=0;level<8;level++){
                if(level != 0){
                    days = Math.pow(2,level-1);
                    queryGreen += " UNION SELECT id as _id,* FROM cards WHERE cat_id = "+catId+" AND readed_at < date('now','-"+days+" day') AND level = "+level;
                }else{
                    queryGreen = "SELECT id as _id,* FROM cards WHERE level = "+level+" AND cat_id = "+catId;

                }
            }

            queryGreen+=" ORDER BY level DESC LIMIT "+startPoint+" , 40";

            try{
                cursor = db.rawQuery(queryGreen, null);
                cursor.moveToFirst();
            }catch (SQLiteException e){
                cursor=null;
                Log.e("getCartsByCatId : ",e.getMessage());
            }

            db.close();
            return cursor;
        }



        public long getReadableCarts(Long catId) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor;//UNION
            double days = 0;
            String queryGreen="";
            for (int level=0;level<8;level++){
                if(level != 0){
                    days = Math.pow(2,level-1);
                    queryGreen += " UNION SELECT count(*) FROM cards WHERE cat_id = "+catId+" AND readed_at < date('now','-"+days+" day') AND level = "+level;
                }else{
                    queryGreen = "SELECT count(*) FROM cards WHERE level = "+level+" AND cat_id = "+catId;

                }
            }
            //SQLiteStatement s = db.compileStatement(queryGreen);
            //long count = s.simpleQueryForLong();
            /*for (int level=0;level<8;level++){
                if(level != 0){
                    days = Math.pow(2,level-1);
                    queryGreen += " UNION SELECT id as _id FROM cards WHERE cat_id = "+catId+" AND readed_at < date('now','-"+days+" day') AND level = "+level;
                }else{
                    queryGreen = "SELECT id as _id FROM cards WHERE level = "+level+" AND cat_id = "+catId;

                }
            }*/
            long count;
            try{
                cursor = db.rawQuery(queryGreen, null);
                count = cursor.getCount();
            }catch (SQLiteException e){
                Log.e("getCartsByCatId : ",e.getMessage());
                count=0;
            }

            db.close();
            return count;
        }


        /**
         * Returns a list of CARTS records from the database that match the parameters of the
         * CARTS object that is passed to the function. This function only searches attributes
         * of the CARTS object that are not null.
         * <p/>
         * The boolean operator is the operator used in the where clause of the SQL query.
         *
         * @param cart
         * @param booleanOperator either AND or OR
         * @return
         */
        public ArrayList<Carts> getCartsList(Carts cart, String booleanOperator) {
            ArrayList<Carts> allCarts = new ArrayList<>();
            // Start building the SQL query.
            StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_CARTS + " ");
            StringBuilder whereClause = new StringBuilder(" WHERE ");

            // Cycle through each of the attributes in the object.
            int attributeCount = 0;
            if (Long.toString(cart.getId()) != null) {
                if (attributeCount > 0) {
                    whereClause.append(" " + booleanOperator + " ");
                }
                whereClause.append(TABLE_CARTS_COLUMN_ID + " = '" + cart.getId() + "' ");
                attributeCount++;
            }
            if (cart.getName() != null) {
                if (attributeCount > 0) {
                    whereClause.append(" " + booleanOperator + " ");
                }
                whereClause.append(TABLE_CARTS_COLUMN_NAME + " = '"
                        + cart.getName().toString().replaceAll("'", "\\'") + "' ");
                attributeCount++;
            }
            if (cart.getValue() != null) {
                if (attributeCount > 0) {
                    whereClause.append(" " + booleanOperator + " ");
                }
                whereClause.append(TABLE_CARTS_COLUMN_VALUE + " = '" + cart.getValue() + "' ");
                attributeCount++;
            }


            // Include the where clause in the SQL query if there are search parameters set.
            if (attributeCount > 0) {
                query.append(whereClause.toString());
            }

            // Close the SQL query statement.
            query.append(";");

            // Execute the SQL query.
            Cursor cursor = myDataBase.rawQuery(query.toString(), null);
            cursor.moveToFirst();

            // Loop through each result.
            while (!cursor.isAfterLast()) {
                // Create a new CARTS object.
                Carts newObject = new Carts();

                // Add data to the CARTS object.
                if (cursor.getInt(cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)) != 0) {

                    newObject.setId(cursor.getLong(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_ID)));
                    newObject.setName(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_NAME)));
                    newObject.setValue(cursor.getString(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_VALUE)));
                    newObject.setCat_id(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_CAT_ID)));
                    newObject.setLevel(cursor.getInt(
                            cursor.getColumnIndex(TABLE_CARTS_COLUMN_LEVEL)));
                }

                // Move the cursor to prevent an infinite loop.
                cursor.moveToNext();

                // Add the CARTS object to the list of records.
                allCarts.add(newObject);
            }

            // Close the database connection.
            cursor.close();
            myDataBase.close();

            // Return all CARTS records.
            return allCarts;
        }


        /**
         * Returns a list of CARTS records from the database that match the parameters of the
         * CARTS object that is passed to the function. This function only searches attributes
         * of the CARTS object that are not null.
         * <p/>
         * This function uses the AND operator for exclusive searching.
         *
         * @param status
         * @return
         */
        public void answerCart(boolean status,long id,int level) {
            SQLiteDatabase db = this.getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate  = dateFormat.format(new Date());
            //String query;
            Log.i("db logs : ",Boolean.toString(status));
            Log.i("db logs 2 : ",Long.toString(id));
            Log.i("db logs 3 : ",Integer.toString(level));
            int nextLevel=level+1;
            int prevLevel=level-1;

            ContentValues cv = new ContentValues();
            if(status){
                if(level!=7){
                    cv.put("readed_at",currentDate);
                    cv.put("level",nextLevel);
                }else{
                    cv.put("readed_at",currentDate);
                    cv.put("status",1);
                }
            }else{
                if(level!=0){
                    cv.put("readed_at",currentDate);
                    cv.put("level",prevLevel);
                }else{
                    cv.put("readed_at",currentDate);
                }
            }

            db.update(TABLE_CARTS, cv, "id="+id, null);

            //db.execSQL(query);

            /*Cursor cursor;
            cursor = db.rawQuery("SELECT changes() AS affected_row_count", null);
            if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
            {
                final long affectedRowCount = cursor.getLong(cursor.getColumnIndex("affected_row_count"));
                Log.d("LOG", "affectedRowCount = " + affectedRowCount);
            }

            cursor = db.rawQuery("SELECT * FROM "+TABLE_CARTS+" WHERE name = 'jealous'", null);
            if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
            {
                final String affectedRowCount = cursor.getString(cursor.getColumnIndex("created_at"));
                final int affectedRowCount2 = cursor.getInt(cursor.getColumnIndex("level"));
                Log.d("LOG 2", "affectedRowCount3333 = " + affectedRowCount+"  -   "+Integer.toString(affectedRowCount2));
            }

            cursor.close();*/
            db.close();

        }

        /**
         * Returns a list of CARTS records from the database that match the parameters of the
         * CARTS object that is passed to the function. This function only searches attributes
         * of the CARTS object that are not null.
         * <p/>
         * This function uses the AND operator for exclusive searching.
         *
         * @param cart
         * @return
         */
        public ArrayList<Carts> getCartListUsingAndOperator(Carts cart) {
            return getCartsList(cart, "AND");
        }

        /**
         * Returns a list of CARTS records from the database that match the parameters of the
         * CARTS object that is passed to the function. This function only searches attributes
         * of the CARTS object that are not null.
         * <p/>
         * This function uses the OR operator for inclusive searching.
         *
         * @param cart
         * @return
         */
        public ArrayList<Carts> getCartListUsingOrOperator(Carts cart) {
            return getCartsList(cart, "OR");
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

            //if(myDataBase != null)
                //myDataBase.close();

            super.close();

        }


        @Override
        protected void finalize() throws Throwable {
            this.close();
            super.finalize();
        }
    }
