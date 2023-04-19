package com.example.taller_02;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.example.taller_02.databinding.CamaraBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import utils.AlertUtils;


public class Camara extends AppCompatActivity {
    private static String TAG = Camara.class.getName();
    private CamaraBinding binding;

    //Definir el id del permiso
    private final int CAMERA_PERMISSION_ID = 101;
    private final int GALLERY_PERMISSION_ID = 102;
    String cameraPerm = Manifest.permission.CAMERA;
    String galeriaPerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    //Para mostrar imagen de la camara
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CamaraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.botontake.setOnClickListener(view -> {
            if(requestPermission(this, cameraPerm, CAMERA_PERMISSION_ID)){
                startCamera(binding.getRoot());
            }
        });
        binding.botonpick.setOnClickListener(view -> {
            if(requestPermission(this, galeriaPerm, GALLERY_PERMISSION_ID)){
                startGallery(binding.getRoot());
            }
        });
    }

    private boolean requestPermission(Activity context, String permission, int id) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    id);
            Log.w(TAG, "requestPermission: HOLA");
            return false;
        }
        return true;
    }

    private void initView(){
        if (ContextCompat.checkSelfPermission(this, cameraPerm)
                != PackageManager.PERMISSION_GRANTED){
            Log.e(TAG, "initView: no pude obtener el permiso :(");
            AlertUtils.indefiniteSnackbar(binding.getRoot(), getString(R.string.permission_denied_label));
        }else {
            Log.i(TAG, "initView: si pude obtener el permiso :)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_ID){
            startCamera(binding.getRoot());
        }
        if(requestCode == GALLERY_PERMISSION_ID){
            startGallery(binding.getRoot());
        }
    }

    public void startCamera(View view){
        if (ContextCompat.checkSelfPermission(this, cameraPerm)
                == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }else {
            AlertUtils.indefiniteSnackbar(binding.getRoot(), getString(R.string.permission_denied_label));
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CAMARA_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, String.format("Ruta: %s", currentPhotoPath));
        return image;
    }

    public void startGallery(View view){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage, GALLERY_PERMISSION_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_PERMISSION_ID:
                    binding.imageView.setImageURI(Uri.parse(currentPhotoPath));
                    Log.i(TAG, "onActivityResult: imagen campturada correctamente");
                    break;
                case GALLERY_PERMISSION_ID:
                    Uri imageUri = data.getData();
                    binding.imageView.setImageURI(imageUri);
                    Log.i(TAG, "onActivityResult: imagen cargada correctamente.");
                    break;
            }
        }
    }
}

