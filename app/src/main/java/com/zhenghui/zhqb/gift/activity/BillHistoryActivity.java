package com.zhenghui.zhqb.gift.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.BillAdapter;
import com.zhenghui.zhqb.gift.model.BillModel;
import com.zhenghui.zhqb.gift.util.RefreshLayout;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_802531;

public class BillHistoryActivity extends MyBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_balance)
    TextView txtBalance;
    @BindView(R.id.txt_withdrawal)
    TextView txtWithdrawal;
    @BindView(R.id.list_bill)
    ListView listBill;
    @BindView(R.id.swipe_container)
    RefreshLayout swipeContainer;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;
    @BindView(R.id.txt_start)
    TextView txtStart;
    @BindView(R.id.txt_end)
    TextView txtEnd;

    private List<BillModel> list;
    private BillAdapter adapter;

    private String accountNumber;

    private int page = 1;
    private int pageSize = 10;

    private Date date;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history);
        ButterKnife.bind(this);

        inits();
        initDate();
        initListView();
        initRefreshLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DATE, -1);//日期回滚1天
        date = calendar.getTime();

        list = new ArrayList<>();
        adapter = new BillAdapter(this, list);

        accountNumber = getIntent().getStringExtra("code");

    }

    private void initDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.roll(Calendar.DATE, -7);
        txtStart.setText(sdf.format(startCalendar.getTime()));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.roll(Calendar.DATE, -1);
        txtEnd.setText(sdf.format(endCalendar.getTime()));

        getData();
    }

    private void initListView() {
        listBill.setAdapter(adapter);
        listBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(BillHistoryActivity.this, BillDetailActivity.class)
                        .putExtra("isHistory", true)
                        .putExtra("code", list.get(i).getCode()));
            }
        });
    }

    private void initRefreshLayout() {
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setOnLoadListener(this);
    }

    @OnClick({R.id.layout_back, R.id.txt_start, R.id.txt_end, R.id.txt_confirm})
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_start:
                DatePickerDialog dialog = new DatePickerDialog(BillHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        if ((month + 1) < 10) {
                            if(day<10){
                                txtStart.setText(year + "-0" + (month + 1) + "-0" + day);
                            }else {
                                txtStart.setText(year + "-0" + (month + 1) + "-" + day);
                            }
                        } else {
                            if(day<10){
                                txtStart.setText(year + "-" + (month + 1) + "-0" + day);
                            }else {
                                txtStart.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        Date date = null;
//                        try{
//                            date = sdf.parse(txtStart.getText().toString());//初始日期
//                            endStart.setTime(date);
//                            endStart.set(Calendar.DATE, endStart.get(Calendar.DATE) - 6);
//                            endDate = endStart.getTime();
//                            txtEnd.setText(new SimpleDateFormat("yyyy-MM-dd").format(endDate));
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMaxDate(date.getTime());
                dialog.show();

                break;

            case R.id.txt_end:
                DatePickerDialog endDialog = new DatePickerDialog(BillHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        if ((month + 1) < 10) {
                            if(day<10){
                                txtEnd.setText(year + "-0" + (month + 1) + "-0" + day);
                            }else {
                                txtEnd.setText(year + "-0" + (month + 1) + "-" + day);
                            }
                        } else {
                            if(day<10){
                                txtEnd.setText(year + "-" + (month + 1) + "-0" + day);
                            }else {
                                txtEnd.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                DatePicker endPicker = endDialog.getDatePicker();
                endPicker.setMaxDate(date.getTime());
                endDialog.show();

                break;

            case R.id.txt_confirm:
                if(check()){
                    getData();
                }
                break;
        }
    }

    private boolean check(){
        if(txtStart.getText().toString().trim().equals("")){
            Toast.makeText(this, "请选择起始时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtEnd.getText().toString().trim().equals("")){
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkDate()){
            System.out.println("checkDate()="+checkDate());
            Toast.makeText(this, "起始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private boolean checkDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date endDate;
        Date startDate;

        try {
            endDate = sdf.parse(txtEnd.getText().toString());
            startDate = sdf.parse(txtStart.getText().toString());
            if (startDate.after(endDate)){
                System.out.println("startDate.after(endDate)="+startDate.after(endDate));
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void getData() {

        JSONObject object = new JSONObject();
        try {
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("accountNumber", accountNumber);
            object.put("dateStart", txtStart.getText().toString());
            object.put("dateEnd", txtEnd.getText().toString());
            object.put("start", page);
            object.put("limit", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_802531, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    List<BillModel> lists = gson.fromJson(jsonObject.getJSONArray("list").toString(), new TypeToken<ArrayList<BillModel>>() {
                    }.getType());

                    if(page == 1){
                        list.clear();
                    }
                    list.addAll(lists);

                    if(list.size() == 0){
                        Toast.makeText(BillHistoryActivity.this, "暂无历史账单", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(BillHistoryActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(BillHistoryActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
                page = 1;
                getData();
                // 更新数据
                // 更新完后调用该方法结束刷新
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setLoading(false);
                page = page + 1;
                getData();
            }
        }, 1500);
    }
}
