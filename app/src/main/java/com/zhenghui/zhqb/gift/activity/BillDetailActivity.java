package com.zhenghui.zhqb.gift.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.BillDetailModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802522;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_802532;

public class BillDetailActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.img_photo)
    ImageView imgPhoto;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.txt_currency)
    TextView txtCurrency;
    @BindView(R.id.txt_info)
    TextView txtInfo;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_orderId)
    TextView txtOrderId;

    boolean isHistory;
    String accountNumber;

    BillDetailModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        ButterKnife.bind(this);

        init();

        getData();
    }

    private void init() {
        accountNumber = getIntent().getStringExtra("code");
        isHistory = getIntent().getBooleanExtra("isHistory", false);
    }

    @OnClick(R.id.layout_back)
    public void onClick() {
        finish();
    }

    private void getData() {

        JSONObject object = new JSONObject();
        try {
            object.put("code", accountNumber);
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String code = "";
        if (isHistory) {
            code = CODE_802532;
        } else {
            code = CODE_802522;
        }

        new Xutil().post(code, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    model = gson.fromJson(jsonObject.toString(), new TypeToken<BillDetailModel>() {
                    }.getType());

                    setView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(BillDetailActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(BillDetailActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {
        txtPhone.setText(model.getRealName());
        txtPrice.setText(NumberUtil.doubleFormatMoney(model.getTransAmount()));
//        txtCurrency.setText(BillUtil.getCurrency(model.getCurrency()));
        txtInfo.setText(model.getBizNote());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        txtTime.setText(format.format(new Date(model.getCreateDatetime())));
        txtOrderId.setText(model.getRefNo());
    }

}
