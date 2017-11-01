package com.zhenghui.zhqb.gift.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.UserModel;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805041;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805043;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_807717;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808219;

public class RegisterActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.edt_userReferee)
    EditText edtUserReferee;
    @BindView(R.id.edt_referrer)
    EditText edtReferrer;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.txt_treaty)
    TextView txtTreaty;

    private boolean isNeedIdentity = false;

    // 验证码是否已发送 未发送false 已发送true
    private boolean isCodeSended = false;

    private SharedPreferences appConfigSp;
    private SharedPreferences userInfoSp;

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

    private Handler EBhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(RegisterActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setWindow();
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        getDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void setWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void inits() {
        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    @OnClick({R.id.layout_back, R.id.btn_send, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.btn_send:
                if (isCodeSended) {
                    Toast.makeText(RegisterActivity.this, "验证码每60秒发送发送一次", Toast.LENGTH_SHORT).show();
                } else {
                    if (edtPhone.getText().toString().trim().length() != 11) {
                        Toast.makeText(RegisterActivity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendCode();
                }

                break;

            case R.id.btn_register:
                if (edtPhone.getText().toString().trim().length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtCode.getText().toString().trim().length() != 4) {
                    Toast.makeText(RegisterActivity.this, "请填写正确的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().trim().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "密码不能小于6位", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (edtUserReferee.getText().toString().trim().length() != 11) {
//                    Toast.makeText(RegisterActivity.this, "请填写推荐人手机号", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                register();
                break;
        }
    }

    private void register() {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", edtPhone.getText().toString().trim());
            object.put("loginPwd", edtPassword.getText().toString().trim());
            object.put("smsCaptcha", edtCode.getText().toString().trim());
            object.put("loginPwdStrength", "1");
            object.put("userReferee", edtUserReferee.getText().toString().trim());
            object.put("kind", "f3");
            object.put("isRegHx", "1");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("object.toString()=" + object.toString());

        new Xutil().post(CODE_805041, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(RegisterActivity.this, "注册成功,为您自动登录", Toast.LENGTH_SHORT).show();
                login();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendCode() {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", edtPhone.getText().toString().trim());
            object.put("bizType", CODE_805041);
            object.put("kind", "f1");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post("805904", object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                isCodeSended = true;
                startTime();
                Toast.makeText(RegisterActivity.this, "短信已发送，请注意查收", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(){
        JSONObject object = new JSONObject();
        try {
            object.put("loginName",edtPhone.getText().toString().trim());
            object.put("loginPwd",edtPassword.getText().toString().trim());
            object.put("kind","f3");
            object.put("companyCode","");
            object.put("systemCode",appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_805043,object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                SharedPreferences.Editor editor = userInfoSp.edit();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    editor.putString("userId",jsonObject.getString("userId"));
                    editor.putString("token",jsonObject.getString("token"));
                    editor.commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getData();

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getStroe() {
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.length() != 0) {
                        startActivity(new Intent(RegisterActivity.this,Main2Activity.class));
                    } else {
                        startActivity(new Intent(RegisterActivity.this,StoreContract2Activity.class));
                    }
                    finish();
                    if(LoginActivity.instance != null){
                        LoginActivity.instance.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取用户详情
     */
    private void getData() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_805056, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    UserModel model = gson.fromJson(jsonObject.toString(), new TypeToken<UserModel>() {
                    }.getType());

                    if (isNeedIdentity){
                        if (model.getIdentityFlag().equals("1")) {
                            getStroe();
                        }else {
                            startActivity(new Intent(RegisterActivity.this, AuthenticateActivity.class));
                        }
                    }else {
                        getStroe();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 是否需要强制认证
     */
    public void getDatas() {
        JSONObject object = new JSONObject();
        try {
            object.put("ckey", "AUTH_BUSER_IN_APP");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_807717, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (!TextUtils.isEmpty(jsonObject.getString("cvalue"))){
                        if (jsonObject.getString("cvalue").equals("0")){
                            isNeedIdentity = false;
                        }else {
                            isNeedIdentity = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(RegisterActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
        btnSend.setText("重新发送");
        timer.cancel();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tip();
        }
        return false;
    }

    private void tip() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("您确定要退出"+getString(R.string.app_name)+"吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("取消", null).show();
    }

}
