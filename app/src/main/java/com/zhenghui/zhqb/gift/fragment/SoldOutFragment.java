package com.zhenghui.zhqb.gift.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.activity.ProductActivity;
import com.zhenghui.zhqb.gift.adapter.SoldOutAdapter;
import com.zhenghui.zhqb.gift.model.ProductModel;
import com.zhenghui.zhqb.gift.util.RefreshLayout;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808025;

public class SoldOutFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener, AdapterView.OnItemClickListener {

    @BindView(R.id.grid_product)
    GridView gridProduct;
    @BindView(R.id.layout_refresh)
    RefreshLayout layoutRefresh;

    // Fragment主视图
    private View view;

    private int page = 1;
    private int pageSize = 100;

    private List<ProductModel> list;
    private SoldOutAdapter adapter;

    private SharedPreferences appConfigSp;
    private SharedPreferences userInfoSp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sell, null);
        ButterKnife.bind(this, view);

        inits();
        initGridView();
        initRefreshLayout();

        getProduct();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        getProduct();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            page = 1;
            getProduct();
        }
    }

    private void inits() {
        list = new ArrayList<>();
        adapter = new SoldOutAdapter(getActivity(),list);

        userInfoSp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getActivity().getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    private void initGridView() {
        gridProduct.setAdapter(adapter);
        gridProduct.setOnItemClickListener(this);
    }

    private void initRefreshLayout() {
        layoutRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        layoutRefresh.setOnRefreshListener(this);
        layoutRefresh.setOnLoadListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(list.get(i).isAdd()){
            startActivity(new Intent(getActivity(), ProductActivity.class));
        }else {
            startActivity(new Intent(getActivity(), ProductActivity.class)
                    .putExtra("code",list.get(i).getCode())
                    .putExtra("isModifi",true));
        }
    }

    private void getProduct() {
        JSONObject object = new JSONObject();
        try {
            object.put("category", "");
            object.put("type", "");
            object.put("name", "");
            object.put("status", "4"); // 已下架
            object.put("location", "");
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


        new Xutil().post(CODE_808025, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    ArrayList<ProductModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<ProductModel>>() {
                    }.getType());

                    if (page == 1) {
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
                Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(getActivity(), "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        layoutRefresh.postDelayed(new Runnable() {

            @Override
            public void run() {
                layoutRefresh.setRefreshing(false);
                getProduct();
                // 更新数据
                // 更新完后调用该方法结束刷新
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        layoutRefresh.postDelayed(new Runnable() {

            @Override
            public void run() {
                layoutRefresh.setLoading(false);
                getProduct();
            }
        }, 1500);
    }

}
