package com.zhenghui.zhqb.gift.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.MyStoreModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802502;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808219;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808275;

public class StoreManage2Activity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.switchButton)
    SwitchButton switchButton;
    @BindView(R.id.layout_store)
    RelativeLayout layoutStore;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.txt_type)
    TextView txtType;
    @BindView(R.id.txt_level_up)
    TextView txtLevelUp;
    @BindView(R.id.layout_level)
    RelativeLayout layoutLevel;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.txt_sale)
    TextView txtSale;
    @BindView(R.id.txt_used)
    TextView txtUsed;
    @BindView(R.id.txt_due)
    TextView txtDue;
    @BindView(R.id.txt_earnings)
    TextView txtEarnings;
    @BindView(R.id.txt_fhq)
    TextView txtFhq;
    @BindView(R.id.txt_my_earnings)
    TextView txtMyEarnings;
    @BindView(R.id.txt_my_fhq)
    TextView txtMyFhq;
    @BindView(R.id.layout_fhq)
    LinearLayout layoutFhq;
    @BindView(R.id.txt_deal)
    TextView txtDeal;

    private ArrayList<MyStoreModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_manage2);
        ButterKnife.bind(this);

        inits();
        initEvent();

        getData();
        getTotal();
        getProperty();
    }

    private void inits() {
        list = new ArrayList<>();

    }

    private void initEvent() {
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(0).getStatus().equals("0")
                        || list.get(0).getStatus().equals("4")
                        || list.get(0).getStatus().equals("91")) {
                    Toast.makeText(StoreManage2Activity.this, "店铺还未通过审核或未上架，不能营业", Toast.LENGTH_SHORT).show();
                    switchButton.setChecked(false);
                    return;
                } else {
                    openOrClose();
                }
            }
        });
    }

    @OnClick({R.id.layout_back, R.id.txt_deal, R.id.txt_edit, R.id.layout_fhq, R.id.layout_store})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_edit:
                startActivity(new Intent(this, StoreActivity.class).putExtra("isModifi", true));
                break;

            case R.id.layout_fhq:
                startActivity(new Intent(this, RightsActivity.class));
                break;

            case R.id.txt_deal:
                startActivity(new Intent(this, RichTextActivity.class).putExtra("ckey","store_sign_statement"));
                break;

            case R.id.layout_store:
                startActivity(new Intent(this, StoreActivity.class).putExtra("isModifi", true));
                break;
        }
    }

    private void getTotal() {
        JSONObject object = new JSONObject();
        try {
            object.put("accountNumber", "A2017100000000000002");
            object.put("token", userInfoSp.getString("token", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802502, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    txtEarnings.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("amount")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreManage2Activity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreManage2Activity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {
        JSONObject object = new JSONObject();
        try {
            object.put("token", userInfoSp.getString("token", null));
            object.put("userId", userInfoSp.getString("userId", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808219, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                SharedPreferences.Editor editor = userInfoSp.edit();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Gson gson = new Gson();
                    MyStoreModel model = gson.fromJson(jsonObject.toString(), new TypeToken<MyStoreModel>() {
                    }.getType());

                    list.clear();
                    list.add(model);

                    editor.putString("storeCode", list.get(0).getCode());
                    editor.putString("level", list.get(0).getLevel());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setView();

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreManage2Activity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreManage2Activity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {
        if (list.get(0).getLevel().equals("2")) {
            txtLevelUp.setVisibility(View.GONE);
            txtType.setText("您已经是礼品商家");
            textView2.setText("店铺已升级");
        }

        if (list.get(0).getStatus().equals("0")) {
            txtStatus.setText("待审核");
        } else if (list.get(0).getStatus().equals("1")) {
            txtStatus.setText("审核通过待上架");
            switchButton.setChecked(false);
        } else if (list.get(0).getStatus().equals("2")) {
            txtStatus.setText("店铺营业中,重新编辑后需审核才可上架");
            switchButton.setChecked(true);
        } else if (list.get(0).getStatus().equals("3")) {
            txtStatus.setText("店铺歇业中");
        } else if (list.get(0).getStatus().equals("4")) {
            txtStatus.setText("已下架");
        } else if (list.get(0).getStatus().equals("91")) {
            txtStatus.setText("审核未通过: " + list.get(0).getRemark());
        }
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

                    txtMyEarnings.setText(NumberUtil.doubleFormatMoney(jsonObject.getDouble("totalStockProfit")));
                    txtFhq.setText(jsonObject.getInt("totalStockCount") + "");
                    txtMyFhq.setText(jsonObject.getInt("stockCount") + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreManage2Activity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreManage2Activity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOrClose() {
        JSONObject object = new JSONObject();
        try {
            object.put("token", userInfoSp.getString("token", null));
            object.put("code", list.get(0).getCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post("808206", object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                getData();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreManage2Activity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreManage2Activity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
