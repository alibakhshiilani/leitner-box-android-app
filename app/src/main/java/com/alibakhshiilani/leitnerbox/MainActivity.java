package com.alibakhshiilani.leitnerbox;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.Fragments.FragmentAbout;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentCategoryAdd;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentCategoryShow;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentContact;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentExcel;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentExport;
import com.alibakhshiilani.leitnerbox.Fragments.FragmentImport;
import com.alibakhshiilani.leitnerbox.Fragments.HomePageFragment;
import com.alibakhshiilani.leitnerbox.Gateway.CartModel;
import com.alibakhshiilani.leitnerbox.Gateway.CategoryModel;
import com.alibakhshiilani.leitnerbox.util.IabHelper;
import com.alibakhshiilani.leitnerbox.util.IabResult;
import com.alibakhshiilani.leitnerbox.util.Inventory;
import com.alibakhshiilani.leitnerbox.util.Purchase;

import co.ronash.pushe.Pushe;

/**
 * Created by Ali Bakhshi on 5/2/2017.
 * www.ali-bakhshi.ir
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* Start Bazzar Payment */
    // Debug tag, for logging
    static final String TAG = "PAYMENTINFO";
    private ProgressDialog Dialog;
    // Does the user have the premium upgrade?
    public static boolean userIsFull = false;
    public static boolean userIsMasters = false;
    public static boolean userIsLearners = false;
    public static boolean userIsSchools = false;
    public static boolean userIsTechnical = false;
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    IabHelper mHelper;
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    public static final String SKU_FULL = "full";
    public static final String SKU_MASTERS = "masters";
    public static final String SKU_LEARNERS = "learners";
    public static final String SKU_SCHOOLS = "schools";
    public static final String SKU_TECHNICAL = "technical";
    /* End Bazzar Payment */

    private DrawerLayout drawer;
    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    public static FloatingActionButton fab;
    public static FloatingActionButton fabExcel;
    private FragmentTransaction manager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Cursor categories;
    private CategoryModel category;
    private CartModel cart;
    private android.os.Handler mHandler;
    public SharedPreferences shp;
    public SharedPreferences shp2;
    private SharedPreferences.Editor ed;

    private ProgressDialog DialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shp = getApplicationContext().getSharedPreferences("nobegheloghat", Context.MODE_PRIVATE);

        String loginStatus="";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                loginStatus= "";
            } else {
                loginStatus= extras.getString("loginStatus");
            }
        } else {
            loginStatus= (String) savedInstanceState.getSerializable("loginStatus");
        }

        //InformationModel informationModel = new InformationModel(getApplicationContext(),null);
        //boolean loginStatusCheck = informationModel.hasPassword();
        boolean loginStatusCheck = shp.getBoolean("lockSwitch", false);
        /*fab = (FloatingActionButton) findViewById(R.id.fab);
        fabExcel = (FloatingActionButton) findViewById(R.id.excelFab);*/

        if((loginStatus==null || !loginStatus.equals("yes")) && loginStatusCheck){
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginIntent);
            finish();
            return;
        }else{
            this.category = new CategoryModel(getApplicationContext(),null);
            cart = new CartModel(getApplicationContext(),null);
            manager = getSupportFragmentManager().beginTransaction();
            manager.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            HomePageFragment homePageFragment = new HomePageFragment();
            manager.replace(R.id.mainframe,homePageFragment).commit();
        }

        this.verifyStoragePermissions(this);
        Pushe.initialize(this,true);
        mHandler = new android.os.Handler();

        DialogProgress = new ProgressDialog(this);
        Dialog  = new ProgressDialog(this);

        /*Intent intentMyService = new Intent(getApplicationContext(), PushNotification.class);
        getApplicationContext().startService(intentMyService);*/



        int fontNumber = shp.getInt("fontInt", 0);
        String fontName;
        if(fontNumber==0){
            fontName = "IRANSansWeb";
        }else{
            fontName = "BHoma";
        }
        // Add Font To Program
        OverWriteFont.setDefaultFont(this, "DEFAULT", "fonts/"+fontName+".ttf");
        OverWriteFont.setDefaultFont(this, "MONOSPACE", "fonts/"+fontName+".ttf");
        OverWriteFont.setDefaultFont(this, "SERIF", "fonts/"+fontName+".ttf");
        OverWriteFont.setDefaultFont(this, "SANS_SERIF","fonts/"+fontName+".ttf");




        ImageView searchBtnCart = (ImageView) findViewById(R.id.searchBtnCart);
        searchBtnCart.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                                                 MainActivity.this.startActivity(searchIntent);
                                             }
        });

        TextView titleBarText = (TextView) findViewById(R.id.titleBarText);
        OverWriteFont.setTitleFont(this,"fonts/IRANSansWeb.ttf",titleBarText);

        TextView cartListTitle = (TextView) findViewById(R.id.cartListTitle);
        if(cartListTitle != null){
            OverWriteFont.setTitleFont(this,"fonts/IRANSansWeb.ttf",cartListTitle);
        }
        // End Font To Program

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        LayoutInflater inflater =getLayoutInflater();
        View myview = inflater.inflate(R.layout.prompt_dialog, null);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));

        /*fabExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                FragmentExcel fragmentExcel = new FragmentExcel();
                ft.addToBackStack("tag").replace(R.id.mainframe, fragmentExcel);
                ft.commit();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getLayoutInflater();
                final View myview = inflater.inflate(R.layout.prompt_dialog, null);
                OptionDialog.setView(myview);
                final EditText edittext = new EditText(MainActivity.this);
                Button cancelCart = (Button) myview.findViewById (R.id.cancelCart);
                cancelCart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        OptionDialog.dismiss();
                    }
                });
                // spinner start
                categories = category.getAllCategory(-2);
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
                categories.close();

                Button saveCart = (Button) myview.findViewById (R.id.saveCart);
                saveCart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText onCard = (EditText) myview.findViewById (R.id.onCard);
                        EditText backCard = (EditText) myview.findViewById (R.id.backCard);
                        EditText cartDesc = (EditText) myview.findViewById (R.id.cartDesc);
                        Spinner categoriesSpinner2 = (Spinner) myview.findViewById(R.id.categories_spinner);
                        int catPosition = categoriesSpinner2.getSelectedItemPosition();
                        if(onCard.getText().toString().equals("") || backCard.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this,"پر کردن متن روی کارت و متن پشت کارت الزامی است",Toast.LENGTH_LONG).show();
                        }else{
                            cart.saveCarts(onCard.getText().toString(),backCard.getText().toString(),cartDesc.getText().toString(), FragmentCategoryShow.categoryId);
                            OptionDialog.dismiss();
                            Toast.makeText(MainActivity.this,"کارت با موفقیت افزوده شد",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                Button excelAdd = (Button) myview.findViewById(R.id.excelAdd);

                excelAdd.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        OptionDialog.dismiss();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        FragmentExcel fragmentExcel = new FragmentExcel();
                        ft.addToBackStack("tag").replace(R.id.mainframe, fragmentExcel);
                        ft.commit();

                    }
                });

                OptionDialog.show();
            }
        });*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        shp2 = getApplicationContext().getSharedPreferences("pardakhtnabeghe", Context.MODE_PRIVATE);

        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCrS9G5jjmXxPgyUmWlFaZY7JIRKyvLN8R+yWyAyRhxQd6Cdjv4VnoONVXBMqQfopBxfRlw3thptf9B8MltCJNNciDa5HnvHSOmHYg6M1n3QfL2+X7zXGVQrD9tJgyXvYuXNHYojMRdGujIH+lask/UoYAn7mxOnomry45M4tZYXh041Bb/SoJ7G9qfPWm+uiFYzZxPaXtzdBXfdL+CVBFEKgh1QclL3YUFH2pmm3MCAwEAAQ==";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        Log.d(TAG, "Starting setup.");
        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                        userIsFull = shp2.getBoolean("userIsFull", false);
                        userIsMasters = shp2.getBoolean("userIsMasters", false);
                        userIsTechnical = shp2.getBoolean("userIsTechnical", false);
                        userIsLearners = shp2.getBoolean("userIsLearners", false);
                        userIsSchools = shp2.getBoolean("userIsSchools", false);
                        return;
                    }
                    /*ArrayList<String> myList = new ArrayList<>();
                    myList.add(SKU_FULL);
                    myList.add(SKU_LEARNERS);
                    myList.add(SKU_MASTERS);
                    myList.add(SKU_SCHOOLS);
                    myList.add(SKU_TECHNICAL);*/
                    // Hooray, IAB is fully set up!

                    showSimpleProgressDialog("در حال بارگزاری","لطفا منتظر بمانید");

                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                userIsFull = shp2.getBoolean("userIsFull", false);
                userIsMasters = shp2.getBoolean("userIsMasters", false);
                userIsTechnical = shp2.getBoolean("userIsTechnical", false);
                userIsLearners = shp2.getBoolean("userIsLearners", false);
                userIsSchools = shp2.getBoolean("userIsSchools", false);
                removeSimpleProgressDialog();
                return;
            }
            else {
                Log.d(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?

                ed = shp2.edit();

                userIsFull = inventory.hasPurchase(SKU_FULL);
                ed.putBoolean("userIsFull", userIsFull);

                userIsLearners = inventory.hasPurchase(SKU_LEARNERS);
                ed.putBoolean("userIsLearners", userIsFull);

                userIsMasters = inventory.hasPurchase(SKU_MASTERS);
                ed.putBoolean("userIsMasters", userIsMasters);

                userIsSchools = inventory.hasPurchase(SKU_SCHOOLS);
                ed.putBoolean("userIsSchools", userIsSchools);

                userIsTechnical = inventory.hasPurchase(SKU_TECHNICAL);
                ed.putBoolean("userIsTechnical", userIsTechnical);

                ed.apply();

                // update UI accordingly

                Log.d(TAG, "User is " + (userIsFull ? "PREMIUM" : "NOT PREMIUM"));
            }

            removeSimpleProgressDialog();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            boolean resultStatus = result.isFailure();
            if(!resultStatus){
                ed = shp2.edit();
            }

            if (resultStatus) {
                Log.d(TAG, "Error purchasing: " + result);
                Toast.makeText(getApplicationContext(),"پرداخت نا موفق بود !",Toast.LENGTH_LONG).show();
                return;
            }
            else if (purchase.getSku().equals(SKU_FULL)) {
                Toast.makeText(getApplicationContext(),"اکنون میتوانید به تمامی گروه های اپ جعبه لایتنر دسترسی داشته باشید",Toast.LENGTH_LONG).show();
                userIsFull=true;
                ed.putBoolean("userIsFull", userIsFull);
            }else if (purchase.getSku().equals(SKU_LEARNERS)) {
                Toast.makeText(getApplicationContext(),"اکنون میتوانید به گروه زبان آموزان دسترسی داشته باشید",Toast.LENGTH_LONG).show();
                userIsLearners=true;
                ed.putBoolean("userIsLearners", userIsLearners);
            }else if (purchase.getSku().equals(SKU_MASTERS)) {
                Toast.makeText(getApplicationContext(),"اکنون میتوانید به گروه کارشناسی ارشد دسترسی داشته باشید",Toast.LENGTH_LONG).show();
                userIsMasters=true;
                ed.putBoolean("userIsLearners", userIsMasters);
            }else if (purchase.getSku().equals(SKU_SCHOOLS)) {
                Toast.makeText(getApplicationContext(),"اکنون میتوانید به گروه واژگان مدرسه دسترسی داشته باشید",Toast.LENGTH_LONG).show();
                userIsSchools=true;
                ed.putBoolean("userIsSchools", userIsSchools);
            }else if (purchase.getSku().equals(SKU_TECHNICAL)) {
                Toast.makeText(getApplicationContext(),"اکنون میتوانید به گروه زبان تخصصی دسترسی داشته باشید",Toast.LENGTH_LONG).show();
                userIsTechnical=true;
                ed.putBoolean("userIsTechnical", userIsTechnical);
            }

            if(!resultStatus){
                ed.apply();
            }

            if(!resultStatus){
                String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCrS9G5jjmXxPgyUmWlFaZY7JIRKyvLN8R+yWyAyRhxQd6Cdjv4VnoONVXBMqQfopBxfRlw3thptf9B8MltCJNNciDa5HnvHSOmHYg6M1n3QfL2+X7zXGVQrD9tJgyXvYuXNHYojMRdGujIH+lask/UoYAn7mxOnomry45M4tZYXh041Bb/SoJ7G9qfPWm+uiFYzZxPaXtzdBXfdL+CVBFEKgh1QclL3YUFH2pmm3MCAwEAAQ==";

                mHelper = new IabHelper(MainActivity.this, base64EncodedPublicKey);

                Log.d(TAG, "queryInventoryAsync");
                try {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        public void onIabSetupFinished(IabResult result) {
                            Log.d(TAG, "Setup finished.");

                            if (!result.isSuccess()) {
                                // Oh noes, there was a problem.
                                Log.d(TAG, "Problem setting up In-app Billing: " + result);
                                userIsFull = shp2.getBoolean("userIsFull", false);
                                userIsMasters = shp2.getBoolean("userIsMasters", false);
                                userIsTechnical = shp2.getBoolean("userIsTechnical", false);
                                userIsLearners = shp2.getBoolean("userIsLearners", false);
                                userIsSchools = shp2.getBoolean("userIsSchools", false);
                                return;
                            }
                    /*ArrayList<String> myList = new ArrayList<>();
                    myList.add(SKU_FULL);
                    myList.add(SKU_LEARNERS);
                    myList.add(SKU_MASTERS);
                    myList.add(SKU_SCHOOLS);
                    myList.add(SKU_TECHNICAL);*/
                            // Hooray, IAB is fully set up!

                            showSimpleProgressDialog("در حال بارگزاری","لطفا منتظر بمانید");

                            mHelper.queryInventoryAsync(mGotInventoryListener);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeAppButtonClicked(long id) {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
        /*Dialog.setMessage("در حال بارگزاری اطلاعات ...");
        Dialog.setCanceledOnTouchOutside(false);
        Dialog.setCancelable(false);
        Dialog.show();*/

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        String Purchase;

        Log.i("onUpgradeAppButton",Long.toString(id));

        if(id==1){
            Purchase = SKU_SCHOOLS;
        }else if(id==4){
            Purchase = SKU_LEARNERS;
        }else if(id==6){
            Purchase = SKU_MASTERS;
        }else if(id==7){
            Purchase = SKU_TECHNICAL;
        }else {
            Purchase = SKU_FULL;
        }

        try{
            mHelper.launchPurchaseFlow(this, Purchase, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"مشکلی در اتصال به بازار پیش امده , لطفا مطمئن شوید بازار نصب است و اینترنت متصل می باشد",Toast.LENGTH_LONG).show();
            //Dialog.dismiss();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Dialog.dismiss();

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mHelper != null){
                mHelper.dispose();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mHelper = null;
        }
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                alert = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getLayoutInflater();
                final View myview = inflater.inflate(R.layout.confirm_exit, null);
                OptionDialog.setView(myview);
                Button no = (Button) myview.findViewById (R.id.no);
                no.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        OptionDialog.dismiss();
                    }
                });
                // spinner end
                Button yes = (Button) myview.findViewById (R.id.yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        OptionDialog.dismiss();
                        MainActivity.super.onBackPressed();
                    }
                });
                OptionDialog.show();
            }else{
                MainActivity.super.onBackPressed();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_setting) {

                    Intent myIntent = new Intent(MainActivity.this, Setting_Activity.class);
                    //myIntent.putExtra("key", "test"); //Optional parameters
                    MainActivity.this.startActivity(myIntent);

                    //manager.beginTransaction().replace(R.id.mainframe,fragmentTwo).commit();

                } else if (id == R.id.nav_categories) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    HomePageFragment homePageFragment = new HomePageFragment();
                    ft.addToBackStack("tag").replace(R.id.mainframe, homePageFragment);
                    ft.commit();


                } else if (id == R.id.nav_categories_add) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentCategoryAdd fragmentCategoryAdd = new FragmentCategoryAdd();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentCategoryAdd);
                    ft.commit();

                }  else if (id == R.id.nav_contact) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentContact fragmentContact = new FragmentContact();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentContact);
                    ft.commit();

                }else if (id == R.id.nav_about) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentAbout fragmentAbout = new FragmentAbout();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentAbout);
                    ft.commit();

                }else if (id == R.id.nav_export) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentExport fragmentExport = new FragmentExport();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentExport);
                    ft.commit();

                }else if (id == R.id.nav_import) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentImport fragmentImport = new FragmentImport();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentImport);
                    ft.commit();

                }else if (id == R.id.nav_excel) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    FragmentExcel fragmentExcel = new FragmentExcel();
                    ft.addToBackStack("tag").replace(R.id.mainframe, fragmentExcel);
                    ft.commit();

                }
            }
        }, 300);

        drawer.closeDrawer(GravityCompat.END);
        return true;
    }



    public void showSimpleProgressDialog(String title,
                                         String msg) {
        DialogProgress.setTitle(title);
        DialogProgress.setMessage(msg);
        DialogProgress.setCanceledOnTouchOutside(false);
        DialogProgress.show();
    }

    public void removeSimpleProgressDialog() {
        DialogProgress.dismiss();
    }


}
