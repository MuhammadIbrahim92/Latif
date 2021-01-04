package com.otacodes.goestateapp.Item;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ornolfr.ratingview.RatingView;
import com.otacodes.goestateapp.Activity.PropertyDetailActivity;
import com.otacodes.goestateapp.Models.PropertyModels;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.BannerAds;
import com.otacodes.goestateapp.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by estateapp on 3/24/2019.
 */

public class ListItem extends RecyclerView.Adapter<ListItem.ItemRowHolder> {

    private ArrayList<PropertyModels> dataList;
    private Context mContext;
    private int rowLayout;
    private DatabaseHelper databaseHelper;

    public ListItem(Context context, ArrayList<PropertyModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        databaseHelper = new DatabaseHelper(mContext);

    }


    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_list, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final PropertyModels singleItem = dataList.get(position);
        holder.text1.setText(singleItem.getName());
        Double getprice = Double.valueOf(singleItem.getPrice());
        String total = String.format(Locale.US, "$%s",
                NumberFormat.getNumberInstance(Locale.US).format(getprice));
        holder.price1.setText(total);
        holder.address1.setText(singleItem.getAddress());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
        holder.bed1.setText(singleItem.getBed()+" "+"Years");
        holder.bath1.setText(singleItem.getBath());
        holder.square1.setText(singleItem.getArea());
        holder.purpose1.setText(singleItem.getPurpose());
        Picasso.with(mContext)
                .load(singleItem.getImage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

        if (databaseHelper.getFavouriteById(singleItem.getPropid())) {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
        }

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getPropid())) {
                    databaseHelper.removeFavouriteById(singleItem.getPropid());
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
                    Toast.makeText(mContext, "Remove To Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getPropid());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getName());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                    fav.put(DatabaseHelper.KEY_RATE, singleItem.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, singleItem.getBed());
                    fav.put(DatabaseHelper.KEY_BATH, singleItem.getBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, singleItem.getAddress());
                    fav.put(DatabaseHelper.KEY_AREA, singleItem.getArea());
                    fav.put(DatabaseHelper.KEY_PRICE, singleItem.getPrice());
                    fav.put(DatabaseHelper.KEY_PURPOSE, singleItem.getPurpose());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(mContext, "Add To Favourite", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, PropertyDetailActivity.class);
                intent.putExtra("Id", singleItem.getPropid());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView text1, price1, address1, purpose1, bed1, bath1, square1;
        ImageView images, favourite;
        RatingView ratingView;
        LinearLayout rootlayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.image);
            text1 = itemView.findViewById(R.id.text);
            favourite = itemView.findViewById(R.id.favourite);
            price1 = itemView.findViewById(R.id.price);
            address1 = itemView.findViewById(R.id.address);
            purpose1 = itemView.findViewById(R.id.textPurpose);
            ratingView = itemView.findViewById(R.id.ratingView);
            bed1 = itemView.findViewById(R.id.bed);
            bath1 = itemView.findViewById(R.id.bath);
            square1 = itemView.findViewById(R.id.square);
        }
    }
}
