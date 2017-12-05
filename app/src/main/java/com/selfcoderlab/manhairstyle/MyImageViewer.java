package com.selfcoderlab.manhairstyle;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import java.util.ArrayList;

import mywork.AppConstant;
import mywork.GridViewImageAdapter;
import mywork.Utils;

/**
 * Created by Selfcoderlab
 */
public class MyImageViewer extends Activity {


    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private AdView mAdView;
    private ImageView ic_back;
    private InterstitialAd mInterstitialAd;





    public static int dipsToPixels(Context context, float dips) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dips, context.getResources()
                        .getDisplayMetrics()));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_imageview);

        mAdView = (AdView) findViewById(R.id.ad_view);

        ic_back = (ImageView) findViewById(R.id.ic_img_back);
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if (isInternetAvailable()) {

            setupBannerAd();
            setupInterstialAd();

        } else {
            mAdView.setVisibility(View.GONE);
        }
        try {
            gridView = (GridView) findViewById(R.id.grid_view);
            utils = new Utils(MyImageViewer.this);
            // Initilizing Grid View
            InitilizeGridLayout();
            // loading all image paths from SD card
            imagePaths = utils.getFilePaths();
            // Gridview adapter
            adapter = new GridViewImageAdapter(MyImageViewer.this, imagePaths, columnWidth);
            // setting grid view adapter
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupInterstialAd() {
        mInterstitialAd = new InterstitialAd(MyImageViewer.this);
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

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
