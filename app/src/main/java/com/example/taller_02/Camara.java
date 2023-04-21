package com.example.taller_02;

import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class Camara extends AppCompatActivity {

    private static final int pic_id = 123;
    private static final int vid_id = 456;

    Button camera_open_id;
    Button gallery_open_id;
    ImageView click_image_id;
    VideoView video_view_id;
    Switch switch_toggle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camara);

        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        gallery_open_id = findViewById(R.id.pick_button);
        video_view_id = findViewById(R.id.click_video);
        switch_toggle_id = findViewById(R.id.switch_toggle);

        switch_toggle_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch is on, record video
                    Intent video_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(video_intent, vid_id);
                } else {
                    // Switch is off, take photo
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, pic_id);
                }
            }
        });

        camera_open_id.setOnClickListener(v -> {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, pic_id);
        });

        gallery_open_id.setOnClickListener(v -> {
            Intent gallery_intent = new Intent(Intent.ACTION_GET_CONTENT);
            gallery_intent.setType("image/*");
            startActivityForResult(gallery_intent, pic_id);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    click_image_id.setImageBitmap(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                click_image_id.setImageBitmap(photo);
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Photo", "Photo taken by Camera");
                click_image_id.setVisibility(View.VISIBLE);
                video_view_id.setVisibility(View.GONE);
            }
        } else if (requestCode == vid_id) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                video_view_id.setVideoURI(videoUri);
                click_image_id.setVisibility(View.GONE);
                video_view_id.setVisibility(View.VISIBLE);
                video_view_id.start();
            }
        }
    }
}
