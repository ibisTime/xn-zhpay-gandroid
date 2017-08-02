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

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808054;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808066;

public class ShipmentsActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_explain)
    TextView txtExplain;
    @BindView(R.id.txt_product)
    TextView txtProduct;
    @BindView(R.id.layout_product)
    LinearLayout layoutProduct;
    @BindView(R.id.edt_express)
    EditText edtExpress;
    @BindView(R.id.txt_express)
    TextView txtExpress;
    @BindView(R.id.layout_express)
    LinearLayout layoutExpress;
    @BindView(R.id.edt_number)
    EditText edtNumber;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_cancel)
    TextView txtCancel;

    private String code;
    private boolean input;
    private SharedPreferences appConfigSp;
    private SharedPreferences userInfoSp;

    private OrderModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipments);
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
        input = getIntent().getBooleanExtra("input", false);
        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);

        if (input) {
            txtCancel.setVisibility(View.GONE);
            txtConfirm.setVisibility(View.GONE);
        }

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
                Toast.makeText(ShipmentsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ShipmentsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void shipments() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("logisticsCompany", edtExpress.getText().toString().trim());
            object.put("logisticsCode", edtNumber.getText().toString().trim());
            object.put("deliverer", userInfoSp.getString("userId", null));
            object.put("deliveryDatetime", df.format(new Date()));
            object.put("pdf", "");
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("remark", "");
            object.put("token", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808054, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                Toast.makeText(ShipmentsActivity.this, "发货成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ShipmentsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ShipmentsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {

        txtName.setText(model.getReceiver());
        txtPhone.setText(model.getReMobile());
        txtAddress.setText(model.getReAddress());
        txtExplain.setText(model.getApplyNote());
        if (input) {
//            edtNumber.setText(model.getLogisticsCode());
//            edtExpress.setText(model.getLogisticsCompany());

            edtNumber.setFocusable(false);
            edtExpress.setFocusable(false);
        }
        TextView view = new TextView(this);
        view.setText(model.getProduct().getName() + " X " + model.getQuantity());
        view.setTextColor(getResources().getColor(R.color.fontColor_gray));
        layoutProduct.addView(view);

    }

    @OnClick({R.id.layout_back, R.id.txt_confirm, R.id.txt_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.txt_confirm:
                if (check()) {
                    shipments();
                }

                break;

            case R.id.txt_cancel:
                orderCancle();
                break;
        }
    }

    private boolean check() {
        if (edtExpress.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写快递公司", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtNumber.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写运单号", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void orderCancle() {

        JSONArray array = new JSONArray();
        array.put(code);

        JSONObject object = new JSONObject();
        try {
            object.put("codeList", array);
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("remark", "商户取消订单");
            object.put("token", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808056, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                Toast.makeText(ShipmentsActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ShipmentsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ShipmentsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
