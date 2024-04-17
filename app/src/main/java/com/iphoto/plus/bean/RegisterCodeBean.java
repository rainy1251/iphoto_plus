package com.iphoto.plus.bean;

public class RegisterCodeBean {

    public RegisterCodeBean(String phone) {
        this.phone = phone;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
