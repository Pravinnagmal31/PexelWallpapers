package com.pravin.pexelwallapapers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;

import java.io.IOException;
import java.util.UUID;


public class FullScreenActivity extends AppCompatActivity implements View.OnClickListener {
    String originalUrl="";
    ZoomageView photoView;
    ProgressBar progress_circular;
    ImageButton btnDownloadWallpaper;
    Button btnSetWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

       // getSupportActionBar().hide();
        Intent intent =  getIntent();
        originalUrl = intent.getStringExtra("originalUrl");
        photoView = findViewById(R.id.photoView);
        btnDownloadWallpaper = findViewById(R.id.btnDownloadWallpaper);
        progress_circular = findViewById(R.id.progress_circular);
        btnSetWallpaper = findViewById(R.id.btnSetWallpaper);
        Glide.with(this)
                .load(originalUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // log exception
                      //  Log.e("TAG", "Error loading image", e);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progress_circular.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);



        btnDownloadWallpaper.setOnClickListener(this);
        btnSetWallpaper.setOnClickListener(this);


    }

    public void SetWallpaperEvent() {

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Bitmap bitmap  = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

     void downloadWallpaperEvent() {

        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(originalUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,UUID.randomUUID().toString()+".jpg");
//         request.setDestinationInExternalFilesDir(FullScreenActivity.this,
//                 Environment.DIRECTORY_DOWNLOADS,
//                 UUID.randomUUID().toString()+".jpg"
//         );
     //   request.setMimeType("*/*");


        downloadManager.enqueue(request);
        Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show();
    }


    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnDownloadWallpaper:
                if(isWriteStoragePermissionGranted()){
                    downloadWallpaperEvent();
                }
                break;
            case R.id.btnSetWallpaper:
                SetWallpaperEvent();
                break;

        }

    }
}