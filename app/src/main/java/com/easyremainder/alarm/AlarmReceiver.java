package com.easyremainder.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.easyremainder.MainActivity;
import com.easyremainder.MyApplication;
import com.easyremainder.R;

public class AlarmReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

    String title = intent.getStringExtra("title");
    long timeStamp=intent.getLongExtra("time_stamp",0);
    int color = intent.getIntExtra("color",0);

    Intent resultIntent = new Intent(context, MainActivity.class);

    if (MyApplication.isActivityVisible()){
        resultIntent=intent;

    }
    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) timeStamp, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("EasyReminder");
        builder.setContentTitle(title);
        builder.setColor(context.getResources().getColor(color));

        builder.setSmallIcon(R.drawable.baseline_done_white_48);

        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags |=Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int)timeStamp,notification);

    }
}
