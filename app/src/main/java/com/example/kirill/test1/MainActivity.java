package com.example.kirill.test1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;
import java.io.File;


public class MainActivity extends Activity {

    AlertDialog.Builder alertDialogMain;
    AlertDialog.Builder alertDialogBadVote;
    Context context;
    private static final int NOTIFY_ID = 0;
    private Camera camera;
    public static File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainDialog();
        initBadVoteDialog();
        initCamera();
    }
    public void initCamera(){
        // проверяем есть ли камера на устройстве
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "На этом устройстве не камеры.", Toast.LENGTH_LONG).show();
        } else {
            // получаем ID камеры
            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "Фронтальная камера не найдена.", Toast.LENGTH_LONG).show();
            } else {
                // открываем камеру для съемки
                camera = Camera.open(cameraId);
            }
        }
    }

    public void makePhoto() {
        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
        Toast.makeText(context, "Фото сделано",
                Toast.LENGTH_LONG).show();
    }

    // поиск камеры
    private int findFrontFacingCamera() {
        int cameraId = -1;

        // Поиск Фронтальной камеры
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            // тут вы указываете какую камеру использовать CAMERA_FACING_BACK или CAMERA_FACING_FRONT
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    public void initMainDialog(){
        String title = "Оцените качество обслуживания";
        String buttonStringGood = "Понравилось";
        String buttonStringBad = "Не понравилось";

        context = MainActivity.this;
        alertDialogMain = new AlertDialog.Builder(context);
        alertDialogMain.setTitle(title);
        alertDialogMain.setPositiveButton(buttonStringGood, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Хорошо",
                        Toast.LENGTH_LONG).show();
                makePhoto();
            }
        });
        alertDialogMain.setNegativeButton(buttonStringBad, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBadVote.show();
            }
        });

        alertDialogMain.setCancelable(true);
        alertDialogMain.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initBadVoteDialog() {
        final String[] badVotes = {"Нагрубили", "Долгое обслуживание", "Прочее"};
        String title = "Укажите причину плохого обслуживания";
        context = MainActivity.this;
        alertDialogBadVote = new AlertDialog.Builder(context);
        alertDialogBadVote.setTitle(title);
        alertDialogBadVote.setItems(badVotes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),
                        "Выбранная причина: " + badVotes[which],
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void firstDialogButtonClick(View view){
        alertDialogMain.show();
    }

    public void pushButtonClick(View view){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Напоминание")
                .setContentText("Текст сообщения");

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}

