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
@Entity(tableName = "chat_group")
@TypeConverters({DateAndLongConverter.class})
public class ChatGroup {

    public static final String DEFAULT_NAME = "default";

    public static final int ID_TYPE_AUTO_INC = 1;
    public static final int ID_TYPE_OBJECT_ID = 2;

    @PrimaryKey
    @NonNull
    private String id = Constant.EMPTY_STRING;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "type")
    private ChatGroupType type;
    // 当 type == TYPE_PRIVATE 时 该字段为 null; 因为在私聊情况下，不同用户视角下 image是不同的
    @ColumnInfo(name = "image_uri")
    private String imageUri;
    // 等级
    @ColumnInfo(name = "level")
    private Integer level;
    @ColumnInfo(name = "create_time")
    private Date createTime;

}
