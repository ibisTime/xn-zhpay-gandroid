package com.zhenghui.zhqb.gift.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.OrderModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
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

public class OrderDetailActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_order)
    TextView txtOrder;
    @BindView(R.id.txt_receiver)
    TextView txtReceiver;
    @BindView(R.id.txt_re_mobile)
    TextView txtReMobile;
    @BindView(R.id.txt_re_address)
    TextView txtReAddress;
    @BindView(R.id.txt_good_name)
    TextView txtGoodName;
    @BindView(R.id.txt_parameter_name)
    TextView txtParameterName;
    @BindView(R.id.txt_number)
    TextView txtNumber;
    @BindView(R.id.txt_total_number)
    TextView txtTotalNumber;
    @BindView(R.id.edt_express)
    EditText edtExpress;
    @BindView(R.id.edt_number)
    EditText edtNumber;
    @BindView(R.id.txt_send)
    Button txtSend;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.layout_dfh)
    LinearLayout layoutDfh;
    @BindView(R.id.txt_dsh_logistics)
    TextView txtDshLogistics;
    @BindView(R.id.txt_dsh_send_time)
    TextView txtDshSendTime;
    @BindView(R.id.layout_dsh)
    LinearLayout layoutDsh;
    @BindView(R.id.txt_ywc_logistics)
    TextView txtYwcLogistics;
    @BindView(R.id.txt_ywc_send_time)
    TextView txtYwcSendTime;
    @BindView(R.id.txt_ywc_status)
    TextView txtYwcStatus;
    @BindView(R.id.txt_ywc_get_time)
    TextView txtYwcGetTime;
    @BindView(R.id.layout_ywc)
    LinearLayout layoutYwc;
    @BindView(R.id.txt_note)
    TextView txtNote;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.txt_yunfei)
    TextView txtYunfei;

    String code;

    OrderModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        init();
        getOrder();
    }

    private void init() {
        code = getIntent().getStringExtra("code");
    }

    @OnClick({R.id.layout_back, R.id.txt_send, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_send:
                if (check()) {
                    send();
                }
                break;

            case R.id.btn_cancel:
                cancel();
                break;
        }
    }

    private void getOrder() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("token", userInfoSp.getString("token", null));
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
                Toast.makeText(OrderDetailActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(OrderDetailActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {

        txtOrder.setText("订单号: " + model.getCode());
        txtReceiver.setText("收件人: " + model.getReceiver());
        txtReMobile.setText("联系电话: " + model.getReMobile());
        txtReAddress.setText("收件地址: " + model.getReAddress());
        if (model.getApplyNote() != null) {
            if (!model.getApplyNote().equals("")) {
                txtNote.setVisibility(View.VISIBLE);
                txtNote.setText("买家嘱咐: " + model.getApplyNote());
            }
        }


        txtGoodName.setText(model.getProduct().getName());
        if (model.getProductSpecsName() != null) {
            txtParameterName.setText("规格: " + model.getProductSpecsName());
        } else {
            txtParameterName.setText("规格: 无");
        }
        txtNumber.setText("数量*" + model.getQuantity());

        txtPrice.setText(NumberUtil.doubleFormatMoney(model.getAmount1()));
        txtYunfei.setText(NumberUtil.doubleFormatMoney(model.getYunfei()));

        txtTotalNumber.setText("共计" + 1 + "件货物");

        if (model.getStatus().equals("2")) { // 待发货
            txtTitle.setText("待发货详情");
            layoutDfh.setVisibility(View.VISIBLE);
        } else if (model.getStatus().equals("3")) { // 待收货
            txtTitle.setText("待收货详情");
            layoutDsh.setVisibility(View.VISIBLE);

            txtDshLogistics.setText(model.getLogisticsCompany() + ": " + model.getLogisticsCode());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (model.getDeliveryDatetime() != null) {
                Date sendTime = new Date(model.getDeliveryDatetime());
                txtDshSendTime.setText("发货时间: " + format.format(sendTime));
            }

        } else if (model.getStatus().equals("4")) { // 已完成
            txtTitle.setText("已完成详情");
            layoutYwc.setVisibility(View.VISIBLE);

            txtYwcLogistics.setText(model.getLogisticsCompany() + ": " + model.getLogisticsCode());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (model.getDeliveryDatetime() != null) {
                Date sendTime = new Date(model.getDeliveryDatetime());
                txtYwcSendTime.setText("发货时间: " + format.format(sendTime));
            }
            if (model.getUpdateDatetime() != null) {
                Date getTime = new Date(model.getUpdateDatetime());
                txtYwcGetTime.setText("收货时间: " + format.format(getTime));
            }

        }

    }


    private void send() {

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

                Toast.makeText(OrderDetailActivity.this, "发货成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(OrderDetailActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(OrderDetailActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancel() {

        JSONArray array = new JSONArray();
        array.put(code);

        JSONObject object = new JSONObject();
        try {
            object.put("codeList", array);
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("remark", "商户" + btnCancel.getText());
            object.put("token", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808056, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                Toast.makeText(OrderDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(OrderDetailActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(OrderDetailActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
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

}
