package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.model.MyBankCardModel;
import com.zhenghui.zhqb.gift.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBankCardAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<MyBankCardModel> list;

    public MyBankCardAdapter(Context context, List<MyBankCardModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_my_bank_card, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    private void setView(int position) {
        holder.txtName.setText(list.get(position).getBankName());

        String card = list.get(position).getBankcardNumber();
        holder.txtNumber.setText(card.substring(card.length()-4,card.length()));

        if(list.get(position).getBankCode() != null){
            String bankCode = list.get(position).getBankCode().toLowerCase();

            int logoId = context.getResources().getIdentifier("logo_"+bankCode, "mipmap" , context.getPackageName());
            int backId = context.getResources().getIdentifier("back_"+bankCode, "mipmap" , context.getPackageName());

            if(logoId == 0 && backId == 0){
                holder.imgBankCart.setBackgroundResource(R.mipmap.logo_defalut);
                holder.layoutBankBg.setBackgroundResource(R.mipmap.back_default);
            }else {
                holder.imgBankCart.setBackgroundResource(logoId);
                holder.layoutBankBg.setBackgroundResource(backId);
            }

        }

    }

    static class ViewHolder {
        @BindView(R.id.img_bankCart)
        ImageView imgBankCart;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_type)
        TextView txtType;
        @BindView(R.id.txt_number)
        TextView txtNumber;
        @BindView(R.id.layout_bankBg)
        RelativeLayout layoutBankBg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
