package com.zhenghui.zhqb.gift.util;

public class BillUtil {

    public static String getBillType(String bizType){

        if(bizType.equals("11")){
            return "充值";
        } else if(bizType.equals("-11")){
            return "取现";
        } else if(bizType.equals("19")){
            return "蓝补";
        } else if(bizType.equals("-19")){
            return "红冲";
        } else if(bizType.equals("-30")){
            return "购物";
        } else if(bizType.equals("30")){
            return "购物退款";
        } else if(bizType.equals("32")){
            return "确认收货，商户收钱";
        } else if(bizType.equals("-40")){
            return "购买折扣券";
        } else if(bizType.equals("-50")){
            return "购买汇赚宝";
        } else if(bizType.equals("51")){
            return "购买汇赚宝分成";
        }else if(bizType.equals("52")){
            return "汇赚宝摇一摇奖励";
        } else if(bizType.equals("53")){
            return "汇赚宝主人摇一摇分成";
        } else if(bizType.equals("54")){
            return "推荐人摇一摇分成";
        } else if(bizType.equals("60")){
            return "发一发得红包";
        } else if(bizType.equals("61")){
            return "领取红包";
        }else if(bizType.equals("-70")){
            return "参与小目标";
        }else if(bizType.equals("71")){
            return "小目标中奖";
        }else if(bizType.equals("200")){
            return "币种兑换";
        }else if(bizType.equals("-ZH1")){
            return "O2O支付";
        }else if(bizType.equals("-ZH2")){
            return "分红权分红";
        }else if(bizType.equals("-ZH3")){
            return "币种售卖";
        }else if(bizType.equals("206")){
            return "C端用户间转账";
        }
        return "";
    }


    public static String getCurrency(String currency){

        switch (currency) {
            case "CNY": // 人名币
                return "人名币";

            case "FRB": // 分润
                return "分润";

            case "GXJL": // 贡献奖励
                return "贡献奖励";

            case "GWB": // 购物币
                return "购物币";

            case "QBB": // 钱包币
                return "钱包币";

            case "HBB": // 红包
                return "红包";

            case "HBYJ": // 红包业绩
                return "红包业绩";

        }

        return "";
    }
}
