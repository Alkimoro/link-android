package cn.linked.baselib.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import cn.linked.baselib.room.converter.DateAndLongConverter;
import lombok.Data;

@Data
@Entity(tableName = "chat_group_member", indices = {@Index(value = {"group_id","user_id"}, unique = true)})
@TypeConverters({DateAndLongConverter.class})
public class ChatGroupMember {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "group_id")
    private String groupId;// GroupId

    @ColumnInfo(name = "user_id")
    private Long userId;// 用户ID

    @ColumnInfo(name = "alias")
    private String alias;

    // 用户分组 组名
    @ColumnInfo(name = "user_group_name")
    private String userGroupName;

    // 用户已读消息的最大 SequenceNum
    @ColumnInfo(name = "have_read_message_max_sequence_num")
    private Long haveReadMessageMaxSequenceNum;

    @ColumnInfo(name = "join_time")
    private Date joinTime;

}
