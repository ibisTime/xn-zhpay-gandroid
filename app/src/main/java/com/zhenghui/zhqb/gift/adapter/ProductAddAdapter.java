package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.ProductModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAddAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<ProductModel> list;

    public ProductAddAdapter(Context context, List<ProductModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list.size() == 0) {
            return 1;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_product_paramter, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    public void setView(int position) {

    }

    static class ViewHolder {
        @BindView(R.id.txt_version)
        TextView txtVersion;
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_weight)
        TextView txtWeight;
        @BindView(R.id.txt_inventory)
        TextView txtInventory;
        @BindView(R.id.txt_place)
        TextView txtPlace;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
