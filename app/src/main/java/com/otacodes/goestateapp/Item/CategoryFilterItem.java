package com.otacodes.goestateapp.Item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.otacodes.goestateapp.Constants.Constants;
import com.otacodes.goestateapp.Models.CategoryModels;
import com.otacodes.goestateapp.R;

import java.util.ArrayList;

/**
 * Created by otacodes on 4/4/2019.
 */

public class CategoryFilterItem extends RecyclerView.Adapter<CategoryFilterItem.ItemRowHolder> {

    public ArrayList<CategoryModels> dataList;
    private Context mContext;
    private int lastSelectedPosition = -1;
    private CompoundButton lastCheckedRB = null;
    public CategoryFilterItem(Context context, ArrayList<CategoryModels> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final CategoryModels singleItem = dataList.get(position);

        holder.radioButtonType.setText(singleItem.getCategoryName());
        holder.radioButtonType.setTag(position);
        holder.radioButtonType.setTag(R.id.filter_name, singleItem);
        holder.radioButtonType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int tag = (int) compoundButton.getTag();
                if (lastCheckedRB == null) {
                    lastCheckedRB = compoundButton;
                } else if (tag != (int) lastCheckedRB.getTag()) {
                    lastCheckedRB.setChecked(false);
                    lastCheckedRB = compoundButton;
                }
                Constants.FILTERCATID = singleItem.getCategoryId();
            }
        });




    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private RelativeLayout relativeLayout;
        RadioGroup checkbox_fil_type;
        RadioButton radioButtonType;

        private ItemRowHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rootLayout);
            checkbox_fil_type = itemView.findViewById(R.id.myRadioGroupType);
            radioButtonType = itemView.findViewById(R.id.filter_name);

        }
    }
}
