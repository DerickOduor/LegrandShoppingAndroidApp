package com.derickoduor.legrand.Controller;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.derickoduor.legrand.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Derick Oduor on 4/9/2018.
 */

public class ImageSliderAdapter extends PagerAdapter {

    Context context;
    String[] product_images;

    public ImageSliderAdapter(Context context,String[] product_images) {
        this.context = context;
        this.product_images=product_images;
    }

    @Override
    public int getCount() {
        return product_images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.cart_image_slider,container,false);

        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.slideLayout);
        ImageView slide_img=(ImageView)view.findViewById(R.id.imageSlide);
        Picasso.with(context).load(product_images[position]).into(slide_img);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
