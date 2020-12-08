package com.example.mydramalist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static com.example.mydramalist.MainActivity.dramaData;

public class DramaListAdapter extends BaseAdapter {

    private Context context;
    private View detail;
    private AlertDialog dialog;

    private static class ViewHolder {
        ImageView item_img;
        TextView item_name;
        TextView item_rating_txt;
        RatingBar item_rating_bar;
        TextView item_created_at;
    }

    public int getCount() {
        return dramaData.size();
    }

    public Object getItem(int i) {
        return i;
    }

    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams", "SimpleDateFormat", "WrongViewCast"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        context = viewGroup.getContext();
        DramaData drama = dramaData.get(i);
        String name = drama.name;
        String total_views = drama.total_views + " 次";
        String created_at = new SimpleDateFormat("yyyy-MM-dd")
                .format(Date.from(Instant.parse(drama.created_at)));
        String thumb = drama.thumb;
        String rating = new java.text.DecimalFormat("#.0").
                format(Double.parseDouble(drama.rating));
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_items, null);
            holder = new ViewHolder();
            holder.item_img = view.findViewById(R.id.item_img);
            holder.item_name = view.findViewById(R.id.item_name);
            holder.item_rating_txt = view.findViewById(R.id.item_rating_txt);
            holder.item_rating_bar = view.findViewById(R.id.item_rating_bar);
            holder.item_created_at = view.findViewById(R.id.item_created_at);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        try {
            Picasso.with(context)
                    .load(thumb)
                    .error(R.drawable.ic_offline)
                    .into(holder.item_img);
            holder.item_name.setText(name);
            holder.item_rating_txt.setText(rating);
            holder.item_rating_bar.setRating(Float.parseFloat(rating));
            holder.item_created_at.setText(created_at);
            View finalView = view;
            view.findViewById(R.id.item).setOnClickListener((v) -> {
                detail = LayoutInflater.from(context).inflate(R.layout.layout_detail, null);
                dialog = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                        .setView(detail).setCancelable(true).create();
                try {
                    BitmapDrawable drawable = (BitmapDrawable) ((ImageView) finalView.findViewById(R.id.item_img)).getDrawable();
                    ((ImageView) detail.findViewById(R.id.item_img)).setImageBitmap(drawable.getBitmap());
                } catch (Exception ignored) {
                }
                ((TextView) detail.findViewById(R.id.item_name)).setText(name);
                ((TextView) detail.findViewById(R.id.item_rating_txt)).setText(rating);
                ((RatingBar) detail.findViewById(R.id.item_rating_bar)).setRating(Float.parseFloat(rating));
                ((TextView) detail.findViewById(R.id.item_total_views)).setText(total_views);
                ((TextView) detail.findViewById(R.id.item_created_at)).setText(created_at);
                detail.findViewById(R.id.item_cancel).setOnClickListener((View vs) -> { // ok
                    dialog.dismiss();
                });
                dialog.show();
            });
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "getView:" + e.toString());
        }
        return view;
    }
}
