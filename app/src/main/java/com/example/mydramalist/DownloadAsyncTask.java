package com.example.mydramalist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@SuppressLint("StaticFieldLeak")
public class DownloadAsyncTask extends AsyncTask<ImageView, Void, Bitmap> {
    ImageView imageView;
    String thumb;
    Context context;

    DownloadAsyncTask(Context context, String thumb) {
        this.context = context;
        this.thumb = thumb;
    }

    protected Bitmap doInBackground(ImageView... imageView) {
        this.imageView = imageView[0];
        return download();
    }

    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(MainActivity.TAG, "Download filed");
            this.imageView.setImageBitmap(getBitmap(context));
//            updateUI();
        }
    }

    private Bitmap download() {
        Bitmap bmp = null;
        try {
            URL url = new URL(thumb);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return compBitmap(bmp);
        } catch (Exception ignored) {
        }
        return bmp;
    }

    private Bitmap compBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 128) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        }
        ByteArrayInputStream isBm;
        Bitmap bitmap;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float ww = 500f;
        float hh = 500f;

        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (((baos.toByteArray().length / 1024) > 100) && options != 0) { // 100kb
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            if (options > 10) {
                options -= 10;
            } else {
                options--;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    private Bitmap getBitmap(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_offline);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}