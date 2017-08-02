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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802502;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808275;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808417;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808418;

public class RightsActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener,AdapterView.OnItemClickListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.list_rights)
    ListView listRights;
    @BindView(R.id.swipe_container)
    RefreshLayout swipeContainer;

    TextView txtFhq;
    TextView txtPool;
    TextView txtEarnings;
    TextView txtTurnover;

    List<RightsModel> list;
    RightsAdapter adapter;

    private View headView;

    private int page = 1;
    private int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rights);
        ButterKnife.bind(this);

        inits();
        initHeadView();
        initsListView();
        initRefreshLayout();

        getData();
        getTotal();
        getLimit();
        getProperty();
    }

    private void inits() {
        list = new ArrayList<>();
        adapter = new RightsAdapter(this,list);
    }

    private void initHeadView() {
        headView = LayoutInflater.from(this).inflate(R.layout.head_rights,null);

        txtFhq = (TextView) headView.findViewById(R.id.txt_fhq);
        txtPool = (TextView) headView.findViewById(R.id.txt_pool);
        txtEarnings = (TextView) headView.findViewById(R.id.txt_earnings);
        txtTurnover = (TextView) headView.findViewById(R.id.txt_turnover);
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

    private void getTotal() {
        JSONObject object = new JSONObject();
        try {
            object.put("accountNumber", "A2017100000000000002");
            object.put("token", userInfoSp.getString("token",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802502, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    txtPool.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("amount")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RightsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RightsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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

        new Xutil().post(CODE_808275, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    txtEarnings.setText(NumberUtil.doubleFormatMoney(jsonObject.getInt("stockCount")*150000-jsonObject.getDouble("totalStockProfit")));
                    txtFhq.setText(jsonObject.getInt("stockCount")+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RightsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RightsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询所需消费额
     */
    private void getLimit() {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808418, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    txtTurnover.setText(NumberUtil.doubleFormatMoney(500000 - jsonObject.getDouble("costAmount")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RightsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RightsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808417, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    Gson gson = new Gson();
                    ArrayList<RightsModel> lists = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<RightsModel>>() {
                    }.getType());

                    list.clear();
                    list.addAll(lists);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RightsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RightsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
//                page = 1;
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
//                page = page + 1;
                getData();


            }
        }, 1500);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i > 0){
            startActivity(new Intent(RightsActivity.this, RightsListActivity.class)
                    .putExtra("received", NumberUtil.doubleFormatMoney(list.get(i-1).getBackAmount()))
                    .putExtra("unclaimed", NumberUtil.doubleFormatMoney(list.get(i-1).getProfitAmount() - list.get(i-1).getBackAmount()))
                    .putExtra("date",list.get(i-1).getCreateDatetime())
                    .putExtra("code",list.get(i-1).getCode()));
        }
    }
}
