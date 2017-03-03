package com.example.kirill.test1;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;

public class PhotoHandler implements PictureCallback {

    private final Context context;

    PhotoHandler(Context context) {
        this.context = context;
    }

    // этот метод будет делать фото и сохранять его
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFileDir = getDir();

        // Если папки не существует и она на создалась то выводим сообщение об этом.
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            return;
        }

        // генерируем имя для фото
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String namePhoto = "Photo_" + date;
        String photoFile = namePhoto + ".jpg";

        String photoFilename = pictureFileDir.getPath() + File.separator + photoFile;
        File pictureFile = new File(photoFilename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            // записываем фото полученное в байтах
            fos.write(data);
            fos.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    // этот метод будет получать путь к папке где нужно сохранить фото
    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }
}

