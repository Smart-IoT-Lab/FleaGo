package com.example.fleago;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import com.example.fleago.MyGlideModule;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;
//    String[] images;
//    LayoutInflater inflater;

    List<String> data = new ArrayList<>();
    Context context;
//    ArrayList<String> photoPath;

//    public ViewPagerAdapter(Activity activity, String[] images) {
//        this.activity = activity;
//        this.images = images;
//    }

    public ViewPagerAdapter(Activity activity, ArrayList<String> photoPath) {
        this.activity = activity;
        this.data = photoPath;
        this.context = activity.getApplicationContext();
    }
    ViewPagerAdapter(Context context) {
        this.context = context;
    }

    public void add(List<String> data) {
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_image, container, false);
        ImageView imageview1 = (ImageView) view.findViewById(R.id.imageView);
        StorageReference storage;
        storage = FirebaseStorage.getInstance().getReference("image/");
        StorageReference banner1 = storage.child(data.get(position));

        Log.d("TEST imagePath", data.get(position));

        Glide.with(context.getApplicationContext())
//                .using(new FirebaseImageLoader())
                .load(banner1)
                .into(imageview1);

        container.addView(view);
        return view;
    }
//        inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View itemview = inflater.inflate(R.layout.viewpager_image,container,false);
//
//        ImageView image;
//        image = (ImageView) itemview.findViewById(R.id.imageView);
//        DisplayMetrics dis = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
//        int height = dis.heightPixels;
//        int width = dis.widthPixels;
//        image.setMinimumHeight(height);
//        image.setMinimumWidth(width);

//        try {
//            Picasso.get()
//                    .load(images[position])
//                    .placeholder(R.mipmap.ic_launcher)
//                    .error(R.mipmap.ic_launcher)
//                    .into(image);
//        }
//
//        catch (Exception ex) {
//
//        }
//
//        container.addView(itemview);
//        return itemview;
//    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}