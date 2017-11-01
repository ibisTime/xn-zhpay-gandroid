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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805043;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_807717;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808219;

public class LoginActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_register)
    TextView txtRegister;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.txt_forget)
    TextView txtForget;
    @BindView(R.id.box_remenber)
    CheckBox boxRemenber;

    public static LoginActivity instance;

    private boolean isNeedIdentity = false;

    SharedPreferences.Editor editor;
    private boolean isRemenberPwd = false;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(LoginActivity.this, "暂时无法连接，请稍候重试!", Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        getDatas();

        boxRemenber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRemenberPwd = b;
                editor.putBoolean("isRemenberPwd",isRemenberPwd);
                editor.commit();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        edtPhone.setText(userInfoSp.getString("loginName",""));
        edtPassword.setText(userInfoSp.getString("loginPwd",""));
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
        instance = this;

        editor = userInfoSp.edit();
        isRemenberPwd = userInfoSp.getBoolean("isRemenberPwd",false);
        boxRemenber.setChecked(isRemenberPwd);

    }

    @OnClick({R.id.layout_back, R.id.txt_register, R.id.btn_login, R.id.txt_forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;

            case R.id.btn_login:
                if(edtPhone.getText().toString().trim().length() != 11){
                    Toast.makeText(LoginActivity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtPassword.getText().toString().trim().length() == 0){
                    Toast.makeText(LoginActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                login();
                break;

            case R.id.txt_forget:
                startActivity(new Intent(LoginActivity.this,ModifyPasswordActivity.class));
                break;
        }
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    editor.putString("userId",jsonObject.getString("userId"));
                    editor.putString("token",jsonObject.getString("token"));

                    if(isRemenberPwd){
                        editor.putString("loginName", edtPhone.getText().toString().trim());
                        editor.putString("loginPwd", edtPassword.getText().toString().trim());
                    }else {
                        editor.putString("loginName", "");
                        editor.putString("loginPwd", "");
                    }
                    editor.commit();

                    // 收起键盘
                    InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }

                    } catch (JSONException e) {
                    e.printStackTrace();
                }

//                finish();
                getData();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
                        startActivity(new Intent(LoginActivity.this,Main2Activity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this,StoreContract2Activity.class));
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(LoginActivity.this, AuthenticateActivity.class));
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
                Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

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
