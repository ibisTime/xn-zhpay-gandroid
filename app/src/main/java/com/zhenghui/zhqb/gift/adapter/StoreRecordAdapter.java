package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.StoreRecordModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreRecordAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<StoreRecordModel> list;

    public StoreRecordAdapter(Context context, List<StoreRecordModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_record, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    public void setView(int position) {
        holder.txtName.setText("消费者: " + list.get(position).getUser().getMobile());
        holder.txtPrice.setText("消费金额: " + NumberUtil.doubleFormatMoney(list.get(position).getPrice()));
        holder.txtType.setText("支付方式: " + payType(list.get(position).getPayType()));
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d5 = new Date(list.get(position).getPayDatetime());
        holder.txtTime.setText("下单时间: " + s.format(d5));
    }

    public String payType(String payType){

        switch (payType){
            case "1":
                return "余额";

            case "2":
                return "微信";

            case "3":
                return "支付宝";

            case "20":
                return "礼品券";

            default:
                return "";
        }

    }

    static class ViewHolder {
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_type)
        TextView txtType;
        @BindView(R.id.txt_time)
        TextView txtTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
