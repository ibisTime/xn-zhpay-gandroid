package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.util.ImageUtil;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<String> list;
    private List<String> listUrl;

    private MyItemClickListener mItemClickListener;


    public RecyclerViewAdapter(Context context, List<String> list, List<String> listUrl) {
        this.list = list;
        this.listUrl = listUrl;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview, null);
        ViewHolder vh = new ViewHolder(view, mItemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageUtil.glide(list.get(position),holder.imgPhoto,context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgPhoto;
        ImageView imgDelete;

        private MyItemClickListener mListener;

        public ViewHolder(View view, MyItemClickListener mListener) {
            super(view);
            imgPhoto = (ImageView) view.findViewById(R.id.img_photo);
            imgDelete = (ImageView) view.findViewById(R.id.img_delete);

            this.mListener = mListener;
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(getPosition());
                    listUrl.remove(getPosition());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.OnItemClick(view.findViewById(R.id.txt_type), getPosition());
            }
        }
    }

    public interface MyItemClickListener {
        void OnItemClick(View view, int position);
    }


    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }


}
