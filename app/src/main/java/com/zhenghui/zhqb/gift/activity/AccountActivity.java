package com.zhenghui.zhqb.gift.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.UserModel;
import com.zhenghui.zhqb.gift.util.CacheUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_807718;
import static com.zhenghui.zhqb.gift.util.UpdateUtil.startWeb;

public class AccountActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.layout_phone)
    LinearLayout layoutPhone;
    @BindView(R.id.txt_authentication)
    TextView txtAuthentication;
    @BindView(R.id.layout_authentication)
    LinearLayout layoutAuthentication;
    @BindView(R.id.layout_loginPwd)
    LinearLayout layoutLoginPwd;
    @BindView(R.id.layout_payPwd)
    LinearLayout layoutPayPwd;
    @BindView(R.id.txt_tr)
    TextView txtTr;
    @BindView(R.id.layout_bankCard)
    LinearLayout layoutBankCard;
    @BindView(R.id.txt_sign_out)
    TextView txtSignOut;
    @BindView(R.id.txt_cache)
    TextView txtCache;
    @BindView(R.id.layout_cache)
    LinearLayout layoutCache;
    @BindView(R.id.txt_versionName)
    TextView txtVersionName;
    @BindView(R.id.layout_version)
    LinearLayout layoutVersion;
    @BindView(R.id.layout_about)
    LinearLayout layoutAbout;

    private UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
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

        try {
            txtCache.setText(CacheUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtVersionName.setText("V"+getVersionName());
    }

    @OnClick({R.id.layout_back, R.id.layout_phone, R.id.layout_authentication, R.id.layout_loginPwd,
            R.id.layout_payPwd, R.id.layout_bankCard, R.id.txt_sign_out, R.id.layout_about,
            R.id.layout_cache, R.id.layout_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.layout_phone:
                if (model.getTradepwdFlag().equals("0")) {
                    Toast.makeText(this, "请先设置支付密码", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AccountActivity.this, ModifyTradeActivity.class).putExtra("isModify", false).putExtra("phone", model.getMobile()));
                } else {
                    startActivity(new Intent(AccountActivity.this, ModifyPhoneActivity.class).putExtra("phone", model.getMobile()));
                }
                break;

            case R.id.layout_loginPwd:
                startActivity(new Intent(AccountActivity.this, ModifyPasswordActivity.class).putExtra("phone", model.getMobile()));
                break;

            case R.id.layout_payPwd:
                if (model.getTradepwdFlag().equals("0")) { // 未设置支付密码
                    startActivity(new Intent(AccountActivity.this, ModifyTradeActivity.class).putExtra("isModify", false).putExtra("phone", model.getMobile()));
                } else {
                    startActivity(new Intent(AccountActivity.this, ModifyTradeActivity.class).putExtra("isModify", true).putExtra("phone", model.getMobile()));
                }
                break;

            case R.id.layout_authentication:
                if (txtAuthentication.getText().toString().trim().equals("")) {
                    startActivity(new Intent(AccountActivity.this, AuthenticateActivity.class));
                } else {
                    Toast.makeText(this, "您已实名认证", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.layout_bankCard:
                if (model.getTradepwdFlag().equals("0")) {
                    Toast.makeText(this, "请先设置支付密码", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AccountActivity.this, ModifyTradeActivity.class).putExtra("isModify", false).putExtra("phone", model.getMobile()));
                } else {
                    startActivity(new Intent(AccountActivity.this, BankCardActivity.class));
                }

                break;

            case R.id.txt_sign_out:
//                SharedPreferences.Editor editor = userInfoSp.edit();
//                editor.putString("userId", null);
//                editor.putString("token", null);
//                editor.commit();
                logOut();
                break;

            case R.id.layout_about:
                startActivity(new Intent(AccountActivity.this, RichTextActivity.class).putExtra("ckey","aboutus"));
                break;

            case R.id.layout_cache:
                clearCache();
                break;

            case R.id.layout_version:
                getVersion();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
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
                    model = gson.fromJson(jsonObject.toString(), new TypeToken<UserModel>() {
                    }.getType());

                    SharedPreferences.Editor editor = userInfoSp.edit();
                    editor.putString("identityFlag", model.getIdentityFlag());
                    editor.putString("tradepwdFlag", model.getTradepwdFlag());

                    editor.commit();

                    setView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(AccountActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(AccountActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {
        txtPhone.setText(model.getMobile());

        if (model.getRealName() != null) {

            txtAuthentication.setText(model.getRealName());

        }
    }

    private void clearCache() {
        CacheUtil.clearAllCache(this);
        try {
            txtCache.setText(CacheUtil.getTotalCacheSize(this));
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getVersion() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "android_g");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_807718, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String note = jsonObject.getString("note");
                    String url = jsonObject.getString("downloadUrl");
                    String force = jsonObject.getString("forceUpdate");
                    int versionCode = Integer.parseInt(jsonObject.getString("version"));

                    if (versionCode > getVersionCode()) {
                        update(note, url, force);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(AccountActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(AccountActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(String msg, final String url, String force) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startWeb(AccountActivity.this,url);

                        finish();
                        System.exit(0);

                    }
                })
                .setCancelable(false);


        if(force.equals("1")){ // 强制更新
            builder.show();
        }else {
            builder.setNegativeButton("取消", null).show();
        }
    }

    /**
     * 退出登录
     */
    private void logOut() {

        RequestParams params = new RequestParams(Xutil.LOGOUT);
        params.addBodyParameter("token", userInfoSp.getString("token", null));

        System.out.println("url=" +  Xutil.LOGOUT);
        System.out.println("token=" + userInfoSp.getString("token", null));

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {

                System.out.println("result=" + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    System.out.println("jsonObject.getJSONObject(\"data\").getBoolean(\"isSuccess\")=" + jsonObject.getJSONObject("data").getBoolean("isSuccess"));

                    if (jsonObject.getJSONObject("data").getBoolean("isSuccess")) {
                        SharedPreferences.Editor editor = userInfoSp.edit();
                        editor.putString("userId", null);
                        editor.putString("token", null);
                        editor.commit();

                        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                        AccountActivity.this.finish();
                        Main2Activity.instance.finish();
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("onError=" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
