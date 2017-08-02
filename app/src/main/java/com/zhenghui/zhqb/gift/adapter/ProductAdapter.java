package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.model.ProductModel;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.util.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<ProductModel> list;

    public ProductAdapter(Context context, List<ProductModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(list.size() == 0){
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
        if(list.size() == 0){
            view = LayoutInflater.from(context).inflate(R.layout.item_product_no, null);
        } else {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_product, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            setView(i);

        }

        return view;
    }

    public void setView(int position) {
        ImageUtil.glide(list.get(position).getAdvPic(),holder.imgPhoto,context);

        holder.txtTitle.setText(list.get(position).getName());
        if(list.get(position).getCategory().equals("FL201700000000000001")){
            holder.txtContent.setText("剁手合集");
        }else{
            holder.txtContent.setText("0元试购");
        }

        if(list.get(position).getStatus().equals("0")){
            holder.txtInfo.setText("待审核");
            holder.txtInfo.setTextColor(context.getResources().getColor(R.color.fontColor_gray));
        }else if(list.get(position).getStatus().equals("1")){
            holder.txtInfo.setText("审批通过待上架");
            holder.txtInfo.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(list.get(position).getStatus().equals("91")){
            holder.txtInfo.setText("审核不通过");
            holder.txtInfo.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(list.get(position).getStatus().equals("3")){
            holder.txtInfo.setText("已上架");
            holder.txtInfo.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(list.get(position).getStatus().equals("4")){
            holder.txtInfo.setText("已下架");
            holder.txtInfo.setTextColor(context.getResources().getColor(R.color.orange));
        }

    }

    static class ViewHolder {
        @BindView(R.id.img_photo)
        ImageView imgPhoto;
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.txt_content)
        TextView txtContent;
        @BindView(R.id.txt_info)
        TextView txtInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
