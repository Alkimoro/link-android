package cn.linked.baselib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;

import java.util.Date;

import lombok.Data;

@Data
public class ChatMessage implements Parcelable {

    // 数据库的主键id 到达服务器 存入数据库时自动设置
    private Long id;

    private Long ackId;

    private Long owner;
    private Long groupId;
    // 该参数 为到达服务器时 存入数据库时自动设置 ack中会返回该参数
    private Long sequenceNumber;
    private String message;
    // 该参数 为到达服务器时 自动设置
    private Date sendTime;

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            ChatMessage result;
            String jsonString=in.readString();
            result=JSON.parseObject(jsonString,ChatMessage.class);
            return result;
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(JSON.toJSONString(this));
    }
}
