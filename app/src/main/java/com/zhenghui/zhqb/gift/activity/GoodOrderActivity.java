package com.zhenghui.zhqb.gift.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.OrderAdapter;
import com.zhenghui.zhqb.gift.model.OrderModel;
import com.zhenghui.zhqb.gift.util.RefreshLayout;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808065;

public class GoodOrderActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_dfh)
    TextView txtDfh;
    @BindView(R.id.line_dfh)
    View lineDfh;
    @BindView(R.id.txt_dsh)
    TextView txtDsh;
    @BindView(R.id.line_dsh)
    View lineDsh;
    @BindView(R.id.txt_ywc)
    TextView txtYwc;
    @BindView(R.id.line_ywc)
    View lineYwc;
    @BindView(R.id.list_order)
    ListView listOrder;
    @BindView(R.id.layout_refresh)
    RefreshLayout layoutRefresh;

    private String status = "2";

    private int page = 1;
    private int pageSize = 10;


    private OrderAdapter adapter;
    private ArrayList<OrderModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_order);
        ButterKnife.bind(this);

        init();
        initListView();
        initRefreshLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrder();
    }

    private void init() {
        list = new ArrayList<>();
        adapter = new OrderAdapter(this,list);
    }

    private void initListView() {
        listOrder.setAdapter(adapter);
        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(GoodOrderActivity.this, OrderDetailActivity.class).putExtra("code", list.get(i).getCode()));
            }
        });
    }

    private void initRefreshLayout() {
        layoutRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        layoutRefresh.setOnRefreshListener(this);
        layoutRefresh.setOnLoadListener(this);

    }

    @OnClick({R.id.layout_back, R.id.txt_dfh, R.id.txt_dsh, R.id.txt_ywc})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_dfh:
                initBtn();
                txtDfh.setTextColor(getResources().getColor(R.color.fontColor_oy));
                lineDfh.setBackgroundColor(getResources().getColor(R.color.fontColor_oy));

                page = 1;
                status = "2";
                getOrder();
                break;

            case R.id.txt_dsh:
                initBtn();
                txtDsh.setTextColor(getResources().getColor(R.color.fontColor_oy));
                lineDsh.setBackgroundColor(getResources().getColor(R.color.fontColor_oy));

                page = 1;
                status = "3";
                getOrder();
                break;

            case R.id.txt_ywc:
                initBtn();
                txtYwc.setTextColor(getResources().getColor(R.color.fontColor_oy));
                lineYwc.setBackgroundColor(getResources().getColor(R.color.fontColor_oy));

                page = 1;
                status = "4";
                getOrder();
                break;
        }
    }

    private void initBtn(){
        txtDfh.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineDfh.setBackgroundColor(getResources().getColor(R.color.white));

        txtDsh.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineDsh.setBackgroundColor(getResources().getColor(R.color.white));

        txtYwc.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineYwc.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void getOrder() {
        JSONObject object = new JSONObject();
        try {
            object.put("applyUser", "");
            object.put("status", status);
            object.put("payType", "");
            object.put("payGroup", "");
            object.put("payCode", "");
            object.put("deliverer", "");
            object.put("logisticsCode", "");
            object.put("logisticsCompany", "");
            object.put("start", page);
            object.put("limit", pageSize);
            object.put("orderDir", "");
            object.put("orderColumn", "");
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", userInfoSp.getString("userId", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808065, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    ArrayList<OrderModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<OrderModel>>() {
                    }.getType());

                    if(page == 1){
                        list.clear();
                    }
                    list.addAll(lists);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(GoodOrderActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(GoodOrderActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRefresh() {
        layoutRefresh.postDelayed(new Runnable() {

            @Override
            public void run() {
                layoutRefresh.setRefreshing(false);

                page = 1;
                getOrder();
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        layoutRefresh.postDelayed(new Runnable() {

            @Override
            public void run() {
                layoutRefresh.setLoading(false);

                page = page + 1;
                getOrder();
            }
        }, 1500);
    }
}
