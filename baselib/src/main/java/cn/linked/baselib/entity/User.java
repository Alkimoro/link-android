package cn.linked.baselib.entity;

import java.util.Date;

import lombok.Data;

@Data
public class User {

    private Long id;

    private String name;
    private String password;
    private int gender;
    private String birthday;
    private String mail;
    private String phoneNumber;
    private String address;
    private String signature;
    private String imageUrl;
    private Date modifyTime;
    private Date createTime;

}
