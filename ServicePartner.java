package com.circlesquare.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by user on 5/29/2017.
 */
@Entity
@Table(name = "ServiceCenter")
public class ServicePartner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "servicePartnerId")
    private int servicePartnerId;


    @Column(name = "name")
    private String name;


    @Column(name = "eMail")
    private String eMail;


    @Column(name = "mobile")
    private String mobile;


    //@Column(name = "address")
    //private String address;


    //@Column(name = "city")
    // private String city;


    @Column(name = "pincode")
    private int pincode;


    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "gcm")
    private String gcm;

    @Column(name = "password")
    private String password;


    //@Column(name = "productType")
    // private String productType;


    // public String getBrand() {
    //   return brand;
    // }


    //public void setBrand(String brand) {
    // this.brand = brand;
    //}


    // public String getProductType() {
    //return productType;
    // }


    //public void setProductType(String productType) {
    //this.productType = productType;
    //  }


    public int getServicePartnerId() {
        return servicePartnerId;
    }


    public void setServicePartnerId(int servicePartnerId) {
        this.servicePartnerId = servicePartnerId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String geteMail() {
        return eMail;
    }


    public void seteMail(String eMail) {
        this.eMail = eMail;
    }


    public String getMobile() {
        return mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    // public String getAddress() {
    // return address;
    //}


    //public void setAddress(String address) {
    // this.address = address;
    // }


    //public String getCity() {
    // return city;
    //}


    // public void setCity(String city) {
    // this.city = city;
    // }


    public int getPincode() {
        return pincode;
    }


    public void setPincode(int pincode) {
        this.pincode = pincode;
    }


    // public int getType() {
    //   return type;
    // }


    //public void setType(int type) {
    // this.type = type;
    //}
    @Override
    public String toString(){
        return "Service Partner ID:"+ servicePartnerId +" name"+name+"eMail "+eMail+"mobile "+mobile+"pincode"+pincode+" gcm:"+gcm+"password"+password+"";
    }
}
