package com.otacodes.goestateapp.Item;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.TouchImageView;

import java.util.List;

public class FullImageItem extends PagerAdapter {

    private Activity act;
    private List<String> imagePaths;
    private LayoutInflater inflater;
    private ImageLoader imgloader = ImageLoader.getInstance();

    // constructor
    public FullImageItem (Activity activity, List<String> imagePaths) {
        this.act = activity;
        this.imagePaths = imagePaths;


    }

    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        TouchImageView imgDisplay;
        Button savebtn;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_full_image, container, false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        savebtn = (Button) viewLayout.findViewById(R.id.savebtn);

        imgloader.displayImage(imagePaths.get(position), imgDisplay);
        ((ViewPager) container).addView(viewLayout);




        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
