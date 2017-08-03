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

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805048;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805904;

public class ModifyPasswordActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
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
        setContentView(R.layout.activity_modify_password);
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
        if(getIntent().getStringExtra("phone") != null){
            edtPhone.setText(getIntent().getStringExtra("phone"));
            edtPhone.setKeyListener(null);
        }

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
                    if(edtPhone.getText().toString().trim().length() != 11){
                        Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();

                    }else {
                        if (edtPassword.getText().toString().trim().length() < 6) {
                            Toast.makeText(ModifyPasswordActivity.this, "请填写正确的登陆密码", Toast.LENGTH_SHORT).show();
                        }else {
                            sendCode();
                        }
                    }

                }

                break;

            case R.id.btn_confirm:
                if(check()){
                    modify();
                }

                break;

            case R.id.layout_code:
                if (layoutCode.getVisibility() == View.VISIBLE) {
//                    layoutCode.setVisibility(View.GONE);
                }
                break;

        }
    }

    private boolean check(){
        if(edtPhone.getText().toString().trim().length() != 11){
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return false;    
        }
        if (edtCode.getText().toString().trim().length() != 4) {
            Toast.makeText(ModifyPasswordActivity.this, "请填写正确的验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void modify() {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", edtPhone.getText().toString().trim());
            object.put("newLoginPwd", edtPassword.getText().toString().trim());
            object.put("smsCaptcha", edtCode.getText().toString().toString());
            object.put("loginPwdStrength", "1");
            object.put("kind", "f3");
            object.put("systemCode", preferences.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_805048, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ModifyPasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ModifyPasswordActivity.this, tip, Toast.LENGTH_SHORT).show();
                layoutCode.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ModifyPasswordActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
                layoutCode.setVisibility(View.GONE);
            }
        });

    }

    /**
     *
     */
    private void sendCode() {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", edtPhone.getText().toString().trim());
            object.put("bizType", CODE_805048);
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
                Toast.makeText(ModifyPasswordActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ModifyPasswordActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ModifyPasswordActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
