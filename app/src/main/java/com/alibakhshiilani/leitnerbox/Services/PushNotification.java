package com.alibakhshiilani.leitnerbox.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.alibakhshiilani.leitnerbox.MainActivity;
import com.alibakhshiilani.leitnerbox.R;

import java.util.Timer;
import java.util.TimerTask;

public class PushNotification extends Service {

    private Timer mTimer;
    private Handler mHandler = new Handler();

    private static final int TIMER_INTERVAL = 5*60000; // 2 Minute
    private static final int TIMER_DELAY = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mTimer != null)
            mTimer = null;

        // Create new Timer
        mTimer = new Timer();

        // Required to Schedule DisplayToastTimerTask for repeated execution with an interval of `2 min`
        mTimer.scheduleAtFixedRate(new CheckNotifications(), TIMER_DELAY, TIMER_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel timer
        mTimer.cancel();
    }

    // Required to do some task
    // Here I just display a toast message "Hello world"
    private class CheckNotifications extends TimerTask {

        @Override
        public void run() {

            // Do something....

            /*mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String message = " اپلیکیشن اپ جعبه لایتنر یک وسیله کمک آموزشی است که می تواند فرآیند یادگیری شما را به طور کامل دگرگون کند. این نرم افزار بر اساس اصول علمی و تحقیقات دانشمند اتریشی سباستین لایتنر طراحی شده است.\n" +
                            "یکی از مشکلات اساسی دانش آموزان ، دانشجویان و شرکت کنندگان در امتحانات این است که هنگام مطالعه احساس می کنند مطلب را به خوبی فرا گرفته اند. اما وقتی پای امتحان به میان می آید متوجه میشوند که نکات مهمی را فراموش کرده اند.\n" +
                            "اپ جعبه لایتنر شما را همراهی می کند تا با به صفر رساندن احتمال فراموشی و جلوگیری از توهم دانستن مطالب را به حافظه بلند مدت خود منتقل نمایید و هیچگاه فراموش نکنید.\n" +
                            "قسمتهای مختلف این اپلیکیشن عبارتند از : 504 لغت کاملا ضروری، 1100 لغت که باید بدانید، 601 لغت که باید برای امتحان بدانید، 3500 لغت  ضروری برای آزمون GRE ، لغات کتابهای درسی پایه هفتم ، هشتم و نهم ، مکالمات روزمره انگلیسی، لغات عمومی آزمون کارشناسی ارشد تمام  رشته ها ، 8000 لغت تکمیلی کتاب 1100 ، ده هزار لغت استفاده شده در خبرها و مقالات ، اصطلاحات رایج در زبان انگلیسی ، واژگان تخصصی رشته های اقتصاد ، مدیریت ، حسابداری ، جامعه شناسی و ....\n" +
                            "چرا اپ جعبه لایتنر؟ ";
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.logo)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setContentTitle("تست ارسال پیام به کاربر")
                                    .setContentText(message);
                    //Vibration
                    mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                    //LED
                    mBuilder.setLights(Color.RED, 3000, 3000);

                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    getApplicationContext(),
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = 001;
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }
            });*/
        }
    }
}