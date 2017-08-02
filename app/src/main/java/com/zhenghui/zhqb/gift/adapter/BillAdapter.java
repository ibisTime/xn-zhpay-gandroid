package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.BillModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillAdapter extends BaseAdapter {

    private List<BillModel> list;
    private Context context;
    private ViewHolder holder;

    public BillAdapter(Context context, List<BillModel> list) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bill2, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    private void setView(int position) {

        if(list.get(position).getTransAmount() > 0){
            holder.txtPrice.setTextColor(context.getResources().getColor(R.color.fontColor_orange));
            holder.imgType.setImageResource(R.mipmap.bill_get);
            holder.txtPrice.setText("+"+ NumberUtil.doubleFormatMoney(list.get(position).getTransAmount()));
        }else {
            holder.txtPrice.setTextColor(context.getResources().getColor(R.color.fontColor_blue));
            holder.imgType.setImageResource(R.mipmap.bill_pay);
            holder.txtPrice.setText(NumberUtil.doubleFormatMoney(list.get(position).getTransAmount()));
        }

        holder.txtInfo.setText(list.get(position).getBizNote());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd日");
        Date date = new Date(list.get(position).getCreateDatetime());
        holder.txtDate.setText(dateFormat.format(date));
        holder.txtTime.setText(timeFormat.format(date));


    }

    static class ViewHolder {
        @BindView(R.id.txt_date)
        TextView txtDate;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.img_type)
        ImageView imgType;
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_info)
        TextView txtInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
