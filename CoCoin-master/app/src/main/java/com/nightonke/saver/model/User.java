package com.nightonke.saver.model;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {

    // android id
    private String androidId;

    // the user's logo
    private String logoObjectId = "";

    // the user's setting for account book name
    private String accountBookName;

    // the user's setting for whether show picture in tag view
    private Boolean showPicture;


    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getLogoObjectId() {
        return logoObjectId;
    }

    public void setLogoObjectId(String logoObjectId) {
        this.logoObjectId = logoObjectId;
    }




    public String getAccountBookName() {
        return accountBookName;
    }

    public void setAccountBookName(String accountBookName) {
        this.accountBookName = accountBookName;
    }


    public Boolean getShowPicture() {
        return showPicture;
    }

    public void setShowPicture(Boolean showPicture) {
        this.showPicture = showPicture;
    }


}
