package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.MessageModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemMessageAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<MessageModel> list;

    public SystemMessageAdapter(Context context, List<MessageModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_system_message2, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    private void setView(int position) {
//        holder.txtMessage.setText(list.get(position).getSmsTitle());
        holder.txtMessage.setText(list.get(position).getSmsContent());

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d5 = new Date(list.get(position).getPushedDatetime());
        holder.txtTime.setText(s.format(d5));

    }

    static class ViewHolder {
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.txt_message)
        TextView txtMessage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
