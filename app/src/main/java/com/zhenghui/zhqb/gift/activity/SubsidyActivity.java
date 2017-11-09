package com.zhenghui.zhqb.gift.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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
import com.zhenghui.zhqb.gift.adapter.RightsAdapter;
import com.zhenghui.zhqb.gift.model.RightsModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.RefreshLayout;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.R.id.txt_get;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808457;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808458;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808459;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808917;

/**
 * Created by lei on 2017/9/21.
 */

public class SubsidyActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener,AdapterView.OnItemClickListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.list_rights)
    ListView listRights;
    @BindView(R.id.swipe_container)
    RefreshLayout swipeContainer;

    TextView txtBtq;
    TextView txtGet;
    TextView txtPool;
    TextView txtEarnings;
    TextView txtTurnover;
    LinearLayout layoutGet;
    LinearLayout layoutPool;

    List<RightsModel> list;
    RightsAdapter adapter;

    private View headView;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidy);
        ButterKnife.bind(this);

        inits();
        initHeadView();
        initsListView();
        initRefreshLayout();

        getData();
        getTotalLimit();
        getProperty();
        getIsShow();
    }

    private void inits() {
        list = new ArrayList<>();
        adapter = new RightsAdapter(this,list);
    }

    private void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.head_subsidy,null);

        txtBtq = (TextView) headView.findViewById(R.id.txt_btq);
        txtGet = (TextView) headView.findViewById(txt_get);
        txtPool = (TextView) headView.findViewById(R.id.txt_pool);
        txtEarnings = (TextView) headView.findViewById(R.id.txt_earnings);
        txtTurnover = (TextView) headView.findViewById(R.id.txt_turnover);

        layoutGet = (LinearLayout) headView.findViewById(R.id.layout_get);
        layoutPool = (LinearLayout) headView.findViewById(R.id.layout_pool);
    }

    private void initsListView() {
        listRights.addHeaderView(headView);
        listRights.setAdapter(adapter);
        listRights.setOnItemClickListener(this);
    }

    private void initRefreshLayout() {
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnLoadListener(this);
        swipeContainer.setOnRefreshListener(this);
    }

    @OnClick(R.id.layout_back)
    public void onClick() {
        finish();
    }


    private void getIsShow() {
        JSONObject object = new JSONObject();
        try {
            object.put("key", "POOL_VISUAL");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808917, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if(jsonObject.getString("cvalue").equals("1")){ // 1显示
                        layoutGet.setVisibility(View.GONE);
                        layoutPool.setVisibility(View.VISIBLE);
                    }else {
                        layoutPool.setVisibility(View.GONE);
                        layoutGet.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(SubsidyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(SubsidyActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProperty() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808459, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    txtBtq.setText(jsonObject.getInt("stockCount")+"");

                    txtPool.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("poolAmount")));
                    txtGet.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("backProfitAmount")));
                    txtEarnings.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("unbackProfitAmount")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(SubsidyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(SubsidyActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        JSONObject object = new JSONObject();
        try {
            object.put("start", page+"");
            object.put("limit", "10");
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808457, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    ArrayList<RightsModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<RightsModel>>() {
                    }.getType());

                    if (page == 1){
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
                Toast.makeText(SubsidyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(SubsidyActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalLimit() {

        JSONObject object = new JSONObject();
        try {
            object.put("key", "CUSER_BUY_AMOUNT");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808917, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    txtTurnover.setText(jsonObject.getString("cvalue"));
                    int num = Integer.parseInt(jsonObject.getString("cvalue")) * 1000;

                    getLimit(num);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(SubsidyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(SubsidyActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询所需消费额
     */
    private void getLimit(final int num) {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808458, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    txtTurnover.setText(NumberUtil.doubleFormatMoney(num - jsonObject.getDouble("costAmount")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(SubsidyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(SubsidyActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
                page = 1;
                getData();
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setLoading(false);
                page = page + 1;
                getData();


            }
        }, 1500);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i > 0){
            startActivity(new Intent(SubsidyActivity.this, RightsListActivity.class)
                    .putExtra("received", NumberUtil.doubleFormatMoney(list.get(i-1).getBackAmount()))
                    .putExtra("unclaimed", NumberUtil.doubleFormatMoney(list.get(i-1).getProfitAmount() - list.get(i-1).getBackAmount()))
                    .putExtra("date",list.get(i-1).getCreateDatetime())
                    .putExtra("code",list.get(i-1).getCode())
                    .putExtra("type","BTQ"));
        }
    }
}
