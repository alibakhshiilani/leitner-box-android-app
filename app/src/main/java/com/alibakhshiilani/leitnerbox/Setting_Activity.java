package com.alibakhshiilani.leitnerbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.alibakhshiilani.leitnerbox.Gateway.InformationModel;
import com.alibakhshiilani.leitnerbox.Tools.Md5;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class Setting_Activity extends AppCompatActivity {


    private AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private Integer font;
    private SharedPreferences.Editor ed;
    private Button fontBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_file);
        final SharedPreferences shp = getApplicationContext().getSharedPreferences("nobegheloghat", Context.MODE_PRIVATE);
        boolean lockSwitch = shp.getBoolean("lockSwitch", false);
        SwitchCompat lockSwitchView = (SwitchCompat) findViewById(R.id.lockSwitch);
        lockSwitchView.setChecked(lockSwitch);
        lockSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ed = shp.edit();
                ed.putBoolean("lockSwitch", isChecked);
                ed.apply();
            }
        });
        boolean timerSwitch = shp.getBoolean("timerSwitch", true);
        SwitchCompat timerSwitchView = (SwitchCompat) findViewById(R.id.timerSwitch);
        timerSwitchView.setChecked(timerSwitch);

        timerSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ed = shp.edit();
                ed.putBoolean("timerSwitch", isChecked);
                ed.apply();
            }
        });
        font = shp.getInt("fontInt", 0);
        //Integer timerMinutes = shp.getInt("timerMinutes", 2);
        Integer red = shp.getInt("red", 244);
        Integer green = shp.getInt("green", 67);
        Integer blue = shp.getInt("blue", 54);

        Button changePasswordBtn = (Button)findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert = new AlertDialog.Builder(Setting_Activity.this, R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getLayoutInflater();
                final View myview = inflater.inflate(R.layout.change_pass_dialog, null);
                Button savePass = (Button) myview.findViewById(R.id.savePass);
                savePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText pass = (EditText) myview.findViewById(R.id.password);
                        String password = pass.getText().toString();

                        if(password.equals("")){
                            pass.setError("رمز عبور نمی تواند خالی باشد !");
                        }else if(password.length() < 4){
                            pass.setError("رمز عبور باید حداقل 4 کاراکتر باشد");
                        }else{
                            Md5 md5 = new Md5();
                            try {
                                password = md5.get(password);
                                InformationModel informationModel = new InformationModel(getApplicationContext(),null);
                                informationModel.setPassword(password);
                                OptionDialog.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"مشکلی پیش امده لطفا دوباره تلاش نمایید",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                Button cancelPass = (Button) myview.findViewById(R.id.cancelPass);
                cancelPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OptionDialog.dismiss();
                    }
                });
                OptionDialog.setView(myview);
                OptionDialog.show();
            }
        });



       // Button changeTimerBtn = (Button)findViewById(R.id.changeTimerBtn);
        //changeTimerBtn.setText(Integer.toString(timerMinutes));

        fontBtn = (Button)findViewById(R.id.changeFontBtn);
        if(font==0){
            fontBtn.setText("فونت 1");
        }else {
            fontBtn.setText("فونت 2");
        }

        fontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                font = shp.getInt("fontInt", 0);
                alert = new AlertDialog.Builder(Setting_Activity.this, R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getLayoutInflater();
                View myview = inflater.inflate(R.layout.font_radio_btn, null);
                final RadioButton fontRadio1 = (RadioButton) myview.findViewById(R.id.radioButton1);
                final RadioButton fontRadio2 = (RadioButton) myview.findViewById(R.id.radioButton2);
                Typeface font1 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/IRANSansWeb.ttf");
                Typeface font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/BHoma.ttf");
                TextView text1 = (TextView) myview.findViewById(R.id.fontText1);
                TextView text2 = (TextView) myview.findViewById(R.id.fontText2);
                text1.setTypeface(font1);
                text2.setTypeface(font2);
                if(font==0){
                    fontRadio1.setChecked(true);
                }else {
                    fontRadio2.setChecked(true);
                }
                Button saveFont = (Button) myview.findViewById(R.id.saveFont);
                saveFont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkedIndex = 0;
                        if(fontRadio2.isChecked()){
                            checkedIndex=1;
                        }
                        if(checkedIndex==0){
                            fontBtn.setText("فونت 1");
                        }else {
                            fontBtn.setText("فونت 2");
                        }
                        ed = shp.edit();
                        ed.putInt("fontInt", checkedIndex);
                        ed.apply();
                        OptionDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"برای اعمال تغییرات فونت برنامه را ببندید و دوباره باز کنید !",Toast.LENGTH_LONG).show();
                    }
                });
                OptionDialog.setView(myview);
                OptionDialog.show();
            }
        });

        final Button showColor = (Button)findViewById(R.id.saveColorButton);
        showColor.setBackgroundColor(Color.rgb(red,green,blue));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final ColorPicker cp = new ColorPicker(Setting_Activity.this, red, green, blue);



        showColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cp.show();
                Button okColor = (Button)cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            showColor.setBackgroundColor(Color.rgb(cp.getRed(),cp.getGreen(),cp.getBlue()));
                            getWindow().setNavigationBarColor(Color.rgb(cp.getRed(),cp.getGreen(),cp.getBlue()));
                            getWindow().setStatusBarColor(Color.rgb(cp.getRed(),cp.getGreen(),cp.getBlue()));
                        }else {
                            Toast.makeText(getApplicationContext(),"تغییر رنگ برنامه تنها در اندروید نسخه 5 یا بالاتر کار میکند !",Toast.LENGTH_LONG).show();
                        }
                        cp.dismiss();
                    }
                });
            }
        });


        /*changeTimerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alert = new AlertDialog.Builder(Setting_Activity.this, R.style.MyDialogTheme);
                OptionDialog = alert.create();
                LayoutInflater inflater =getLayoutInflater();
                final View myview = inflater.inflate(R.layout.number_picker_dialog, null);
                OptionDialog.setView(myview);
                OptionDialog.show();

            }
        });*/

    }

}

