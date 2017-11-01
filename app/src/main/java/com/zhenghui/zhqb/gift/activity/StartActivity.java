package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_807717;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808219;

public class StartActivity extends MyBaseActivity {

    @BindView(R.id.txt_pass)
    TextView txtPass;

    int time = 3500;
    private Timer timer;
    private TimerTask task;

    private boolean isNeedIdentity = false;

    private SharedPreferences userInfoSp;
    private SharedPreferences appConfigSp;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                startApp();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        startTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    @OnClick(R.id.txt_pass)
    public void onClick() {

    }

    private void startTime() {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, time);
    }

    private void stopTime() {
        timer.cancel();
    }

    private void startApp() {
//        if(userInfoSp.getString("userId",null) != null){
////            getData();
//            startActivity(new Intent(StartActivity.this,Main2Activity.class));
//        }else {
//            startActivity(new Intent(StartActivity.this,LoginActivity.class));
//        }
//        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//
//        stopTime();
//        finish();

        getDatas();

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
                        startActivity(new Intent(StartActivity.this,Main2Activity.class));
                    } else {
                        startActivity(new Intent(StartActivity.this,StoreContract2Activity.class));
                    }

                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    stopTime();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StartActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StartActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(StartActivity.this, AuthenticateActivity.class));

                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                            stopTime();
                            finish();
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
                Toast.makeText(StartActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StartActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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

                    if(userInfoSp.getString("userId",null) != null){
                        getData();
                    }else {
                        startActivity(new Intent(StartActivity.this,LoginActivity.class));
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                        stopTime();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StartActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StartActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
