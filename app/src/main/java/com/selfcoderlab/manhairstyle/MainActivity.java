package com.selfcoderlab.manhairstyle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

import Utils.Consts;


public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView ic_gallery, ic_camera, ic_work, ic_share, ic_rate;
    protected static final int SELECT_GALLERY = 2;
    protected static final int SELECT_CAMERA = 1;
    private String fname, root;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean galleryopen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdView = (AdView) findViewById(R.id.ad_view);

        if (isInternetAvailable()) {

            setupBannerAd();
            mInterstitialAd = new InterstitialAd(MainActivity.this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_screen_ad_unit_id));

            AdRequest adRequestFull = new AdRequest.Builder().build();

            setinterStitial();

        } else {
            mAdView.setVisibility(View.GONE);
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                init();

            }
        } else {
            init();
        }

    }

    private void setinterStitial() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (galleryopen == false) {
                    AdRequest adRequestFull = new AdRequest.Builder().build();

                    mInterstitialAd.loadAd(adRequestFull);
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            // Full screen advertise will show only after loading complete
                            mInterstitialAd.show();
                            Log.e("1minad", "1minad");
                        }
                    });
                } else {
                    setinterStitial();
                }
            }
        }, 10000);
    }

   /* private void setupInterstialAd() {
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_screen_ad_unit_id));

        AdRequest adRequestFull = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequestFull);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Full screen advertise will show only after loading complete
                mInterstitialAd.show();
            }
        });*/
//}

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

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void init() {


        ic_gallery = (ImageView) findViewById(R.id.ic_gallery);
        ic_camera = (ImageView) findViewById(R.id.ic_camera);
        ic_work = (ImageView) findViewById(R.id.ic_work);
        ic_share = (ImageView) findViewById(R.id.ic_share);
        ic_rate = (ImageView) findViewById(R.id.ic_rate);

        ic_gallery.setOnClickListener(this);
        ic_camera.setOnClickListener(this);
        ic_work.setOnClickListener(this);
        ic_share.setOnClickListener(this);
        ic_rate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_gallery:
                /*Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);*/

                galleryopen = true;
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_GALLERY);
                break;
            case R.id.ic_camera:
                // root = this.getCacheDir();
                galleryopen = true;
                fname = "Style" + System.currentTimeMillis() + ".jpg";

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                startActivityForResult(cameraIntent, SELECT_CAMERA);
                break;
            case R.id.ic_work:
                Intent intent = new Intent(MainActivity.this, MyImageViewer.class);
                startActivity(intent);
                break;
            case R.id.ic_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.selfcoderlab.manhairstyle");
                startActivity(Intent.createChooser(share, "Share link!"));
                break;
            case R.id.ic_rate:

                Intent rateintent = new Intent(Intent.ACTION_VIEW);
                rateintent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.selfcoderlab.manhairstyle"));
                startActivity(rateintent);
                break;
        }

    }

    private Uri getImageUri() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), fname);

        Uri tempURI = Uri.fromFile(file);
        Log.e("URI", ">> " + tempURI);
        return tempURI;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_GALLERY && resultCode == RESULT_OK && null != data) {
            try {
                galleryopen = false;
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Consts.img_Bitmap = null;
                Consts.img_Bitmap = BitmapFactory.decodeFile(picturePath);
                Intent intent = new Intent(MainActivity.this, CropActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_CAMERA) {
            try {
                galleryopen = false;
/*
                FileInputStream in = new FileInputStream("/sdcard/test2.png");
                BufferedInputStream buf = new BufferedInputStream(in);
                byte[] bMapArray= new byte[buf.available()];
                buf.read(bMapArray);
                Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length*/

                Uri selectedImageUri = getImageUri();
                String uri = selectedImageUri.toString();


                // Bitmap photo = (Bitmap) data.getExtras().get("data");
                Consts.img_Bitmap = null;
                //Consts.img_Bitmap = (Bitmap) data.getExtras().get("data");
                Consts.img_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                ExifInterface ei = new ExifInterface(selectedImageUri.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Consts.img_Bitmap = rotateImage(Consts.img_Bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Consts.img_Bitmap = rotateImage(Consts.img_Bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Consts.img_Bitmap = rotateImage(Consts.img_Bitmap, 270);
                        break;

                    default:

                        break;
                }

                Intent intent = new Intent(MainActivity.this, CropActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                galleryopen = false;
                e.printStackTrace();
            }
        } else {
            galleryopen = false;
            Toast.makeText(MainActivity.this, "you have not selected any Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }
}
