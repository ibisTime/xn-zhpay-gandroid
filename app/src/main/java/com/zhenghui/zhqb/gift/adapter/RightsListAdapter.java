package com.zhenghui.zhqb.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.RightsListModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RightsListAdapter extends BaseAdapter {

    private List<RightsListModel> list;
    private Context context;
    private ViewHolder holder;

    public RightsListAdapter(Context context, List<RightsListModel> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_rights_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setView(i);

        return view;
    }

    private void setView(int position) {
        holder.txtPrice.setText(NumberUtil.doubleFormatMoney(list.get(position).getToAmount()) + setAssets(position));

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d5 = new Date(list.get(position).getCreateDatetime());
        holder.txtTime.setText(s.format(d5));
    }

    private String setAssets(int position) {
        switch (list.get(position).getToCurrency()) {
            case "FRB":
                return "分润";

            case "QBB":
                return "钱包币";

            case "HBB":
                return "红包币";

            case "GXJL":
                return "贡献奖励";

            case "GWB":
                return "购物币";

            case "HBYJ":
                return "红包业绩";

        }
        return "";
    }

    static class ViewHolder {
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_time)
        TextView txtTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
