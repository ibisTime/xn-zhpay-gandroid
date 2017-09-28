package com.zhenghui.zhqb.gift.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.qiniu.android.http.ResponseInfo;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.RecyclerViewAdapter;
import com.zhenghui.zhqb.gift.model.StoreModel;
import com.zhenghui.zhqb.gift.model.StoreTypeModel;
import com.zhenghui.zhqb.gift.util.ImageUtil;
import com.zhenghui.zhqb.gift.util.NumberUtil;
import com.zhenghui.zhqb.gift.util.QiNiuUtil;
import com.zhenghui.zhqb.gift.util.Xutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_808007;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808201;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808203;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808218;
import static com.zhenghui.zhqb.gift.util.ImageUtil.RESULT_CAMARA_IMAGE;
import static com.zhenghui.zhqb.gift.util.ImageUtil.album;
import static com.zhenghui.zhqb.gift.util.ImageUtil.camara;

public class StoreActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.txt_province)
    TextView txtProvince;
    @BindView(R.id.txt_city)
    TextView txtCity;
    @BindView(R.id.txt_district)
    TextView txtDistrict;
    @BindView(R.id.layout_address)
    LinearLayout layoutAddress;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.edt_legal_person)
    EditText edtLegalPerson;
    @BindView(R.id.edt_bookMobile)
    EditText edtBookMobile;
    @BindView(R.id.edt_smsMobile)
    EditText edtSmsMobile;
    @BindView(R.id.edt_referrer)
    EditText edtReferrer;
    @BindView(R.id.edt_slogan)
    EditText edtSlogan;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.layout_advPic)
    FrameLayout layoutAdvPic;
    @BindView(R.id.txt_gps)
    TextView txtGps;
    @BindView(R.id.layout_gps)
    LinearLayout layoutGps;
    @BindView(R.id.img_license)
    ImageView imgLicense;
    @BindView(R.id.layout_license)
    FrameLayout layoutLicense;
    @BindView(R.id.txt_type)
    TextView txtType;
    @BindView(R.id.layout_type)
    LinearLayout layoutType;
    @BindView(R.id.txt_use)
    TextView txtUse;
    @BindView(R.id.txt_nonuse)
    TextView txtNonuse;
    @BindView(R.id.edt_detail)
    EditText edtDetail;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.layout_confirm)
    LinearLayout layoutConfirm;

    private String picFlag = "";
    private boolean isModifi = false;

    private int type = -1;
    private String[] storeType;
    private List<StoreTypeModel> list;

    private String province;
    private String city;
    private String district;

    private String licenseUrl = "";
    private String coverUrl = "";
    private String latitude = "";
    private String longitude = "";
    private String address = "";

    private List<String> listPic;
    private List<String> listPicUrl;

    private RecyclerViewAdapter recyclerViewAdapter;

    private StoreModel model;

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);

        inits();
        initRecyclerView();

        getStoreType();
    }

    private void inits() {
        listPic = new ArrayList<>();
        listPicUrl = new ArrayList<>();

        isModifi = getIntent().getBooleanExtra("isModifi", false);

    }

    private void initRecyclerView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StoreActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        recyclerViewAdapter = new RecyclerViewAdapter(StoreActivity.this, listPic, listPicUrl);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @OnClick({R.id.layout_back, R.id.layout_type, R.id.layout_address, R.id.layout_advPic,
            R.id.img_license, R.id.layout_gps, R.id.img_add, R.id.layout_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.layout_type:
                chooseStoreType();
                break;

            case R.id.layout_address:
                cityPicker();
                break;

            case R.id.layout_advPic:
                picFlag = "advPic";
                choosePhoto(view);
                break;

            case R.id.img_license:
                picFlag = "license";
                choosePhoto(view);
                break;

            case R.id.layout_gps:
                startActivityForResult(new Intent(StoreActivity.this, MapActivity.class), 0);
                break;

            case R.id.img_add:
                if (listPicUrl.size() < 3) {
                    picFlag = "pic";
                    choosePhoto(view);
                } else {
                    Toast.makeText(this, "图片最多只能选择三张", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.layout_confirm:
                if (flag) {
                    if (checkData()) {
                        commit();
                    }
                }

                break;

        }
    }

    private void getStoreType() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "3");
            object.put("name", "");
            object.put("status", "1");
            object.put("parentCode", "");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808007, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    Gson gson = new Gson();
                    list = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<StoreTypeModel>>() {
                    }.getType());

                    storeType = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        storeType[i] = list.get(i).getName();
                    }

                    if (storeType.length != 0) {
                        txtType.setText(list.get(0).getName());
                        type = 0;
                    }

                    if (isModifi) {
                        edtReferrer.setKeyListener(null);
                        getDatas();
                    } else {
//                        txtBtn.setText("保存");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void chooseStoreType() {
        new AlertDialog.Builder(this).setTitle("请选择店铺类型").setSingleChoiceItems(
                storeType, type, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        txtType.setText(storeType[which]);
                        type = which;
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void cityPicker() {
        final CityPicker cityPicker = new CityPicker.Builder(StoreActivity.this)
                .textSize(18)
                .titleBackgroundColor("#ffffff")
                .titleTextColor("#ffffff")
                .backgroundPop(0xa0000000)
                .confirTextColor("#FE4332")
                .cancelTextColor("#FE4332")
                .province("北京市")
                .city("北京市")
                .district("昌平区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();

        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                province = citySelected[0];
                //城市
                city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                district = citySelected[2];
                //邮编
                String code = citySelected[3];

                txtCity.setText(city);
                txtProvince.setText(province);
                txtDistrict.setText(district);
            }

            @Override
            public void onCancel() {
                cityPicker.hide();
            }
        });
    }

    /**
     * 选择照片
     *
     * @param view
     */
    private PopupWindow popupWindow;

    private void choosePhoto(View view) {

        // 一个自定义的布局，作为显示的内容
        View mview = LayoutInflater.from(this).inflate(
                R.layout.popup_release, null);

        TextView txtPhotograph = (TextView) mview
                .findViewById(R.id.txt_photograph);
        TextView txtAlbum = (TextView) mview
                .findViewById(R.id.txt_album);
        TextView txtCancel = (TextView) mview
                .findViewById(R.id.txt_releasePopup_cancel);
        TextView txtTitle = (TextView) mview
                .findViewById(R.id.txt_title);

        txtTitle.setText("选择店铺封面");

        LinearLayout dismiss = (LinearLayout) mview.findViewById(R.id.quxiao);

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

        txtTitle.setText("选择封面");

        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        txtAlbum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 调用android的图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, ImageUtil.RESULT_LOAD_IMAGE);
                popupWindow.dismiss();
            }
        });

        txtPhotograph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAMARA_IMAGE);

                popupWindow.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.corners_layout));
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, Gravity.BOTTOM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data为B中回传的Intent
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == ImageUtil.RESULT_LOAD_IMAGE) {
                if (data.getData() != null) {
                    switch (picFlag){
                        case "advPic":
                            Glide.with(StoreActivity.this).load(album(StoreActivity.this, data)).into(imgCover);
                            break;

                        case "license":
                            Glide.with(StoreActivity.this).load(album(StoreActivity.this, data)).into(imgLicense);
                            break;

                        case "pic":
                            listPic.add(album(StoreActivity.this, data));
                            recyclerViewAdapter.notifyDataSetChanged();
                            break;
                    }

                    new QiNiuUtil(StoreActivity.this, album(StoreActivity.this, data), null).qiNiu(new QiNiuUtil.QiNiuCallBack() {
                        @Override
                        public void onSuccess(String key, ResponseInfo info, JSONObject res) {
                            System.out.println("key=" + key);

                            switch (picFlag){
                                case "advPic":
                                    coverUrl = key;
                                    break;

                                case "license":
                                    licenseUrl = key;
                                    break;

                                case "pic":
                                    listPicUrl.add(key);
                                    break;
                            }

                        }
                    }, true);
                }

            } else if (requestCode == ImageUtil.RESULT_CAMARA_IMAGE) {
                if (data.getExtras() != null) {

                    switch (picFlag){
                        case "advPic":
                            Glide.with(StoreActivity.this).load(camara(StoreActivity.this, data)).into(imgCover);
                            break;

                        case "license":
                            Glide.with(StoreActivity.this).load(camara(StoreActivity.this, data)).into(imgLicense);
                            break;

                        case "pic":
                            listPic.add(camara(StoreActivity.this, data));
                            recyclerViewAdapter.notifyDataSetChanged();
                            break;
                    }

                    new QiNiuUtil(StoreActivity.this, camara(StoreActivity.this, data), null).qiNiu(new QiNiuUtil.QiNiuCallBack() {
                        @Override
                        public void onSuccess(String key, ResponseInfo info, JSONObject res) {

                            switch (picFlag){
                                case "advPic":
                                    coverUrl = key;
                                    break;

                                case "license":
                                    licenseUrl = key;
                                    break;

                                case "pic":
                                    listPicUrl.add(key);
                                    break;
                            }
                        }
                    }, true);
                }
            } else if (requestCode == 0 && resultCode == 0) {
                longitude = data.getStringExtra("longitude");
                latitude = data.getStringExtra("latitude");
                txtGps.setText("Lng:" + NumberUtil.doubleFormatGps(Double.parseDouble(longitude))
                        + "  Lat:" + NumberUtil.doubleFormatGps(Double.parseDouble(latitude)));
                address = data.getStringExtra("addressName");
            }
        }
    }

    private boolean checkData() {

        if (type == -1) {
            Toast.makeText(this, "请选择店铺类型", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写店铺名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtProvince.getText().toString().trim().equals("省") && txtCity.getText().toString().trim().equals("市") && txtDistrict.getText().toString().trim().equals("区")) {
            Toast.makeText(this, "请选择所在省市区", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtLegalPerson.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写法人姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtBookMobile.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写店固定电话", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtSmsMobile.getText().toString().trim().length() != 11) {
            Toast.makeText(this, "请填写正确的短信手机号码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtReferrer.getText().toString().trim().length() != 11) {
            Toast.makeText(this, "请填写正确的推荐人账号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtSlogan.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写店铺广告", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (coverUrl.equals("")) {
            Toast.makeText(this, "请添加店铺封面", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude.equals("") && longitude.equals("")) {
            Toast.makeText(this, "请定位店铺经纬度", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDetail.getText().toString().trim().length() < 20) {
            Toast.makeText(this, "店铺详情不能少于20个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listPic.size() == 0) {
            Toast.makeText(this, "请添加店铺图片", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void commit() {
        flag = false;
        String pic = "";

        for (String s : listPicUrl) {
            pic = pic + s + "||";
        }
        System.out.println("pic=" + pic);

        JSONObject object = new JSONObject();
        try {
            if (isModifi) {
                object.put("code", userInfoSp.getString("storeCode", null));
                object.put("updater", userInfoSp.getString("userId", null));
            } else {
                object.put("owner", userInfoSp.getString("userId", null));
            }
            object.put("name", edtName.getText().toString().trim());
            object.put("type", list.get(type).getCode());
            object.put("legalPersonName", edtLegalPerson.getText().toString().trim());
            object.put("userReferee", edtReferrer.getText().toString().trim());
            object.put("slogan", edtSlogan.getText().toString().trim());
            object.put("advPic", coverUrl);
            object.put("license", licenseUrl);
            object.put("pic", pic.substring(0, pic.length() - 2));
            object.put("description", edtDetail.getText().toString().trim());
            object.put("province", province);
            object.put("city", city);
            object.put("area", district);
            object.put("address", edtAddress.getText().toString().trim());
            object.put("longitude", longitude);
            object.put("latitude", latitude);
            object.put("bookMobile", edtBookMobile.getText().toString().trim());
            object.put("smsMobile", edtSmsMobile.getText().toString().trim());
            object.put("pdf", "");
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String httCode = "";
        if (isModifi) {
            httCode = CODE_808203;

        } else {
            httCode = CODE_808201;
        }

        new Xutil().post(httCode, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (isModifi) {
                        Toast.makeText(StoreActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StoreActivity.this, "签约成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(StoreActivity.this, Main2Activity.class));
                        StoreContract2Activity.instance.finish();
                    }
                    SharedPreferences.Editor editor = userInfoSp.edit();
                    editor.putBoolean("storeFlag", true);
                    editor.commit();

                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreActivity.this, tip, Toast.LENGTH_SHORT).show();
                flag = true;
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        });

    }

    /**
     * 获取商家详情
     */
    private void getDatas() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfoSp.getString("userId", null));
            object.put("code", userInfoSp.getString("storeCode", null));
            object.put("token", userInfoSp.getString("token", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808218, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    model = gson.fromJson(jsonObject.toString(), new TypeToken<StoreModel>() {
                    }.getType());

                    setView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(StoreActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(StoreActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {
        ImageUtil.glide(model.getAdvPic(), imgCover, this);
        coverUrl = model.getAdvPic();

        if (model.getLicense() != null) {
            if (!model.getLicense().equals("")) {
                ImageUtil.glide(model.getLicense(), imgLicense, this);
                licenseUrl = model.getLicense();
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (model.getType().equals(list.get(i).getCode())) {
                txtType.setText(list.get(i).getName());
                type = i;
            }
        }

        edtName.setText(model.getName());
        txtProvince.setText(model.getProvince());
        txtCity.setText(model.getCity());
        txtDistrict.setText(model.getArea());
        edtAddress.setText(model.getAddress());

        province = model.getProvince();
        city = model.getCity();
        district = model.getArea();

        edtBookMobile.setText(model.getBookMobile());
        edtSmsMobile.setText(model.getSmsMobile());
        edtSlogan.setText(model.getSlogan());
        edtLegalPerson.setText(model.getLegalPersonName());
        edtReferrer.setText(model.getRefereeMobile());
        edtDetail.setText(model.getDescription());

        txtUse.setText((model.getRate1() * 100) + "");
        txtNonuse.setText(model.getRate2() * 100 + "");

        String[] pic = model.getPic().split("\\|\\|");
        for (int i = 0; i < pic.length; i++) {
            listPic.add(pic[i]);
            listPicUrl.add(pic[i]);
        }
        recyclerViewAdapter.notifyDataSetChanged();

        latitude = model.getLatitude();
        longitude = model.getLongitude();

        txtGps.setText("Lng:" + NumberUtil.doubleFormatGps(Double.parseDouble(longitude))
                + "  Lat:" + NumberUtil.doubleFormatGps(Double.parseDouble(latitude)));

//        LatLonPoint latLonPoint = new LatLonPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
//        getAddress(latLonPoint);

    }
}
