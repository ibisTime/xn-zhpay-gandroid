package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.ProductModel;
import com.zhenghui.zhqb.gift.util.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SoldOutAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<ProductModel> list;

    public SoldOutAdapter(Context context, List<ProductModel> list) {
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
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_sold_out, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        setView(i);

        return view;
    }

    private void setView(int i) {
        ImageUtil.glide(list.get(i).getAdvPic(),holder.imgPhoto,context);
    }

    static class ViewHolder {
        @BindView(R.id.img_photo)
        ImageView imgPhoto;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
