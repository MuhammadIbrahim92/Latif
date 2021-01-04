package com.otacodes.goestateapp.Item;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.otacodes.goestateapp.Activity.PropertyDetailActivity;
import com.otacodes.goestateapp.Models.PropertyModels;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.BannerAds;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by otacodes on 3/23/2019.
 */

public class SliderItem extends PagerAdapter {

    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<PropertyModels> mList;

    public SliderItem(Activity context, ArrayList<PropertyModels> propertyModels) {
        this.context = context;
        this.mList = propertyModels;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        assert imageLayout != null;
        RelativeLayout rootlayout = imageLayout.findViewById(R.id.rootLayout);
        ImageView imageView = imageLayout.findViewById(R.id.image);
        TextView name = imageLayout.findViewById(R.id.text);
        Button price = imageLayout.findViewById(R.id.price);

        final PropertyModels propertyModels = mList.get(position);
        Picasso.with(context)
                .load(propertyModels.getImage())
                .placeholder(R.drawable.image_placeholder).into(imageView);
        name.setText(propertyModels.getName());
        Double getprice = Double.valueOf(propertyModels.getPrice());
        String total = String.format(Locale.US, "$%s",
                NumberFormat.getNumberInstance(Locale.US).format(getprice));
        price.setText(total);

        rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(context);
                Intent intent = new Intent(context, PropertyDetailActivity.class);
                intent.putExtra("Id", propertyModels.getPropid());
                context.startActivity(intent);

            }
        });
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
