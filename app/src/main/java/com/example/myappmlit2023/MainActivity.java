package com.example.myappmlit2023;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    public static int REQUEST_CAMERA = 111;
    public static int REQUEST_GALLERY = 222;
    public TextView txtResults;
    public ImageView mImageView;
    private Bitmap mSelectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResults = findViewById(R.id.txtresults);
        mImageView = findViewById(R.id.image_view);

    }
    public void abrirGaleria (View view){
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_GALLERY);
    }

    public void camara(View view) {
        launchCamera();
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            handleImageCapture(requestCode, data);
        }
    }

    private void handleImageCapture(int requestCode, Intent data) {
        try {
            mSelectedImage = requestCode == REQUEST_CAMERA ? (Bitmap) data.getExtras().get("data")
                    : MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            displayCapturedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayCapturedImage() {
        mImageView.setImageBitmap(mSelectedImage);
    }
    public void scaner(View v) {
        if (mSelectedImage != null) {
            scanImageForQR();
        } else {
            txtResults.setText("No image to scan");
        }
    }

    private void scanImageForQR() {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();
        processImage(scanner, image);
    }

    private void processImage(BarcodeScanner scanner, InputImage image) {
        scanner.process(image)
                .addOnSuccessListener(this::displayBarcodes)
                .addOnFailureListener(e -> txtResults.setText("Error al procesar imagen"));
    }

    private void displayBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String value = barcode.getDisplayValue();
            txtResults.setText(value);
        }
    }
}