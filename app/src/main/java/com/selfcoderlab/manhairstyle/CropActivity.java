package com.selfcoderlab.manhairstyle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.theartofdev.edmodo.cropper.CropImageView;

import Utils.Consts;

public class CropActivity extends Activity {
    private AdView mAdView;
    CropImageView cropImageView;
    //ImageView cropImageView;
    TextView btn_ok;
    ImageView iv_left;
    ImageView iv_right;
    Bitmap flippedBitmap;
    ImageView iv_flip_vertical;
    ImageView iv_flip_horizontal;
    Matrix mat;
    int height;
    int width;
    ImageView ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        //cropImageView = (ImageView) findViewById(R.id.cropImageView);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        iv_flip_vertical = (ImageView) findViewById(R.id.iv_flip_vertical);
        iv_flip_horizontal = (ImageView) findViewById(R.id.iv_flip_horizontal);
        mat = new Matrix();
        mAdView = (AdView) findViewById(R.id.ad_view);
        ic_back = (ImageView) findViewById(R.id.ic_img_back);
        cropImageView.setImageBitmap(Consts.img_Bitmap);
        cropImageView.setFixedAspectRatio(false);

        if (isInternetAvailable()) {

            setupBannerAd();
        } else {
            mAdView.setVisibility(View.GONE);
        }

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mat.postRotate(-90);
                flippedBitmap = Bitmap.createBitmap(Consts.img_Bitmap, 0, 0,
                        Consts.img_Bitmap.getWidth(), Consts.img_Bitmap.getHeight(), mat, true);
                cropImageView.setImageBitmap(flippedBitmap);
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mat.postRotate(90);
                flippedBitmap = Bitmap.createBitmap(Consts.img_Bitmap, 0, 0,
                        Consts.img_Bitmap.getWidth(), Consts.img_Bitmap.getHeight(), mat, true);
                cropImageView.setImageBitmap(flippedBitmap);

            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Consts.main_Bitmap = cropImageView.getCroppedImage();
               // Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
               // Consts.main_Bitmap = ((BitmapDrawable)cropImageView.getDrawable()).getBitmap();
                Intent i = new Intent(CropActivity.this, EditorActivity.class);
                startActivity(i);
            }
        });
        iv_flip_horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Consts.img_Bitmap != null) {
                    width = Consts.img_Bitmap.getWidth();
                    height = Consts.img_Bitmap.getHeight();
                    mat.preScale(1, -1);

                    try {
                        flippedBitmap = Bitmap.createBitmap(Consts.img_Bitmap, 0, 0, width,
                                height, mat, false);

                        cropImageView.setImageBitmap(flippedBitmap);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });
        iv_flip_vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Consts.img_Bitmap != null) {
                    width = Consts.img_Bitmap.getWidth();
                    height = Consts.img_Bitmap.getHeight();
                    mat.preScale(-1, 1);
                    try {
                        flippedBitmap = Bitmap.createBitmap(Consts.img_Bitmap, 0, 0, width,
                                height, mat, true);

                        cropImageView.setImageBitmap(flippedBitmap);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setupBannerAd() {
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        /*AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();*/


        mAdView.loadAd(adRequest);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
