package com.cs646.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManagerCompat;
    EditText editTextTitle, editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextMessage = findViewById(R.id.edit_text_message);

        Intent intent = getIntent();
        if(intent.getStringExtra("HI") != null)
            editTextTitle.setText(intent.getStringExtra("HI"));
        if(intent.getStringExtra("MESSAGE") != null)
            editTextMessage.setText(intent.getStringExtra("MESSAGE"));


    }

    public void sendOnChannel1(View v){

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.putExtra("HI","VK Title");
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(activityIntent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(editTextTitle.getText().toString())
                .setContentText(editTextMessage.getText().toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();
        notificationManagerCompat.notify(1, notification);

    }


    private Notification getNotification(PendingIntent pendingIntent){

       Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(editTextTitle.getText().toString())
                .setContentText(editTextMessage.getText().toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
               .build();

        return  notification;

    }


    public PendingIntent getPendingIntent(){
        Intent targetIntent = new Intent(this, MainActivity.class);
        targetIntent.putExtra("MESSAGE","This is my message from intent");
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(targetIntent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }

    private void scheduleBroadCaster(int delay){

        Intent broadCastIntent = new Intent(this, AlarmReceiver.class);
        broadCastIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        broadCastIntent.putExtra(AlarmReceiver.NOTIFICATION,getNotification(getPendingIntent()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //action on time
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void scheduleNotification10sec(View view){
        scheduleBroadCaster(10000);
    }
}
