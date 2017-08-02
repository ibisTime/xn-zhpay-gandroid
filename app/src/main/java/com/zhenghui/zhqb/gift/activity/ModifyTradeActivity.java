package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805045;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805057;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805904;

public class ModifyTradeActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.txt_tip)
    TextView txtTip;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.layout_code)
    RelativeLayout layoutCode;

    private SharedPreferences preferences;
    private SharedPreferences userInfoSp;

    private boolean isModify;

    // 验证码是否已发送 未发送false 已发送true
    private boolean isCodeSended = false;

    private int i = 60;
    private Timer timer;
    private TimerTask task;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            btnSend.setText(i + "秒后重发");
            if (msg.arg1 == 0) {
                stopTime();
            } else {
                startTime();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_trade);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        txtPhone.setText(getIntent().getStringExtra("phone"));
        isModify = getIntent().getBooleanExtra("isModify", false);

//        if (isModify) {
//            txtTitle.setText("修改支付密码");
//        }else{
//        txtTitle.setText("设置支付密码");
//        }

        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        preferences = getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    @OnClick({R.id.layout_back, R.id.btn_send, R.id.btn_confirm, R.id.layout_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.btn_send:
                    if (isCodeSended) {
                        layoutCode.setVisibility(View.VISIBLE);
                        //                    Toast.makeText(ModifyPasswordActivity.this, "验证码每60秒发送发送一次", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edtPassword.getText().toString().trim().length() < 6) {
                            Toast.makeText(ModifyTradeActivity.this, "请填写正确的支付密码", Toast.LENGTH_SHORT).show();
                        }else {
                            sendCode();
                        }
                    }

                break;

            case R.id.btn_confirm:

                if (edtCode.getText().toString().trim().length() != 4) {
                    Toast.makeText(ModifyTradeActivity.this, "请填写正确的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                set();

                break;

            case R.id.layout_code:
                if (layoutCode.getVisibility() == View.VISIBLE) {
//                    layoutCode.setVisibility(View.GONE);
                }
        }
    }

    private void sendCode() {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", txtPhone.getText().toString().trim());
            if (isModify) {
                object.put("bizType", CODE_805057);
            } else {
                object.put("bizType", CODE_805045);
            }
            object.put("kind", "f3");
            object.put("systemCode", preferences.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_805904, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                isCodeSended = true;
                startTime();
                layoutCode.setVisibility(View.VISIBLE);
                Toast.makeText(ModifyTradeActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ModifyTradeActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ModifyTradeActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void set() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));
            if (isModify) {
                object.put("newTradePwd", edtPassword.getText().toString().trim());
            } else {
                object.put("tradePwd", edtPassword.getText().toString().trim());
            }

            object.put("smsCaptcha", edtCode.getText().toString().toString());
            object.put("tradePwdStrength", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String code = "";
        if (isModify) {
            code = CODE_805057;
        } else {
            code = CODE_805045;
        }

        new Xutil().post(code, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
//                if (isModify) {
//                    Toast.makeText(ModifyTradeActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//                } else {
                Toast.makeText(ModifyTradeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
//                }

                SharedPreferences.Editor editor = userInfoSp.edit();
                editor.putString("tradepwdFlag","1");
                editor.commit();

                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ModifyTradeActivity.this, tip, Toast.LENGTH_SHORT).show();
                layoutCode.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ModifyTradeActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
                layoutCode.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 验证码发送倒计时
     */
    private void startTime() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                i--;
                Message message = handler.obtainMessage();
                message.arg1 = i;
                handler.sendMessage(message);
            }

        };

        timer.schedule(task, 1000);
    }

    private void stopTime() {
        isCodeSended = false;
        i = 60;
        btnSend.setText("向密保手机发送验证码");
        timer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(layoutCode.getVisibility() != View.VISIBLE){
                finish();
            } else {
                layoutCode.setVisibility(View.GONE);
            }
        }
        return false;
    }
}
