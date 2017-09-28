package com.zhenghui.zhqb.gift.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.WalletModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802503;

public class WalletActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_lpq)
    TextView txtLpq;
    @BindView(R.id.layout_lpq)
    LinearLayout layoutLpq;
    @BindView(R.id.txt_frb)
    TextView txtFrb;
    @BindView(R.id.layout_frb)
    LinearLayout layoutFrb;
    @BindView(R.id.txt_btb)
    TextView txtBtb;
    @BindView(R.id.layout_btb)
    LinearLayout layoutBtb;
    @BindView(R.id.txt_hkb)
    TextView txtHkb;
    @BindView(R.id.layout_hkb)
    LinearLayout layoutHkb;

    private double frb;
    private double lpq;
    private double btb;
    private double hkb;

    private String btbCode;
    private String frbCode;
    private String lpqCode;
    private String hkbCode;

    private List<WalletModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        init();
        getMoney();

    }

    private void init() {
        list = new ArrayList<>();
    }

    @OnClick({R.id.layout_back, R.id.layout_lpq, R.id.layout_frb, R.id.layout_btb, R.id.layout_hkb})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.layout_frb:
                startActivity(new Intent(this, BillActivity.class)
                        .putExtra("code", frbCode)
                        .putExtra("accountAmount", frb)
                        .putExtra("accountName", "frb"));
                break;

            case R.id.layout_lpq:
                startActivity(new Intent(this, BillActivity.class)
                        .putExtra("code", lpqCode)
                        .putExtra("accountAmount", lpq)
                        .putExtra("accountName", "lpq"));
                break;

            case R.id.layout_btb:
                startActivity(new Intent(this, BillActivity.class)
                        .putExtra("code", btbCode)
                        .putExtra("accountAmount", btb)
                        .putExtra("accountName", "btb"));
                break;

            case R.id.layout_hkb:
                startActivity(new Intent(this, BillActivity.class)
                        .putExtra("code", hkbCode)
                        .putExtra("accountAmount", hkb)
                        .putExtra("accountName", "btb"));
                break;
        }
    }

    private void getMoney() {
        JSONObject object = new JSONObject();
        try {
            object.put("token", userInfoSp.getString("token", null));
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802503, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    Gson gson = new Gson();
                    List<WalletModel> lists = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<WalletModel>>() {
                    }.getType());

                    list.addAll(lists);
                    setMoney();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(WalletActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(WalletActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMoney() {
        for (WalletModel model : list) {
            switch (model.getCurrency()) {
                case "CNY": // 人名币
                    break;

                case "FRB": // 分润
                    txtFrb.setText(NumberUtil.doubleFormatMoney(model.getAmount()));
                    frb = model.getAmount();
                    frbCode = model.getAccountNumber();
                    break;


                case "LPQ": // 礼品券
                    txtLpq.setText(NumberUtil.doubleFormatMoney(model.getAmount()));
                    lpq = model.getAmount();

                    lpqCode = model.getAccountNumber();
                    break;

                case "BTB": // 补贴
                    txtBtb.setText(NumberUtil.doubleFormatMoney(model.getAmount()));
                    btb = model.getAmount();

                    btbCode = model.getAccountNumber();
                    break;

                case "HKB": // 货款
                    txtHkb.setText(NumberUtil.doubleFormatMoney(model.getAmount()));
                    hkb = model.getAmount();

                    hkbCode = model.getAccountNumber();
                    break;
            }
        }
    }
}
