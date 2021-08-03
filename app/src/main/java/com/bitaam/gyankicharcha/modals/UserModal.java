package com.bitaam.gyankicharcha.modals;

import java.io.Serializable;

public class UserModal implements Serializable {

    String name,phoneNo,interest1,interest2,email,country,state;

    public UserModal() {
    }

    public UserModal(String name, String phoneNo, String interest1, String interest2, String email, String country, String state) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.email = email;
        this.country = country;
        this.state = state;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getInterest1() {
        return interest1;
    }

    public void setInterest1(String interest1) {
        this.interest1 = interest1;
    }

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
