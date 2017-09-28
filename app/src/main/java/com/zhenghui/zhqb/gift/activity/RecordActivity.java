package com.zhenghui.zhqb.gift.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.model.RecordModel;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808249;

public class RecordActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_get)
    TextView txtGet;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_bill)
    TextView txtBill;

    private String code;
    private String time;
    private double amount;
    private String nickName;

    private SpeechSynthesizer mTts;

    private Timer timer;
    private TimerTask task;

    SharedPreferences.Editor editor;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            timer.cancel();
            getRecord();

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recode);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTime();
        if (null != mTts) {
            mTts.stopSpeaking();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTime();
    }

    private void init() {
        timer = new Timer();
        editor = userInfoSp.edit();

        code = userInfoSp.getString("recordCode", "");
    }

    @OnClick({R.id.layout_back, R.id.txt_bill})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_bill:
                startActivity(new Intent(RecordActivity.this, StoreRecordActivity.class));
                break;
        }
    }

    private void getRecord() {
        JSONObject object = new JSONObject();
        try {
            object.put("storeCode", userInfoSp.getString("storeCode", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808249, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.toString().equals("{}")) {
                        Gson gson = new Gson();
                        RecordModel model = gson.fromJson(jsonObject.toString(), new TypeToken<RecordModel>() {
                        }.getType());

                        if (model != null) {
                            amount = model.getStoreAmount();
                            time = model.getPayDatetime();
                            nickName = model.getUser().getMobile();

                            check(model.getCode());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(getApplicationContext(), "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void check(String recordCode) {
//        if (!code.equals(recordCode)) {
//            code = recordCode;
//            editor.putString("recordCode", code);
//            editor.commit();


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        txtTime.setText(format.format(date));

        txtName.setText("“" + nickName + "”");
        txtGet.setText(NumberUtil.doubleFormatMoney(amount) + "礼品券");

        speak();
//        } else {
//            startTime();
//        }
    }

    private void startTime() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                handler.sendMessage(message);
            }

        };

        timer.schedule(task, 2000);
    }

    private void stopTime() {
        timer.cancel();
    }

    private void speak() {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "aisxmeng");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "80");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        //3.开始合成
        String voice = "您收到尾号" + numToChinese(nickName.substring(nickName.length() - 4, nickName.length())) + "支付的" + NumberUtil.doubleFormatMoney(amount) + "礼品券";
        System.out.println("voice=" + voice);
        mTts.startSpeaking(voice, mSynListener);

        //合成监听器
    }

    private SynthesizerListener mSynListener = new SynthesizerListener() {


        @Override
        public void onSpeakBegin() {
            stopTime();
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            //会话结束回调接口，没有错误时，error为null
            if (speechError == null) {
//                startTime();
//                Toast.makeText(RealTimeActivity.this, "语音播放结束", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public String numToChinese(String phone) {
        String[] str = {"零", "幺", "二", "三", "四", "五", "六", "七", "八", "九"};//
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < phone.length(); i++) {
            String index = String.valueOf(phone.charAt(i));
            sb = sb.append(str[Integer.parseInt(index)]);
        }

        return sb.toString();
    }
}
