package com.zhenghui.zhqb.gift.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805191;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_805192;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808219;

public class AuthenticateActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_identity)
    EditText edtIdentity;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.edt_trade)
    EditText edtTrade;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private String code = "";
    private double price = 0.00;

    private boolean canBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("ZHLPS", "scheme: " + intent.getScheme());
        Uri uri = intent.getData();
        if (uri != null) {
            Log.e("ZHLPS", "scheme: " + uri.getScheme());
            Log.e("ZHLPS", "host: " + uri.getHost());
            Log.e("ZHLPS", "port: " + uri.getPort());
            Log.e("ZHLPS", "path: " + uri.getPath());
            Log.e("ZHLPS", "queryString: " + uri.getQuery());
            Log.e("ZHLPS", "queryParameter: " + uri.getQueryParameter("biz_content"));

            if (null != uri.getQueryParameter("biz_content")) {
                try {
                    JSONObject object = new JSONObject(uri.getQueryParameter("biz_content"));
                    check(object.getString("biz_no"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        canBack = getIntent().getBooleanExtra("canBack",false);
        if (canBack == true){
            layoutBack.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.layout_back, R.id.btn_confirm, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.btn_confirm:
                if (edtName.getText().toString().equals("")) {
                    Toast.makeText(this, "请填写真实姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtIdentity.getText().toString().length() == 15 || edtIdentity.getText().toString().length() == 18) {

                } else {
                    Toast.makeText(this, "请填写正确的身份证号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                authenticate();
                break;
        }
    }


    /**
     * 实名认证
     */
    private void authenticate() {

        JSONObject object = new JSONObject();
        try {
            object.put("returnUrl", "zhlps://certi.back");
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("realName", edtName.getText().toString().trim());
            object.put("idKind", "1");
            object.put("idNo", edtIdentity.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_805191, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("isSuccess")) {
                        Toast.makeText(AuthenticateActivity.this, "您已通过实名认证", Toast.LENGTH_SHORT).show();
                        if (canBack == true){
                            finish();
                        }else {
                            getStore();
                        }
                    } else {
                        doVerify(jsonObject.getString("url"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(AuthenticateActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(AuthenticateActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 实名认证结果查询
     */
    private void check(String bizNo) {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("bizNo", bizNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_805192, object.toString(), new Xutil.XUtils3CallBackPost() {

            @Override
            public void onSuccess(String result) {
                System.out.println("result=" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("isSuccess")) {
                        SharedPreferences.Editor editor = userInfoSp.edit();
                        editor.putString("realName", edtName.getText().toString().trim());
                        editor.commit();

                        Toast.makeText(AuthenticateActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                        if (canBack == true){
                            finish();
                        }else {
                            getStore();
                        }
                    } else {
                        Toast.makeText(AuthenticateActivity.this, "认证失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(AuthenticateActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(AuthenticateActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getStore() {
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
                        startActivity(new Intent(AuthenticateActivity.this,Main2Activity.class));
                    } else {
                        startActivity(new Intent(AuthenticateActivity.this,StoreContract2Activity.class));
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(AuthenticateActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(AuthenticateActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 启动支付宝进行认证
     *
     * @param url 开放平台返回的URL
     */
    private void doVerify(String url) {
        if (hasApplication()) {
            Intent action = new Intent(Intent.ACTION_VIEW);
            StringBuilder builder = new StringBuilder();
            builder.append("alipays://platformapi/startapp?appId=20000067&url=");
            builder.append(URLEncoder.encode(url));
            action.setData(Uri.parse(builder.toString()));
            startActivity(action);
        } else {
            //处理没有安装支付宝的情况
            new AlertDialog.Builder(this)
                    .setMessage("是否下载并安装支付宝完成认证?")
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent action = new Intent(Intent.ACTION_VIEW);
                            action.setData(Uri.parse("https://m.alipay.com"));
                            startActivity(action);
                        }
                    }).setNegativeButton("算了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    /**
     * 判断是否安装了支付宝
     *
     * @return true 为已经安装
     */
    private boolean hasApplication() {
        PackageManager manager = getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse("alipays://"));
        List<ResolveInfo> list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(canBack == true){
                finish();
            }else {
                tip();
            }
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
