package com.zhenghui.zhqb.gift.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenghui.zhqb.gift.MyBaseActivity;
import com.zhenghui.zhqb.gift.R;
import com.zhenghui.zhqb.gift.fragment.DustbinFragment;
import com.zhenghui.zhqb.gift.fragment.SellFragment;
import com.zhenghui.zhqb.gift.fragment.SoldOutFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductManageActivity extends MyBaseActivity {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_sell)
    TextView txtSell;
    @BindView(R.id.line_sell)
    View lineSell;
    @BindView(R.id.txt_sold_out)
    TextView txtSoldOut;
    @BindView(R.id.line_sold_out)
    View lineSoldOut;
    @BindView(R.id.layout_product)
    FrameLayout layoutProduct;
    @BindView(R.id.txt_delete)
    TextView txtDelete;
    @BindView(R.id.line_delete)
    View lineDelete;

    private SellFragment sellFragment;
    private DustbinFragment dustbinFragment;
    private SoldOutFragment soldOutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manage);
        ButterKnife.bind(this);
        setSelect(0);
    }

    @OnClick({R.id.layout_back, R.id.txt_sell, R.id.txt_sold_out, R.id.txt_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;

            case R.id.txt_sell:
                setSelect(0);


                break;
            case R.id.txt_sold_out:
                setSelect(1);

                break;

            case R.id.txt_delete:
                setSelect(2);

                break;
        }
    }

    /**
     * 选择Fragment
     *
     * @param i
     */
    public void setSelect(int i) {
        initLine();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        // 把图片设置为亮的
        // 设置内容区域
        switch (i) {
            case 0:
                if (sellFragment == null) {
                    sellFragment = new SellFragment();
                    transaction.add(R.id.layout_product, sellFragment);
                } else {
                    transaction.show(sellFragment);
                }
                txtSell.setTextColor(getResources().getColor(R.color.fontColor_rose));
                lineSell.setBackgroundColor(getResources().getColor(R.color.rose));
                break;

            case 1:
                if (soldOutFragment == null) {
                    soldOutFragment = new SoldOutFragment();
                    transaction.add(R.id.layout_product, soldOutFragment);
                } else {
                    transaction.show(soldOutFragment);

                }
                txtSoldOut.setTextColor(getResources().getColor(R.color.fontColor_rose));
                lineSoldOut.setBackgroundColor(getResources().getColor(R.color.rose));
                break;

            case 2:
                if (dustbinFragment == null) {
                    dustbinFragment = new DustbinFragment();
                    transaction.add(R.id.layout_product, dustbinFragment);
                } else {
                    transaction.show(dustbinFragment);

                }
                txtDelete.setTextColor(getResources().getColor(R.color.fontColor_rose));
                lineDelete.setBackgroundColor(getResources().getColor(R.color.rose));
                break;

            default:
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (sellFragment != null) {
            transaction.hide(sellFragment);
        }
        if (soldOutFragment != null) {
            transaction.hide(soldOutFragment);
        }
        if (dustbinFragment != null) {
            transaction.hide(dustbinFragment);
        }
    }

    private void initLine() {
        txtSell.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineSell.setBackgroundColor(getResources().getColor(R.color.white));
        txtSoldOut.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineSoldOut.setBackgroundColor(getResources().getColor(R.color.white));
        txtDelete.setTextColor(getResources().getColor(R.color.fontColor_gray));
        lineDelete.setBackgroundColor(getResources().getColor(R.color.white));
    }

}
