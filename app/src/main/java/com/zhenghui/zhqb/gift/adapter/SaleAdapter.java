package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.ProductModel;
import com.zhenghui.zhqb.gift.util.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<ProductModel> list;

    public SaleAdapter(Context context, List<ProductModel> list) {
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

        if (list.get(i).isAdd()) {
            view = LayoutInflater.from(context).inflate(R.layout.item_sell_add, null);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_sell, null);
            holder = new ViewHolder(view);
            setView(i);
        }

        return view;
    }

    private void setView(int i) {
        holder.txtName.setText(list.get(i).getName());
        ImageUtil.glide(list.get(i).getAdvPic(),holder.imgPhoto,context);

    }

    static class ViewHolder {
        @BindView(R.id.img_photo)
        ImageView imgPhoto;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_edit)
        TextView txtEdit;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
