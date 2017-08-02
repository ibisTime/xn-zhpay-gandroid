package com.zhenghui.zhqb.gift.model;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {

    private String code;
    private boolean isAdd = false;
    private String category;
    private String type;
    private String name;
    private String slogan;
    private String advPic;
    private String pic;
    private String description;
    private String status;
    private String updater;
    private String updateDatetime;
    private int boughtCount;
    private String companyCode;
    private String systemCode;

    private StoreBean store;

    private List<ProductSpecsListBean> productSpecsList;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getAdvPic() {
        return advPic;
    }

    public void setAdvPic(String advPic) {
        this.advPic = advPic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public int getBoughtCount() {
        return boughtCount;
    }

    public void setBoughtCount(int boughtCount) {
        this.boughtCount = boughtCount;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public StoreBean getStore() {
        return store;
    }

    public void setStore(StoreBean store) {
        this.store = store;
    }

    public List<ProductSpecsListBean> getProductSpecsList() {
        return productSpecsList;
    }

    public void setProductSpecsList(List<ProductSpecsListBean> productSpecsList) {
        this.productSpecsList = productSpecsList;
    }

    public static class StoreBean {
        private String code;
        private String name;
        private String level;
        private String type;
        private String slogan;
        private String advPic;
        private String pic;
        private String description;
        private String province;
        private String city;
        private String area;
        private String address;
        private String longitude;
        private String latitude;
        private String bookMobile;
        private String smsMobile;
        private String uiLocation;
        private String uiOrder;
        private String legalPersonName;
        private String userReferee;
        private String isDefault;
        private String status;
        private String updater;
        private String updateDatetime;
        private String createDatetime;
        private String onUser;
        private String onDatetime;
        private String offUser;
        private String offDatetime;
        private String owner;
        private String companyCode;
        private String systemCode;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public String getAdvPic() {
            return advPic;
        }

        public void setAdvPic(String advPic) {
            this.advPic = advPic;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getBookMobile() {
            return bookMobile;
        }

        public void setBookMobile(String bookMobile) {
            this.bookMobile = bookMobile;
        }

        public String getSmsMobile() {
            return smsMobile;
        }

        public void setSmsMobile(String smsMobile) {
            this.smsMobile = smsMobile;
        }

        public String getUiLocation() {
            return uiLocation;
        }

        public void setUiLocation(String uiLocation) {
            this.uiLocation = uiLocation;
        }

        public String getUiOrder() {
            return uiOrder;
        }

        public void setUiOrder(String uiOrder) {
            this.uiOrder = uiOrder;
        }

        public String getLegalPersonName() {
            return legalPersonName;
        }

        public void setLegalPersonName(String legalPersonName) {
            this.legalPersonName = legalPersonName;
        }

        public String getUserReferee() {
            return userReferee;
        }

        public void setUserReferee(String userReferee) {
            this.userReferee = userReferee;
        }

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUpdater() {
            return updater;
        }

        public void setUpdater(String updater) {
            this.updater = updater;
        }

        public String getUpdateDatetime() {
            return updateDatetime;
        }

        public void setUpdateDatetime(String updateDatetime) {
            this.updateDatetime = updateDatetime;
        }

        public String getCreateDatetime() {
            return createDatetime;
        }

        public void setCreateDatetime(String createDatetime) {
            this.createDatetime = createDatetime;
        }

        public String getOnUser() {
            return onUser;
        }

        public void setOnUser(String onUser) {
            this.onUser = onUser;
        }

        public String getOnDatetime() {
            return onDatetime;
        }

        public void setOnDatetime(String onDatetime) {
            this.onDatetime = onDatetime;
        }

        public String getOffUser() {
            return offUser;
        }

        public void setOffUser(String offUser) {
            this.offUser = offUser;
        }

        public String getOffDatetime() {
            return offDatetime;
        }

        public void setOffDatetime(String offDatetime) {
            this.offDatetime = offDatetime;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getSystemCode() {
            return systemCode;
        }

        public void setSystemCode(String systemCode) {
            this.systemCode = systemCode;
        }
    }

    public static class ProductSpecsListBean {
        private String code;
        private String name;
        private String productCode;
        private double price1;
        private double price2;
        private double price3;
        private int quantity;
        private String province;
        private double weight;
        private int orderNo;
        private String companyCode;
        private String systemCode;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public double getPrice1() {
            return price1;
        }

        public void setPrice1(double price1) {
            this.price1 = price1;
        }

        public double getPrice2() {
            return price2;
        }

        public void setPrice2(double price2) {
            this.price2 = price2;
        }

        public double getPrice3() {
            return price3;
        }

        public void setPrice3(double price3) {
            this.price3 = price3;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(int orderNo) {
            this.orderNo = orderNo;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getSystemCode() {
            return systemCode;
        }

        public void setSystemCode(String systemCode) {
            this.systemCode = systemCode;
        }
    }
}
