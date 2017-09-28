package com.zhenghui.zhqb.gift.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.MyBankCardAdapter;
import com.zhenghui.zhqb.gift.model.MyBankCardModel;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802011;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_802015;

public class BankCardActivity extends MyBaseActivity implements AdapterView.OnItemClickListener {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.list_bankCart)
    ListView listBankCart;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.layout_more)
    LinearLayout layoutMore;

    private int page = 1;
    private int pageSize = 10;

    private List<MyBankCardModel> list;
    private MyBankCardAdapter adapter;

    private SharedPreferences userInfoSp;
    private SharedPreferences appConfigSp;

    private boolean isWithdrawal;

    private View footView;
    private LinearLayout layoutAdd;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        // 初始返回数据
        setResult(0, new Intent().putExtra("bankcardNumber", "").putExtra("bankName", "").putExtra("bankName", ""));

        inits();
        initFootView();
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        list = new ArrayList<>();
        adapter = new MyBankCardAdapter(this, list);

        userInfoSp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        appConfigSp = getSharedPreferences("appConfig", Context.MODE_PRIVATE);

        isWithdrawal = getIntent().getBooleanExtra("isWithdrawal", false);
        if (isWithdrawal) {
            txtTitle.setText("选择银行卡");
        }
    }

    private void initFootView() {
        footView = LayoutInflater.from(this).inflate(R.layout.foot_bank_card, null);
        layoutAdd = (LinearLayout) footView.findViewById(R.id.layout_add);

        layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BankCardActivity.this, BindBankCardActivity.class));
            }
        });
    }

    private void initListView() {
        listBankCart.addFooterView(footView);
        listBankCart.setAdapter(adapter);
        listBankCart.setOnItemClickListener(this);
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
            object.put("status", "");
            object.put("start", page);
            object.put("limit", pageSize);
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

                    if (page == 1) {
                        list.clear();
                    }

                    list.addAll(lists);
                    if (list.size() > 0) {
                        layoutAdd.setVisibility(View.GONE);
                        layoutMore.setVisibility(View.VISIBLE);
                    }else {
                        layoutMore.setVisibility(View.GONE);
                        layoutAdd.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(BankCardActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(BankCardActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.layout_back, R.id.layout_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                setResult(0, new Intent().putExtra("bankcardNumber", "").putExtra("bankName", "").putExtra("bankName", ""));
                finish();
                break;

            case R.id.layout_more:
                choosePhoto(view);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isWithdrawal) {
            setResult(0, new Intent().putExtra("bankcardNumber", list.get(i).getBankcardNumber())
                    .putExtra("bankName", list.get(i).getBankName()));
            finish();
        } else {
            startActivity(new Intent(BankCardActivity.this, BindBankCardActivity.class)
                    .putExtra("code", list.get(i).getCode())
                    .putExtra("isModifi", true));
        }
    }

    /**
     * 选择照片
     *
     * @param view
     */
    private void choosePhoto(View view) {

        // 一个自定义的布局，作为显示的内容
        View mview = LayoutInflater.from(this).inflate(
                R.layout.popup_bank_card, null);

        TextView txtDelete = (TextView) mview.findViewById(R.id.txt_delete);

        LinearLayout layoutCancel = (LinearLayout) mview.findViewById(R.id.layout_cancel);

        popupWindow = new PopupWindow(mview, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);

        popupWindow.setTouchable(true);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });


        layoutCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                delete();
                popupWindow.dismiss();
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.corners_layout));
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, Gravity.BOTTOM);

    }

    private void delete() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", list.get(0).getCode());
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802011, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                getList();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(BankCardActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(BankCardActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
