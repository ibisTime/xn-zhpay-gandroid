package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.ParameterModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParameterAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder viewHolder;
    private List<ParameterModel> list;
    private SharedPreferences userInfoSp;

    public ParameterAdapter(Context context, List<ParameterModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_product_paramter, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    public void setView(int i) {
        viewHolder.txtVersion.setText(list.get(i).getName());
        try {
            viewHolder.txtTotal.setText(NumberUtil.doubleFormatMoney(list.get(i).getPrice1())+"礼品券");
        }catch (Exception e){
            e.printStackTrace();
        }

        viewHolder.txtWeight.setText("单件重量:" + list.get(i).getWeight() + "Kg");
        viewHolder.txtInventory.setText("库存:" + list.get(i).getQuantity());
        viewHolder.txtPlace.setText("发货地:" + list.get(i).getProvince());
    }

    static class ViewHolder {
        @BindView(R.id.txt_version)
        TextView txtVersion;
        @BindView(R.id.txt_total)
        TextView txtTotal;
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
