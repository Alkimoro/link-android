package cn.linked.baselib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.alibaba.fastjson.JSON;

import java.util.Date;

import cn.linked.baselib.room.converter.DateAndLongConverter;
import lombok.Data;

@Data
@Entity(tableName = "chat_message", indices = {@Index(value = {"group_id","sequence_number"}, unique = true)})
@TypeConverters(DateAndLongConverter.class)
public class ChatMessage implements Parcelable {

    // 数据库的主键id 到达服务器 存入数据库时自动设置
    @PrimaryKey
    @NonNull
    private String id = "";

    @Ignore
    private Long ackId;

    @ColumnInfo(name = "owner")
    // 发送到服务器后自动根据sessionId获取userId设置
    private Long owner;
    @ColumnInfo(name = "group_id")
    private String groupId;
    // 该参数 为到达服务器时 存入数据库时自动设置 ack中会返回该参数
    // 改参数在服务器为自增字段，第一条数据的该字段值为 1
    @ColumnInfo(name = "sequence_number")
    private Long sequenceNumber;
    @ColumnInfo(name = "message")
    private String message;
    // 该参数 为到达服务器时 自动设置
    @ColumnInfo(name = "send_time")
    private Date sendTime;

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            ChatMessage result;
            String jsonString = in.readString();
            result = JSON.parseObject(jsonString, ChatMessage.class);
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
