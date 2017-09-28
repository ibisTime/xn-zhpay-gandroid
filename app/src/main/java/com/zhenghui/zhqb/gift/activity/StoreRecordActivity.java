package com.zhenghui.zhqb.gift.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.StoreRecordAdapter;
import com.zhenghui.zhqb.gift.model.StoreRecordModel;
import com.zhenghui.zhqb.gift.util.RefreshLayout;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808248;

public class StoreRecordActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.list_order)
    ListView listOrder;
    @BindView(R.id.layout_refresh)
    RefreshLayout layoutRefresh;

    private int page = 1;
    private int pageSize = 10;

    private List<StoreRecordModel> list;
    private StoreRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_record);
        ButterKnife.bind(this);

        inits();
        initListView();
        initRefreshLayout();

        getRecord();
    }

    private void inits() {
        list = new ArrayList<>();
        adapter = new StoreRecordAdapter(this,list);
    }

    private void initListView() {
        listOrder.setAdapter(adapter);
    }

    private void initRefreshLayout() {
        layoutRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        layoutRefresh.setOnRefreshListener(this);
        layoutRefresh.setOnLoadListener(this);

    }

    @OnClick(R.id.layout_back)
    public void onClick() {
        finish();
    }

    private void getRecord() {
        JSONObject object = new JSONObject();
        try {
            object.put("storeCode", userInfoSp.getString("storeCode",""));
            object.put("start", page);
            object.put("limit", pageSize);
            object.put("orderDir", "");
            object.put("orderColumn", "");
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808248, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    ArrayList<StoreRecordModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<StoreRecordModel>>() {
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
                Toast.makeText(StoreRecordActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreRecordActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
                getRecord();
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
                getRecord();
            }
        }, 1500);
    }
}
