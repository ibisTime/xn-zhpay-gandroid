package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.OrderAdapter;
import com.zhenghui.zhqb.gift.adapter.ProductAdapter;
import com.zhenghui.zhqb.gift.model.OrderModel;
import com.zhenghui.zhqb.gift.model.ProductModel;
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

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808025;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808065;

public class ManageActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener, AdapterView.OnItemClickListener {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_product)
    TextView txtProduct;
    @BindView(R.id.txt_order)
    TextView txtOrder;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.list_manage)
    ListView listManage;
    @BindView(R.id.swipe_container)
    RefreshLayout swipeContainer;

    private boolean isAtProduct = true;

    private List<ProductModel> productList;
    private ProductAdapter productAdapter;

    private List<OrderModel> orderList;
    private OrderAdapter orderAdapter;

    private int productPage = 1;
    private int productPageSize = 10;

    private int orderPage = 1;
    private int orderPageSize = 10;

    private SharedPreferences appConfigSp;
    private SharedPreferences userInfoSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        initEvent();
        initRefreshLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAtProduct) {
            productPage = 1;
            getProduct();
        } else {
            orderPage = 1;
            getOrder();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void initEvent() {
        listManage.setOnItemClickListener(this);
    }

    private void inits() {
        orderList = new ArrayList<>();
        productList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        productAdapter = new ProductAdapter(this, productList);

        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    private void initRefreshLayout() {
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setOnLoadListener(this);
    }

    @OnClick({R.id.layout_back, R.id.txt_product, R.id.txt_order, R.id.img_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_product:
                initButton();
                isAtProduct = true;
                imgAdd.setVisibility(View.VISIBLE);

                txtOrder.setTextColor(getResources().getColor(R.color.fontColor_white));
                txtOrder.setBackground(getResources().getDrawable(R.drawable.corners_orange_right));

                productPage = 1;
                getProduct();

                break;

            case R.id.txt_order:
                initButton();
                isAtProduct = false;
                imgAdd.setVisibility(View.GONE);

                txtProduct.setTextColor(getResources().getColor(R.color.fontColor_white));
                txtProduct.setBackground(getResources().getDrawable(R.drawable.corners_orange_left));

                orderPage = 1;
                getOrder();

                break;

            case R.id.img_add:
                // 不是理财商家
                if(userInfoSp.getString("level","0").equals("2")){ // 是理财商家
                    startActivity(new Intent(ManageActivity.this, ProductActivity.class));
                }else {
                    Toast.makeText(this, "您还不是理财型商家，不能添加商品", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void initButton() {
        txtProduct.setTextColor(getResources().getColor(R.color.fontColor_orange));
        txtProduct.setBackground(getResources().getDrawable(R.drawable.corners_white_left));

        txtOrder.setTextColor(getResources().getColor(R.color.fontColor_orange));
        txtOrder.setBackground(getResources().getDrawable(R.drawable.corners_white_left));
    }

    private void getProduct() {
        JSONObject object = new JSONObject();
        try {
            object.put("category", "");
            object.put("type", "");
            object.put("name", "");
            object.put("status", "");
            object.put("location", "");
            object.put("start", productPage);
            object.put("limit", productPageSize);
            object.put("orderDir", "");
            object.put("orderColumn", "");
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", userInfoSp.getString("userId", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808025, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {


                try {
                    JSONObject jsonObject = new JSONObject(result);


                    Gson gson = new Gson();
                    ArrayList<ProductModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<ProductModel>>() {
                    }.getType());

                    if (productPage == 1) {
                        productList.clear();
                        productList.addAll(lists);
                        listManage.setAdapter(productAdapter);
                        productAdapter.notifyDataSetChanged();
                    }else{
                        productList.addAll(lists);
                        productAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ManageActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ManageActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getOrder() {

        JSONArray jsonArray = new JSONArray();
        jsonArray.put("2");
        jsonArray.put("3");
        jsonArray.put("4");

        JSONObject object = new JSONObject();
        try {
            object.put("statusList", jsonArray);
            object.put("start", orderPage+"");
            object.put("limit", orderPageSize+"");
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

                    if (orderPage == 1) {
                        orderList.clear();
                        orderList.addAll(lists);
                        listManage.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();
                    }else{
                        orderList.addAll(lists);
                        orderAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ManageActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ManageActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
                if (isAtProduct) {
                    productPage = 1;
                    getProduct();
                } else {
                    orderPage = 1;
                    getOrder();
                }

                // 更新数据
                // 更新完后调用该方法结束刷新
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setLoading(false);
                if (isAtProduct) {
                    productPage = productPage + 1;
                    getProduct();
                } else {
                    orderPage = orderPage + 1;
                    getOrder();
                }

            }
        }, 1500);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (isAtProduct) {
            if(productList.size() > 0){
                startActivity(new Intent(ManageActivity.this, ProductActivity.class).putExtra("isModifi", true).putExtra("code", productList.get(i).getCode()));
            }
        } else {
            if(orderList.size() > 0){
                if (orderList.get(i).getStatus().equals("3")) { // 已发货，跳转到取消
                    startActivity(new Intent(ManageActivity.this, ShipmentsCancleActivity.class)
                            .putExtra("code", orderList.get(i).getCode()));
                } else if (orderList.get(i).getStatus().equals("2")) { // 待发货，跳转到发货
                    startActivity(new Intent(ManageActivity.this, ShipmentsActivity.class)
                            .putExtra("code", orderList.get(i).getCode()));
                } else if (orderList.get(i).getStatus().equals("4")) { // 已收货，查看订单信息
                    startActivity(new Intent(ManageActivity.this, ShipmentsActivity.class)
                            .putExtra("input", true)
                            .putExtra("code", orderList.get(i).getCode()));
                }
            }
        }
    }

}
