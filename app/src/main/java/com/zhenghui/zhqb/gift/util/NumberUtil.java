package com.zhenghui.zhqb.gift.util;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String doubleFormatMoney(double money){
        DecimalFormat df = new DecimalFormat("#######0.000");
        String showMoney = df.format((money/1000));
        return showMoney.substring(0,showMoney.length()-1);
    }

    public static String doubleFormatGps(double d){
        DecimalFormat df = new DecimalFormat("#######0.000");
        String showMoney = df.format(d);
        return showMoney.substring(0,showMoney.length()-1);
    }

}
