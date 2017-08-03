package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.OrderModel;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808066;

public class ShipmentsCancleActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_explain)
    TextView txtExplain;
    @BindView(R.id.layout_product)
    LinearLayout layoutProduct;
    @BindView(R.id.edt_reason)
    EditText edtReason;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;
    @BindView(R.id.txt_phone)
    TextView txtPhone;

    private String code;
    private SharedPreferences appConfigSp;
    private SharedPreferences userInfoSp;

    private OrderModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipments_cancle);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        code = getIntent().getStringExtra("code");
        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    private void getData() {

        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("token", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808066, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    model = gson.fromJson(jsonObject.toString(), new TypeToken<OrderModel>() {
                    }.getType());

                    setView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ShipmentsCancleActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ShipmentsCancleActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void shipmentsCancle() {

        JSONArray array = new JSONArray();
        array.put(code);

        JSONObject object = new JSONObject();
        try {
            object.put("codeList", array);
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("remark", edtReason.getText().toString().trim());
            object.put("token", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808056, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                Toast.makeText(ShipmentsCancleActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ShipmentsCancleActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ShipmentsCancleActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setView() {

        //        txtName.setText(model.getReceiver());
        //        txtPhone.setText(model.getMobile());
        //        txtAddress.setText(model.getReAddress());
        //        txtExplain.setText(model.getApplyNote());
        //
        //        for (int i = 0; i < model.getProductOrderList().size(); i++) {
        //            TextView view = new TextView(this);
        //            view.setText(model.getProductOrderList().get(i).getProduct().getName() + " X " + model.getProductOrderList().get(i).getQuantity());
        //            view.setTextColor(getResources().getColor(R.color.fontColor_gray));
        //            layoutProduct.addView(view);
        //        }

    }

    @OnClick({R.id.layout_back, R.id.txt_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_confirm:
                shipmentsCancle();
                break;
        }
    }
}
