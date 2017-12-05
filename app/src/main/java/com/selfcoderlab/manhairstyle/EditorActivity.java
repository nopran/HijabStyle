package com.selfcoderlab.manhairstyle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Utils.Consts;

public class EditorActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    boolean changes = false;
    boolean final_save = true;

    RelativeLayout main_layout;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private static final String TAG = "Touch";
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    int mode = NONE;
    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    float scale = 0;
    float d = 0f;
    float newRot = 0f;
    float[] lastEvent = new float[0];
    HorizontalAdapter horizontalAdapter;

    AdView mAdView;
    private Bitmap temp_save;
    private RecyclerView recylerView;
    String clicked = "";
    boolean saved = false;
    private FrameLayout main_frame, img_frame;
    private boolean isStyleVisible = false;
    ImageView main_image;
    TextView edit_title;
    private ImageView ic_styles, ic_colors, ic_save, ic_flip, ic_clean, ic_share, ic_back;
    private RelativeLayout styles, colors;
    private ImageView ic_hair, ic_mucch, ic_beard, ic_cap, ic_spec, hair_img;
    private GridView gridView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mAdView = (AdView) findViewById(R.id.ad_view);

        if (isInternetAvailable()) {

            setupBannerAd();
        } else {
            mAdView.setVisibility(View.GONE);
        }
        init();

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

    private void init() {

        hair_img = (ImageView) findViewById(R.id.hair_image);
        ic_beard = (ImageView) findViewById(R.id.ic_beard);
        ic_cap = (ImageView) findViewById(R.id.ic_cap);
        ic_spec = (ImageView) findViewById(R.id.ic_spec);
        ic_mucch = (ImageView) findViewById(R.id.ic_mucch);
        ic_hair = (ImageView) findViewById(R.id.ic_hair);
        main_image = (ImageView) findViewById(R.id.main_image);
        edit_title = (TextView) findViewById(R.id.edit_title);
        ic_styles = (ImageView) findViewById(R.id.ic_styles);
        ic_colors = (ImageView) findViewById(R.id.ic_colors);
        ic_save = (ImageView) findViewById(R.id.ic_save);
        ic_flip = (ImageView) findViewById(R.id.ic_flip);
        ic_clean = (ImageView) findViewById(R.id.ic_clean);
        ic_share = (ImageView) findViewById(R.id.ic_share);
        ic_back = (ImageView) findViewById(R.id.ic_back);

        recylerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);

        styles = (RelativeLayout) findViewById(R.id.styles);
        colors = (RelativeLayout) findViewById(R.id.colors);
        gridView = (GridView) findViewById(R.id.gridView);
        main_frame = (FrameLayout) findViewById(R.id.main_frame);
        img_frame = (FrameLayout) findViewById(R.id.img_frame);
        main_layout = (RelativeLayout) findViewById(R.id.main_layout);
        Typeface face = Typeface.createFromAsset(getAssets(), "alex.ttf");
        edit_title.setTypeface(face);

        ic_styles.setOnClickListener(this);
        ic_colors.setOnClickListener(this);
        ic_hair.setOnClickListener(this);
        ic_mucch.setOnClickListener(this);
        ic_beard.setOnClickListener(this);
        ic_cap.setOnClickListener(this);
        ic_spec.setOnClickListener(this);
        ic_save.setOnClickListener(this);
        ic_flip.setOnClickListener(this);
        ic_clean.setOnClickListener(this);
        ic_share.setOnClickListener(this);
        ic_back.setOnClickListener(this);

        hair_img.setOnTouchListener(this);



        if (Consts.main_Bitmap != null) {
            main_image.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(">>", "1> " + main_image.getX() + "\n2> " + main_image.getY() + "\n3> " + main_image.getWidth() + "\n4> " + main_image.getHeight() + "\n5> " + Consts.main_Bitmap.getWidth() + "\n6> " + Consts.main_Bitmap.getHeight());

                }
            });

            main_image.setImageBitmap(Consts.main_Bitmap);


        } else {
            Toast.makeText(EditorActivity.this, "Please Select image", Toast.LENGTH_LONG).show();
        }


        String[] colorsTxt = getApplicationContext().getResources().getStringArray(R.array.colors);
        List<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < colorsTxt.length; i++) {
            int newColor = Color.parseColor(colorsTxt[i]);
            colors.add(newColor);
        }
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(EditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recylerView.setLayoutManager(horizontalLayoutManagaer);
        horizontalAdapter = new HorizontalAdapter(colors);
        recylerView.setAdapter(horizontalAdapter);
            }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_styles:
                styles.setVisibility(View.VISIBLE);
                colors.setVisibility(View.INVISIBLE);
                break;
            case R.id.ic_colors:
                styles.setVisibility(View.INVISIBLE);
                colors.setVisibility(View.VISIBLE);

                break;
            case R.id.ic_hair:
                hairStyles("hair");
                break;
            case R.id.ic_mucch:
                hairStyles("mucch");
                break;
            case R.id.ic_beard:
                hairStyles("beard");
                break;
            case R.id.ic_spec:
                hairStyles("spec");
                break;
            case R.id.ic_cap:
                hairStyles("cap");
                break;
            case R.id.ic_back:
                backMethod();
                break;
            case R.id.ic_clean:
                if (changes == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                    builder.setMessage("Do you want to revert all Changes");
                    builder.setTitle("Man HairStyle");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            main_image.setImageBitmap(Consts.main_Bitmap);
                            hair_img.setVisibility(View.GONE);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog d = builder.create();
                    d.show();
                }
                break;
            case R.id.ic_flip:
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                // Bitmap bitmap = ((GlideBitmapDrawable)hair_img.getDrawable().getCurrent()).getBitmap();
                if (hair_img.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap = ((BitmapDrawable) hair_img.getDrawable()).getBitmap();
                    hair_img.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                }
                break;
            case R.id.ic_share:
                shareImage();
                break;
            case R.id.ic_save:
                if (isInternetAvailable()) {

                    setupInterstialAd();
                }
                saved = true;

                saveImage("save");


                break;

        }

    }

    private void setupInterstialAd() {
        mInterstitialAd = new InterstitialAd(EditorActivity.this);
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

    private void backMethod() {

        if (final_save == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
            builder.setMessage("Do you want back Without Save Work");
            builder.setTitle("Man HairStyle");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });
            AlertDialog d = builder.create();
            d.show();
        } else {
            finish();
        }
    }


    private void shareImage() {

        try {
            saved = true;

            img_frame.setDrawingCacheEnabled(true);
            img_frame.buildDrawingCache();
            img_frame.buildDrawingCache(true);


            img_frame.measure(View.MeasureSpec.makeMeasureSpec(main_image.getWidth(), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(main_image.getHeight(), View.MeasureSpec.UNSPECIFIED));


            Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache(), (int) main_image.getX(), (int) main_image.getY(), main_image.getWidth(), main_image.getHeight());

            //img_frame.layout(0, 0, main_image.getMeasuredWidth(), main_image.getMeasuredHeight());

           // Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache());
            img_frame.setDrawingCacheEnabled(false);
            main_image.setImageBitmap(b);

            hair_img.setVisibility(View.GONE);
            String path = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            path = "Style" + path + ".jpg";

            File file = new File(this.getCacheDir(), path);
            FileOutputStream fOut = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImage(String tag) {

        img_frame.setDrawingCacheEnabled(true);
        img_frame.buildDrawingCache();
        main_image.setDrawingCacheEnabled(true);
        main_image.buildDrawingCache();


        img_frame.measure(View.MeasureSpec.makeMeasureSpec(main_image.getWidth(), View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(main_image.getHeight(), View.MeasureSpec.UNSPECIFIED));


        Bitmap b1 = Bitmap.createBitmap(img_frame.getDrawingCache(), (int) main_image.getX(), (int) main_image.getY(), main_image.getWidth(), main_image.getHeight());
        //  Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache());


        main_image.setImageBitmap(b1);
        img_frame.destroyDrawingCache();
        main_image.destroyDrawingCache();

        img_frame.setDrawingCacheEnabled(true);
        img_frame.buildDrawingCache();
        main_image.setDrawingCacheEnabled(true);
        main_image.buildDrawingCache();

        img_frame.measure(View.MeasureSpec.makeMeasureSpec(main_image.getWidth(), View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(main_image.getHeight(), View.MeasureSpec.UNSPECIFIED));


        Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache(), (int) main_image.getX(), (int) main_image.getY(), main_image.getWidth(), main_image.getHeight());
        //  Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache());


        main_image.setImageBitmap(b);
        img_frame.destroyDrawingCache();
        main_image.destroyDrawingCache();


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/HairStyle Editor");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        String path = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        path = "Style" + path + ".jpg";
        File f = new File(myDir.getAbsolutePath() + "/" + path);

        //File file = new File (myDir, fname);
        if (f.exists()) f.delete();
        try {
            FileOutputStream out = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            hair_img.setVisibility(View.GONE);
            MediaScannerConnection.scanFile(EditorActivity.this,
                    new String[]{f.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);

                        }
                    });
            final_save = true;
            Snackbar snackbar = Snackbar.make(main_frame, "Image Saved Successfully", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.snack));
            snackbar.show();

            if (tag.equals("back")) {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hairStyles(final String style) {

        final Dialog dialog = new Dialog(EditorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.savedialog);
        final TextView d_title = (TextView) dialog.findViewById(R.id.title);

        d_title.setText("Do you want to save your work");
        TextView d_save = (TextView) dialog.findViewById(R.id.d_save);
        TextView d_discard = (TextView) dialog.findViewById(R.id.d_discard);
        ImageView d_cancel = (ImageView) dialog.findViewById(R.id.d_cancel);


        d_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                img_frame.setDrawingCacheEnabled(true);
                img_frame.buildDrawingCache();
                main_image.setDrawingCacheEnabled(true);
                main_image.buildDrawingCache();

                img_frame.measure(View.MeasureSpec.makeMeasureSpec(main_image.getWidth(), View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(main_image.getHeight(), View.MeasureSpec.UNSPECIFIED));

                Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache(), (int) main_image.getX(), (int) main_image.getY(), main_image.getWidth(), main_image.getHeight());


                //Bitmap b = Bitmap.createBitmap(img_frame.getDrawingCache());


                main_image.setImageBitmap(b);
                img_frame.destroyDrawingCache();
                main_image.destroyDrawingCache();

                clicked = "";
                hair_img.setVisibility(View.GONE);
                isStyleVisible = false;
                hairStyles(style);
                dialog.dismiss();

            }
        });

        d_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = "";
                hair_img.setVisibility(View.GONE);
                isStyleVisible = false;
                hairStyles(style);
                dialog.dismiss();


            }
        });

        d_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();


            }
        });

        StyleAdapter styleAdapter;
        if (isStyleVisible) {
            isStyleVisible = false;
            gridView.setVisibility(View.GONE);

        } else {

            if (style.equals("hair")) {
                if (clicked.equals("hair") || clicked.equals("") || saved == true) {
                    isStyleVisible = true;

                    gridView.setVisibility(View.VISIBLE);
                    styleAdapter = new StyleAdapter(EditorActivity.this, 20, "hair");
                    gridView.setAdapter(styleAdapter);
                } else {

                    dialog.show();
                }

            } else if (style.equals("mucch")) {

                if (clicked.equals("much") || clicked.equals("") || saved == true) {
                    gridView.setVisibility(View.VISIBLE);
                    isStyleVisible = true;
                    styleAdapter = new StyleAdapter(EditorActivity.this, 33, "much");
                    gridView.setAdapter(styleAdapter);
                } else {

                    dialog.show();
                }
            } else if (style.equals("beard")) {
                if (clicked.equals("beard") || clicked.equals("") || saved == true) {
                    gridView.setVisibility(View.VISIBLE);
                    isStyleVisible = true;
                    styleAdapter = new StyleAdapter(EditorActivity.this, 11, "beard");
                    gridView.setAdapter(styleAdapter);
                } else {

                    dialog.show();
                }
            } else if (style.equals("spec")) {
                if (clicked.equals("spec") || clicked.equals("") || saved == true) {
                    gridView.setVisibility(View.VISIBLE);
                    isStyleVisible = true;
                    styleAdapter = new StyleAdapter(EditorActivity.this, 25, "spec");
                    gridView.setAdapter(styleAdapter);
                } else {

                    dialog.show();
                }
            } else if (style.equals("cap")) {
                if (clicked.equals("cap") || clicked.equals("") || saved == true) {
                    gridView.setVisibility(View.VISIBLE);
                    isStyleVisible = true;
                    styleAdapter = new StyleAdapter(EditorActivity.this, 20, "cap");
                    gridView.setAdapter(styleAdapter);
                } else {

                    dialog.show();
                }
            }

        }

    }


    private class StyleAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        int size;
        String i_style;

        public StyleAdapter(Context context, int i, String i_style) {
            this.context = context;
            this.size = i;
            this.i_style = i_style;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int i) {
            return size;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.icon_layout, viewGroup, false);
                holder.icon_View = (ImageView) view.findViewById(R.id.imgIcon);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Glide.with(context).load("file:///android_asset/" + i_style + "/" + (position) + ".png").into(holder.icon_View);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i_style.equals("hair")) {
                        clicked = "hair";
                        saved = false;
                    } else if (i_style.equals("beard")) {
                        clicked = "beard";
                        saved = false;
                    } else if (i_style.equals("cap")) {
                        clicked = "cap";
                        saved = false;
                    } else if (i_style.equals("much")) {
                        clicked = "much";
                        saved = false;
                    } else if (i_style.equals("spec")) {
                        clicked = "spec";
                        saved = false;
                    }
                    isStyleVisible = false;
                    gridView.setVisibility(View.GONE);
                    hair_img.setVisibility(View.VISIBLE);
                    // hair_img.bringToFront();

                    // Glide.with(context).load("file:///android_asset/" + i_style + "/" + (position) + ".png").into(hair_img);
                    AssetManager assetManager = getAssets();
                    String fileName = i_style + "/" + (position) + ".png";
                    InputStream istr = null;
                    try {
                        istr = assetManager.open(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(istr);
                    hair_img.setImageBitmap(bitmap);
                    changes = true;
                    final_save = false;
                }
            });
            return view;
        }

        public class ViewHolder {
            ImageView icon_View;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);

        dumpEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);


                d = rotation(event);
                break;

            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;


            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM && event.getPointerCount() == 2) {
                    float newDist = spacing(event);
                    matrix.set(savedMatrix);
                    if (newDist > 10f) {
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event);

                        float r = newRot - d;
                        matrix.postRotate(r, view.getMeasuredWidth() / 2,
                                view.getMeasuredHeight() / 2);
                    }
                }
                break;

        }
        // Perform the transformation
        view.setImageMatrix(matrix);


        return true;
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");

        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())

                sb.append(";");
        }

        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        private List<Integer> colorlist;

        public HorizontalAdapter(List<Integer> colors) {
            this.colorlist = colors;
        }

        @Override
        public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.color_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HorizontalAdapter.MyViewHolder holder, final int position) {

            holder.colorbtn.setBackgroundColor(colorlist.get(position));
            holder.colorbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hair_img.getVisibility() == View.VISIBLE) {
                        hair_img.getDrawable().setColorFilter(colorlist.get(position), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return colorlist.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public Button colorbtn;

            public MyViewHolder(View itemView) {

                super(itemView);
                colorbtn = (Button) itemView.findViewById(R.id.colorbtn);
            }
        }
    }

    
    @Override
    public void onBackPressed() {

        backMethod();
    }

}
