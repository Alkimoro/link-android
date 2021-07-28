package cn.linked.baselib.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import cn.linked.baselib.config.Constant;
import cn.linked.baselib.room.converter.DateAndLongConverter;
import lombok.Data;

@Data
@Entity(tableName = "user")
@TypeConverters(DateAndLongConverter.class)
public class User {

    @PrimaryKey
    @NonNull
    private Long id = Constant.INVALID_ID;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "gender")
    private int gender;
    @ColumnInfo(name = "birthday")
    private String birthday;
    @ColumnInfo(name = "mail")
    private String mail;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "signature")
    private String signature;
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    @ColumnInfo(name = "create_time")
    private Date createTime;

}
