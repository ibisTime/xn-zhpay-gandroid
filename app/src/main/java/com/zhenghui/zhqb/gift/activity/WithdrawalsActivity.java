package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
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
import com.zhenghui.zhqb.gift.model.BankModel;
import com.zhenghui.zhqb.gift.model.MyBankCardModel;
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

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802015;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_802029;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_802750;

public class WithdrawalsActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_bankCard)
    TextView txtBankCard;
    @BindView(R.id.layout_bankCard)
    LinearLayout layoutBankCard;
    @BindView(R.id.edt_price)
    EditText edtPrice;
    @BindView(R.id.txt_canUsePrice)
    TextView txtCanUsePrice;
    @BindView(R.id.txt_tip4)
    TextView txtTip4;
    @BindView(R.id.edt_repassword)
    EditText edtRepassword;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;
    @BindView(R.id.txt_tip)
    TextView txtTip;
    @BindView(R.id.txt_tip2)
    TextView txtTip2;
    @BindView(R.id.txt_tip3)
    TextView txtTip3;

    private List<BankModel> list;
    private List<MyBankCardModel> bankCardList;

    private SharedPreferences userInfoSp;
    private SharedPreferences appConfigSp;

    private double balance;
    private String accountNumber;

    private String bankName;
    private String bankcardNumber;

    private double USERQXFL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawals);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        initEditText();

        getTip();
        getList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        list = new ArrayList<>();
        bankCardList = new ArrayList<>();

        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);

        balance = getIntent().getDoubleExtra("balance", 0.00);
        accountNumber = getIntent().getStringExtra("accountNumber");

        txtCanUsePrice.setText("可提现金额" + NumberUtil.doubleFormatMoney(balance) + "元");
    }

    private void initEditText() {
        edtPrice.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        //设置字符过滤
        edtPrice.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});

        edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().trim().equals("")){
                   txtTip4.setText("* 本次提现手续费:"+NumberUtil.doubleFormatGps((Double.parseDouble(editable.toString()) * USERQXFL)));
                }else {
                    txtTip4.setText("* 本次提现手续费:0");
                }
            }
        });
    }

    @OnClick({R.id.layout_back, R.id.layout_bankCard, R.id.txt_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.layout_bankCard:
//                chooseBankCard();
                startActivityForResult(new Intent(WithdrawalsActivity.this, BankCardActivity.class).putExtra("isWithdrawal", true), 0);

                break;

            case R.id.txt_confirm:

                if (!edtPrice.getText().toString().toString().equals("")) {
                    if (Double.parseDouble(edtPrice.getText().toString().trim()) == 0.0) {
                        Toast.makeText(WithdrawalsActivity.this, "金额必须大于等于0.01元", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtBankCard.getText().toString().equals("选择银行卡")) {
                            Toast.makeText(WithdrawalsActivity.this, "请先选择银行卡", Toast.LENGTH_SHORT).show();
                        } else {
                            if (edtRepassword.getText().toString().length() < 6) {
                                Toast.makeText(WithdrawalsActivity.this, "请填写正确格式的支付密码", Toast.LENGTH_SHORT).show();
                            } else {
                                withdrawal();
                            }
                        }
                    }
                } else {
                    Toast.makeText(WithdrawalsActivity.this, "请输入提现金额", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!data.getStringExtra("bankName").equals("")) {
            bankName = data.getStringExtra("bankName");
            bankcardNumber = data.getStringExtra("bankcardNumber");

            txtBankCard.setText(bankName);
        }

    }

    private void getList() {

        JSONObject object = new JSONObject();
        try {
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("token", userInfoSp.getString("token", null));
            object.put("bankcardNumber", "");
            object.put("bankName", "");
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("realName", "");
            object.put("type", "");
            object.put("status", "1");
            object.put("start", "1");
            object.put("limit", "1");
            object.put("orderColumn", "");
            object.put("orderDir", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802015, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    ArrayList<MyBankCardModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<MyBankCardModel>>() {
                    }.getType());

                    bankCardList.addAll(lists);
                    if (bankCardList.size() > 0) {

                        bankName = bankCardList.get(0).getBankName();
                        bankcardNumber = bankCardList.get(0).getBankcardNumber();

                        System.out.println("bankName=" + bankName);
                        System.out.println("bankcardNumber=" + bankcardNumber);

                        txtBankCard.setText(bankCardList.get(0).getBankName());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(WithdrawalsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(WithdrawalsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTip() {

        JSONObject object = new JSONObject();
        try {
            object.put("type", "G_RMB");
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802029, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    txtTip.setText("1.每月最大取现次数为"+jsonObject.getString("USER_MONTIMES")+"次");
                    txtTip2.setText("2.提现金额是" + jsonObject.getString("USER_QXBS") + "的倍数，单笔最高" + jsonObject.getString("USER_QXDBZDJE"));
                    txtTip3.setText("3.取现手续费:" + (jsonObject.getDouble("USER_QXFL")*100)+"%");

                    USERQXFL = jsonObject.getDouble("USER_QXFL");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(WithdrawalsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(WithdrawalsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void withdrawal() {

        JSONArray accountNumberList = new JSONArray();
        accountNumberList.put(accountNumber);

        JSONObject object = new JSONObject();
        try {
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("token", userInfoSp.getString("token", null));
            object.put("accountNumber", accountNumber);
            object.put("amount", (int) (Double.parseDouble(edtPrice.getText().toString().trim()) * 1000));
            object.put("payCardNo", bankcardNumber);
            object.put("payCardInfo", bankName);
            object.put("applyNote", "Android礼品商端取现");
            object.put("applyUser", userInfoSp.getString("userId", null));
            object.put("tradePwd", edtRepassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802750, object.toString(), new Xutil.XUtils3CallBackPost() {

            @Override
            public void onSuccess(String result) {
                Toast.makeText(WithdrawalsActivity.this, "提现申请成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(WithdrawalsActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(WithdrawalsActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
