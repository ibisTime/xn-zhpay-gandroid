package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.DiscountModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscountAdapter extends BaseAdapter {

    private List<DiscountModel> list;
    private Context context;
    private ViewHolder holder;

    public DiscountAdapter(Context context, List<DiscountModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_discount, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    private void setView(int position) {

        if (list.get(position).getStatus().equals("0")) { // 待上架
            holder.imgZhang.setVisibility(View.GONE);
            holder.layoutBg.setBackground(context.getResources().getDrawable(R.mipmap.discount_used_bg));

            holder.txtName.setTextColor(context.getResources().getColor(R.color.graycc));
            holder.txtTime.setTextColor(context.getResources().getColor(R.color.graycc));

        } else if (list.get(position).getStatus().equals("1")) { // 已上架
            holder.imgZhang.setVisibility(View.GONE);
            holder.layoutBg.setBackground(context.getResources().getDrawable(R.mipmap.discount_unused_bg));

            holder.txtName.setTextColor(context.getResources().getColor(R.color.fontColor_pink));
            holder.txtTime.setTextColor(context.getResources().getColor(R.color.fontColor_support));

        } else if (list.get(position).getStatus().equals("2")) { // 已下架
            holder.imgZhang.setVisibility(View.VISIBLE);
            holder.imgZhang.setBackgroundResource(R.mipmap.discount_lower);
            holder.layoutBg.setBackground(context.getResources().getDrawable(R.mipmap.discount_used_bg));

            holder.txtName.setTextColor(context.getResources().getColor(R.color.graycc));
            holder.txtTime.setTextColor(context.getResources().getColor(R.color.graycc));
        } else {
            holder.imgZhang.setVisibility(View.VISIBLE); // 期满作废
            holder.imgZhang.setBackgroundResource(R.mipmap.discount_timeout);
            holder.layoutBg.setBackground(context.getResources().getDrawable(R.mipmap.discount_used_bg));

            holder.txtName.setTextColor(context.getResources().getColor(R.color.graycc));
            holder.txtTime.setTextColor(context.getResources().getColor(R.color.graycc));
        }

        holder.txtName.setText("消费满" + (list.get(position).getKey1() / 1000) + "抵扣" + (list.get(position).getKey2() / 1000)+"元");
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date d5 = new Date(list.get(position).getValidateEnd());
        holder.txtTime.setText("有效期: " + s.format(d5));

    }

    static class ViewHolder {
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.img_zhang)
        ImageView imgZhang;
        @BindView(R.id.layout_bg)
        LinearLayout layoutBg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
