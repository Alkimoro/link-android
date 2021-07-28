package cn.linked.baselib.repository.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import cn.linked.baselib.entity.ChatGroup;
import cn.linked.baselib.entity.ChatGroupMember;
import cn.linked.baselib.entity.ChatMessage;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChatMessage(ChatMessage... messages);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChatMessage(List<ChatMessage> chatMessageList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertChatGroup(List<ChatGroup> chatGroupList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertChatGroup(ChatGroup... chatGroups);

    @Query("SELECT * FROM chat_group WHERE id = :groupId")
    ChatGroup findChatGroupById(@NonNull String groupId);

    @Transaction
    @Query("SELECT * FROM chat_message WHERE group_id = :groupId AND sequence_number <= :maxSequenceNumber " +
            "ORDER BY sequence_number DESC LIMIT :num")
    List<ChatMessage> findChatMessage(@NonNull String groupId,@NonNull Long maxSequenceNumber,@NonNull Integer num);
     @Transaction
     @Query("SELECT * FROM chat_message WHERE group_id = :groupId AND sequence_number <= :maxSequenceNumber " +
            "ORDER BY sequence_number DESC")
    List<ChatMessage> findChatMessage(@NonNull String groupId,@NonNull Long maxSequenceNumber);

    @Query("SELECT * FROM chat_group_member WHERE group_id = :groupId")
    List<ChatGroupMember> findChatGroupMemberByGroupId(@NonNull String groupId);

    @Query("SELECT * FROM chat_group_member WHERE group_id = :groupId AND user_id = :userId")
    ChatGroupMember findUserChatGroupMember(@NonNull String groupId, @NonNull Long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertChatGroupMember(List<ChatGroupMember> chatGroupMemberList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertChatGroupMember(ChatGroupMember chatGroupMember);

    @Query("UPDATE chat_group_member SET have_read_message_max_sequence_num = :maxSequenceNum WHERE group_id = :groupId AND user_id = :userId")
    void setUserHaveReadMessageMaxSequenceNum(String groupId, Long userId, Long maxSequenceNum);

}
