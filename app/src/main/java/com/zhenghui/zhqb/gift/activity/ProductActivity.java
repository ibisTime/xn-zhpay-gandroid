package com.zhenghui.zhqb.gift.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.zhenghui.zhqb.gift.MyApplication;
import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.adapter.ParameterAdapter;
import com.zhenghui.zhqb.gift.adapter.RecyclerViewAdapter;
import com.zhenghui.zhqb.gift.model.ParameterModel;
import com.zhenghui.zhqb.gift.model.ProductModel;
import com.zhenghui.zhqb.gift.model.ProductTypeModel;
import com.zhenghui.zhqb.gift.model.UserModel;
import com.zhenghui.zhqb.gift.util.ImageUtil;
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

import static com.zhenghui.zhqb.gift.util.Constant.CODE_805056;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808007;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808010;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808012;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808013;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808014;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808016;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808017;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808026;
import static com.zhenghui.zhqb.gift.util.Constant.CODE_808037;
import static com.zhenghui.zhqb.gift.util.ImageUtil.RESULT_CAMARA_IMAGE;
import static com.zhenghui.zhqb.gift.util.ImageUtil.album;
import static com.zhenghui.zhqb.gift.util.ImageUtil.camara;

public class ProductActivity extends MyBaseActivity {


    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.list_product)
    ListView listProduct;
    @BindView(R.id.txt_btn)
    TextView txtBtn;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    EditText edtName;
    EditText edtDetail;
    EditText edtAdvertisement;
    ImageView imgAdd;
    ImageView imgPhoto;
    TextView txtBigType;
    TextView txtSmallType;
    FrameLayout layoutAdvPic;
    RecyclerView recyclerView;
    LinearLayout layoutBigType;
    LinearLayout layoutSmallType;

    ImageView imgAddParamter;
    Button btnSend;

    private View headerView;
    private View footerView;

    private String cover = "";

    private boolean isCover = true;

    // 所有
    private List<ProductTypeModel> typeList;
    private List<ProductTypeModel> bigTypeList;
    private List<ProductTypeModel> smallTypeList;

    private List<ProductTypeModel> smallList1;
    private List<ProductTypeModel> smallList2;
    private List<ProductTypeModel> smallList3;


    private String[] bigType = {"剁手合集"};
    private String[] smallType1;
    private String[] smallType2;
    private String[] smallType3;

    private String category;
    private String type;

    private List<String> listPic;
    private List<String> listPicUrl;
    private RecyclerViewAdapter recyclerViewAdapter;

    private String code;
    private boolean isModifi;

    private ProductModel model;

    private ParameterAdapter adapter;
    private List<ParameterModel> listParamter;
    private JSONArray productSpecsList;

    private int PARAMETER_ADD = 233;
    private int PARAMETER_DETAIL = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add2);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);

        inits();
        initHeadView();
        initFootView();
        initListView();
        initRecyclerView();

        getProductType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    private void inits() {
        code = getIntent().getStringExtra("code");
        isModifi = getIntent().getBooleanExtra("isModifi", false);

        if(isModifi){
            txtTitle.setText("修改商品");
        }

        typeList = new ArrayList<>();
        bigTypeList = new ArrayList<>();
        smallTypeList = new ArrayList<>();

        smallList1 = new ArrayList<>();
        smallList2 = new ArrayList<>();
        smallList3 = new ArrayList<>();

        listPic = new ArrayList<>();
        listPicUrl = new ArrayList<>();

        listParamter = new ArrayList<>();
        adapter = new ParameterAdapter(ProductActivity.this, listParamter);
    }

    private void initHeadView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.head_product, null);

        txtBigType = (TextView) headerView.findViewById(R.id.txt_bigType);
        txtSmallType = (TextView) headerView.findViewById(R.id.txt_smallType);

        layoutBigType = (LinearLayout) headerView.findViewById(R.id.layout_bigType);
        layoutSmallType = (LinearLayout) headerView.findViewById(R.id.layout_smallType);

        imgAdd = (ImageView) headerView.findViewById(R.id.img_add);
        imgPhoto = (ImageView) headerView.findViewById(R.id.img_photo);

        layoutAdvPic = (FrameLayout) headerView.findViewById(R.id.layout_advPic);
        recyclerView = (RecyclerView) headerView.findViewById(R.id.recycler_view);

        edtName = (EditText) headerView.findViewById(R.id.edt_name);
        edtDetail = (EditText) headerView.findViewById(R.id.edt_detail);
        edtAdvertisement = (EditText) headerView.findViewById(R.id.edt_advertisement);

        layoutBigType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseBigType();
            }
        });

        layoutSmallType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtBigType.getText().equals("")) {
                    Toast.makeText(ProductActivity.this, "请先选择大类", Toast.LENGTH_SHORT).show();
                    return;
                }
                chooseSmallType();
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listPicUrl.size() < 3){
                    isCover = false;
                    choosePhoto(view);
                }else {
                    Toast.makeText(ProductActivity.this, "图片最多只能选择三张", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCover = true;
                choosePhoto(view);
            }
        });
    }

    private void initFootView() {
        footerView = LayoutInflater.from(this).inflate(R.layout.foot_product, null);
        btnSend = (Button) footerView.findViewById(R.id.btn_send);
        imgAddParamter = (ImageView) footerView.findViewById(R.id.img_add);

        imgAddParamter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isModifi) {
                    if(!model.getStatus().equals("9")){
                        startActivityForResult(new Intent(ProductActivity.this, ParameterActivity.class)
                                .putExtra("orderNo", listParamter.size() + 1)
                                .putExtra("code", model.getCode())
                                .putExtra("isModify", isModifi), PARAMETER_ADD);
                    }else {
                        Toast.makeText(ProductActivity.this, "垃圾箱里的商品不可添加或修改规格", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivityForResult(new Intent(ProductActivity.this, ParameterActivity.class), PARAMETER_ADD);
                }

            }
        });
    }

    private void initRecyclerView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        recyclerViewAdapter = new RecyclerViewAdapter(ProductActivity.this, listPic, listPicUrl);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initListView() {
        listProduct.addHeaderView(headerView);
        listProduct.addFooterView(footerView);
        listProduct.setAdapter(adapter);
        listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0){
                    return;
                }

                if (i == listParamter.size()+1){
                    return;
                }

                if (isModifi) {
                    if(!model.getStatus().equals("9")){
                        if (listParamter.size() > 0){
                            startActivityForResult(new Intent(ProductActivity.this, ParameterActivity.class)
                                    .putExtra("index", (i - 1))
                                    .putExtra("isModify", isModifi)
                                    .putExtra("model", listParamter.get(i - 1)), PARAMETER_DETAIL);
                        }

                    }else {
                        Toast.makeText(ProductActivity.this, "垃圾箱里的商品不可添加或修改规格", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (listParamter.size() > 0){
                        startActivityForResult(new Intent(ProductActivity.this, ParameterActivity.class)
                                .putExtra("index", (i - 1))
                                .putExtra("isModify", isModifi)
                                .putExtra("model", listParamter.get(i - 1)), PARAMETER_DETAIL);
                    }

                }

            }
        });
    }

    @OnClick({R.id.layout_back, R.id.txt_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_btn:
                switch (txtBtn.getText().toString()){
                    case "下架":
                        soldOut();
                        break;

                    case "操作":
                        showPopup(view);
                        break;

                    case "还原":
                        restore();
                        break;

                    case "发布":
                        if (checkData()) {
                            if ("1".equals(userInfoSp.getString("identityFlag",null))) {
                                commit();
                                if (isModifi) {
                                    modifi();
                                } else {
                                }
                            }else {
                                startActivity(new Intent(ProductActivity.this, AuthenticateActivity.class)
                                        .putExtra("canBack",true));
                            }

                        }
                        break;
                }
                break;
        }
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
                    if (isCover) {
                        Glide.with(ProductActivity.this).load(album(ProductActivity.this, data)).into(imgPhoto);
                    } else {
                        listPic.add(album(ProductActivity.this, data));
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    new QiNiuUtil(ProductActivity.this, album(ProductActivity.this, data), null).qiNiu(new QiNiuUtil.QiNiuCallBack() {
                        @Override
                        public void onSuccess(String key, ResponseInfo info, JSONObject res) {
                            System.out.println("key=" + key);

                            if (isCover) {
                                cover = key;
                            } else {
                                listPicUrl.add(key);
                            }

                        }
                    }, true);
                }

            } else if (requestCode == ImageUtil.RESULT_CAMARA_IMAGE) {
                if (data.getExtras() != null) {
                    if (isCover) {
                        Glide.with(ProductActivity.this).load(camara(ProductActivity.this, data)).into(imgPhoto);
                    } else {
                        listPic.add(camara(ProductActivity.this, data));
                        recyclerViewAdapter.notifyDataSetChanged();

                    }

                    new QiNiuUtil(ProductActivity.this, camara(ProductActivity.this, data), null).qiNiu(new QiNiuUtil.QiNiuCallBack() {
                        @Override
                        public void onSuccess(String key, ResponseInfo info, JSONObject res) {
                            if (isCover) {
                                cover = key;
                            } else {
                                listPicUrl.add(key);
                            }
                        }
                    }, true);
                }
            } else if (requestCode == PARAMETER_ADD) {
                ParameterModel model = (ParameterModel) data.getSerializableExtra("model");
                if (model != null) {
                    listParamter.add(model);
                    adapter.notifyDataSetChanged();
                }

            } else if (requestCode == PARAMETER_DETAIL) {
                int index = data.getIntExtra("index", 0);
                if(resultCode == 101){
                    ParameterModel model = (ParameterModel) data.getSerializableExtra("model");
                    if (model != null) {
                        listParamter.set(index,model);
                    }
                }else {
                    listParamter.remove(index);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取产品类别
     */
    private void getProductType() {
        JSONObject object = new JSONObject();
        try {
            object.put("parentCode", "");
            object.put("name", "");
            object.put("type", "1");
            object.put("orderColumn", "order_no");
            object.put("orderDir", "asc");
            object.put("status", "1");
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808007, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {


                try {
                    JSONArray jsonObject = new JSONArray(result);

                    Gson gson = new Gson();
                    ArrayList<ProductTypeModel> lists = gson.fromJson(jsonObject.toString(), new TypeToken<ArrayList<ProductTypeModel>>() {
                    }.getType());

                    typeList.addAll(lists);

                    creatType();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void creatType() {
        for (ProductTypeModel model : typeList) {
            if (model.getParentCode().equals("0")) {
                if (!model.getName().equals("一元夺宝")) {
                    bigTypeList.add(model);
                }
            } else {
                smallTypeList.add(model);
            }
        }

        setType();
    }

    private void setType() {
        for (ProductTypeModel big : bigTypeList) {
            if (big.getCode().equals("FL201700000000000001")) {
//                bigType[0] = big.getName();
                for (ProductTypeModel small : smallTypeList) {
                    if (small.getParentCode().equals(big.getCode())) {
                        if (small.getCode().equals("FL201700000000000101"))
                            smallList1.add(small);
                    }
                }
            }
        }
//            else if (big.getOrderNo() == 2) {
////                bigType[1] = big.getName();
//                for (ProductTypeModel small : smallTypeList) {
//                    if (small.getParentCode().equals(big.getCode())) {
//                        smallList2.add(small);
//                    }
//                }
//            }
//            else if (big.getCode().equals("FL201700000000000002")) {
//                bigType[2] = big.getName();
//                for (ProductTypeModel small : smallTypeList) {
//                    if (small.getParentCode().equals(big.getCode())) {
//                        smallList3.add(small);
//                    }
//                }
//            }
//        }


        smallType1 = new String[smallList1.size()];
        smallType3 = new String[smallList3.size()];

        for (int i = 0; i < smallList1.size(); i++) {
            smallType1[i] = smallList1.get(i).getName();
        }
//        if (smallList2.size() != 0) {
//            smallType2 = new String[smallList2.size()];
//            for (int i = 0; i < smallList2.size(); i++) {
//                smallType2[i] = smallList2.get(i).getName();
//            }
//        }
        for (int i = 0; i < smallList3.size(); i++) {
            smallType3[i] = smallList3.get(i).getName();
        }

        if (isModifi) {
            getDatas();
        }

    }


    private void chooseBigType() {
        new AlertDialog.Builder(this).setTitle("请选择大类").setSingleChoiceItems(
                bigType, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        txtBankCard.setText(list.get(which).getBankName());
                        txtBigType.setText(bigType[which]);
                        txtSmallType.setText("");
                        category = bigTypeList.get(which).getCode() + "";
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void chooseSmallType() {

        final String[] str;
        if (txtBigType.getText().toString().trim().equals("剁手合集")) {
            str = smallType1;
        } else if (txtBigType.getText().toString().trim().equals("一元夺宝")) {
            str = smallType2;
        } else {
            str = smallType3;
        }

        new AlertDialog.Builder(this).setTitle("请选择小类").setSingleChoiceItems(
                str, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        txtBankCard.setText(list.get(which).getBankName());
                        txtSmallType.setText(str[which]);

                        if (txtBigType.getText().toString().trim().equals("剁手合集")) {
                            type = smallList1.get(which).getCode();
                        } else if (txtBigType.getText().toString().trim().equals("一元夺宝")) {
                            type = smallList2.get(which).getCode();
                        } else if (txtBigType.getText().toString().trim().equals("0元试购")) {
                            type = smallList3.get(which).getCode();
                        }

                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }


    private void modifi() {
        productSpecsList = new JSONArray();
        for(int i=0; i<listParamter.size(); i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", listParamter.get(i).getName());
                jsonObject.put("price1", listParamter.get(i).getPrice1());
                jsonObject.put("price2", listParamter.get(i).getPrice2());
                jsonObject.put("price3", listParamter.get(i).getPrice3());
                jsonObject.put("weight", listParamter.get(i).getWeight());
                jsonObject.put("province", listParamter.get(i).getProvince());
                jsonObject.put("quantity", listParamter.get(i).getQuantity());
                jsonObject.put("orderNo", i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            productSpecsList.put(jsonObject);
        }

        String pic = "";

        for (String s : listPicUrl) {
            pic = pic + s + "||";
        }
        System.out.println("pic=" + pic);

        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("type", type);
            object.put("name", edtName.getText().toString().trim());
            object.put("slogan", edtAdvertisement.getText().toString().trim());
            object.put("advPic", cover);
            object.put("pic", pic.substring(0, pic.length() - 2));
            object.put("description", edtDetail.getText().toString().trim());
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", userInfoSp.getString("userId", null));
            object.put("productSpecsList", productSpecsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808012, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "修改商品成功", Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void commit() {
        productSpecsList = new JSONArray();
        for(int i=0; i<listParamter.size(); i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", listParamter.get(i).getName());
                jsonObject.put("price1", listParamter.get(i).getPrice1());
                jsonObject.put("price2", listParamter.get(i).getPrice2());
                jsonObject.put("price3", listParamter.get(i).getPrice3());
                jsonObject.put("weight", listParamter.get(i).getWeight());
                jsonObject.put("province", listParamter.get(i).getProvince());
                jsonObject.put("quantity", listParamter.get(i).getQuantity());
                jsonObject.put("orderNo", i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            productSpecsList.put(jsonObject);
        }

        String pic = "";
        for (String s : listPicUrl) {
            pic = pic + s + "||";
        }

        JSONObject object = new JSONObject();
        try {
            object.put("type", type);
            object.put("name", edtName.getText().toString().trim());
            object.put("slogan", edtAdvertisement.getText().toString().trim());
            object.put("advPic", cover);
            object.put("pic", pic.substring(0, pic.length() - 2));
            object.put("description", edtDetail.getText().toString().trim());
            object.put("updater", userInfoSp.getString("userId", null));
            object.put("token", userInfoSp.getString("token", null));
            object.put("systemCode", appConfigSp.getString("systemCode", null));
            object.put("companyCode", userInfoSp.getString("userId", null));
            object.put("productSpecsList", productSpecsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_808010, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "添加商品成功", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 获取商品详情
     */
    public void getDatas() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808026, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Gson gson = new Gson();
                    model = gson.fromJson(jsonObject.toString(), new TypeToken<ProductModel>() {
                    }.getType());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setView();
                setData();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 获取商品规格
     */
    public void getParameter() {
        JSONObject object = new JSONObject();
        try {
            object.put("productCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808037, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    Gson gson = new Gson();
                    List<ParameterModel> lists = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<ParameterModel>>() {
                    }.getType());

                    if (lists != null) {
                        listParamter.clear();
                        for (ParameterModel bean : lists) {

                            ParameterModel parameterModel = new ParameterModel();
                            parameterModel.setName(bean.getName());
                            parameterModel.setCode(bean.getCode());
                            parameterModel.setPrice1(bean.getPrice1());
                            parameterModel.setPrice2(bean.getPrice2());
                            parameterModel.setPrice3(bean.getPrice3());
                            parameterModel.setWeight(bean.getWeight());
                            parameterModel.setOrderNo(bean.getOrderNo());
                            parameterModel.setQuantity(bean.getQuantity());
                            parameterModel.setProvince(bean.getProvince());
                            listParamter.add(parameterModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setView() {
        if (isModifi) {
            if(model.getStatus().equals("3")){ // 已上架
                txtBtn.setText("下架");
            } else if(model.getStatus().equals("4")){ // 已下架
                txtBtn.setText("操作");
            }else if(model.getStatus().equals("9")) { // 已删除
                txtBtn.setText("还原");
            }
        } else {
            txtBtn.setText("发布");
        }

        ImageUtil.glide(model.getAdvPic(), imgPhoto, this);

        for (ProductTypeModel big : bigTypeList) {
            if (big.getCode().equals(model.getCategory())) {
                txtBigType.setText(big.getName());
            }
        }

        for (ProductTypeModel small : smallTypeList) {
            if (small.getCode().equals(model.getType())) {
                txtSmallType.setText(small.getName());
            }
        }

        edtName.setText(model.getName());
        edtAdvertisement.setText(model.getSlogan());
        edtDetail.setText(model.getDescription());

        String[] str = model.getPic().split("\\|\\|");
        listPic.clear();
        listPicUrl.clear();
        for (int i = 0; i < str.length; i++) {
            listPic.add(str[i]);
            listPicUrl.add(str[i]);
        }
        recyclerViewAdapter.notifyDataSetChanged();

        if (model.getProductSpecsList() != null) {
            listParamter.clear();
            for (ProductModel.ProductSpecsListBean bean : model.getProductSpecsList()) {

                ParameterModel parameterModel = new ParameterModel();
                parameterModel.setName(bean.getName());
                parameterModel.setCode(bean.getCode());
                parameterModel.setPrice1(bean.getPrice1());
                parameterModel.setPrice2(bean.getPrice2());
                parameterModel.setPrice3(bean.getPrice3());
                parameterModel.setWeight(bean.getWeight());
                parameterModel.setOrderNo(bean.getOrderNo());
                parameterModel.setQuantity(bean.getQuantity());
                parameterModel.setProvince(bean.getProvince());
                listParamter.add(parameterModel);
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void setData() {
        category = model.getCategory();
        type = model.getType();
        cover = model.getAdvPic();
    }

    private boolean checkData() {

        if (cover.equals("")) {
            Toast.makeText(this, "请添加商品封面", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写商品名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtAdvertisement.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写商品广告语", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtBigType.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请选择商品大类", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtSmallType.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请选择商品小类", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDetail.getText().toString().trim().length() < 20) {
            Toast.makeText(this, "商品详情不能少于20个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listPic.size() == 0) {
            Toast.makeText(this, "请添加商品图片", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listParamter.size() == 0) {
            Toast.makeText(this, "请添加商品规格", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 上架
     */
    public void putAway() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("location", "1");
            object.put("orderNo", "1");
            object.put("updater", userInfoSp.getString("userId",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808013, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "上架成功", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 下架
     */
    public void soldOut() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("updater", userInfoSp.getString("userId",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808014, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "下架成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
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

                    SharedPreferences.Editor editor = userInfoSp.edit();
                    editor.putString("identityFlag", model.getIdentityFlag());

                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopup(View view) {

        // 一个自定义的布局，作为显示的内容
        View mview = LayoutInflater.from(this).inflate(R.layout.popup_product, null);

        TextView txtUp = (TextView) mview.findViewById(R.id.txt_up);
        TextView txtModify = (TextView) mview.findViewById(R.id.txt_modify);
        TextView txtDelete = (TextView) mview.findViewById(R.id.txt_delete);

        LinearLayout layoutCancel = (LinearLayout) mview.findViewById(R.id.layout_cancel);

        final PopupWindow popupWindow = new PopupWindow(mview,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
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

        txtUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(userInfoSp.getString("identityFlag",null))) {
                    putAway();
                }else {
                    startActivity(new Intent(ProductActivity.this, AuthenticateActivity.class)
                            .putExtra("canBack",true));
                }
                popupWindow.dismiss();

            }
        });

        txtModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    if ("1".equals(userInfoSp.getString("identityFlag",null))) {
                        modifi();
                    }else {
                        startActivity(new Intent(ProductActivity.this, AuthenticateActivity.class)
                                .putExtra("canBack",true));
                    }

                }
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                popupWindow.dismiss();

            }
        });


        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_layout));
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);

    }

    /**
     * 删除
     */
    public void delete() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("updater", userInfoSp.getString("userId",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808016, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 还原
     */
    public void restore() {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("updater", userInfoSp.getString("userId",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Xutil().post(CODE_808017, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(ProductActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(ProductActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(ProductActivity.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
