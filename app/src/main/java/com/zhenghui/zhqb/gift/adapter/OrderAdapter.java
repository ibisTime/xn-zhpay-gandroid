package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhenghui.zhqb.gift.R.id.txt_recipients;

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<OrderModel> list;

    public OrderAdapter(Context context, List<OrderModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_good_order, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    public void setView(int position) {
        holder.txtNumber.setText("订单号: "+list.get(position).getCode());
        holder.txtRecipients.setText("收件人: "+list.get(position).getReceiver());
        holder.txtMobile.setText("联系电话: "+list.get(position).getReMobile());
        holder.txtAddress.setText("收件地址: "+list.get(position).getReAddress());

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(list.get(position).getApplyDatetime() != null){
            Date d5 = new Date(list.get(position).getApplyDatetime());
            holder.txtTime.setText("下单时间: " + s.format(d5));
        }


        if(list.get(position).getStatus().equals("2")){ // 待发货
            holder.layoutExpress.setVisibility(View.GONE);
            holder.layoutReceiving.setVisibility(View.GONE);
        }else if(list.get(position).getStatus().equals("3")){ // 待收货
            holder.layoutExpress.setVisibility(View.VISIBLE);
            holder.layoutReceiving.setVisibility(View.GONE);

            holder.txtExpress.setText(list.get(position).getLogisticsCompany()+":  "+list.get(position).getLogisticsCode());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(list.get(position).getDeliveryDatetime() != null){
                Date date = new Date(list.get(position).getDeliveryDatetime());
                holder.txtDeliverTime.setText("发货时间: "+format.format(date));
            }

        }else if(list.get(position).getStatus().equals("4")){ // 已完成
            holder.layoutExpress.setVisibility(View.VISIBLE);
            holder.layoutReceiving.setVisibility(View.VISIBLE);

            holder.txtExpress.setText(list.get(position).getLogisticsCompany()+":  "+list.get(position).getLogisticsCode());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if(list.get(position).getDeliveryDatetime() != null){
                Date deliverTime = new Date(list.get(position).getDeliveryDatetime());
                holder.txtDeliverTime.setText("发货时间: "+format.format(deliverTime));
            }
            if(list.get(position).getUpdateDatetime() != null){
                Date receivingTime = new Date(list.get(position).getUpdateDatetime());
                holder.txtReceivingTime.setText("收货时间: "+format.format(receivingTime));
            }
        }

    }

    static class ViewHolder {
        @BindView(R.id.txt_number)
        TextView txtNumber;
        @BindView(txt_recipients)
        TextView txtRecipients;
        @BindView(R.id.txt_mobile)
        TextView txtMobile;
        @BindView(R.id.txt_address)
        TextView txtAddress;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.txt_check)
        TextView txtCheck;
        @BindView(R.id.txt_express)
        TextView txtExpress;
        @BindView(R.id.txt_deliver_time)
        TextView txtDeliverTime;
        @BindView(R.id.layout_express)
        LinearLayout layoutExpress;
        @BindView(R.id.txt_receiving_time)
        TextView txtReceivingTime;
        @BindView(R.id.layout_receiving)
        LinearLayout layoutReceiving;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
