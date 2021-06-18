package cn.linked.baselib.repository.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cn.linked.baselib.entity.ChatMessage;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChatMessage(ChatMessage... message);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChatMessage(List<ChatMessage> chatMessageList);

    @Query("SELECT * FROM chat_message WHERE group_id = :groupId AND sequence_number <= :maxSequenceNumber " +
            "ORDER BY sequence_number DESC LIMIT :num")
    List<ChatMessage> findChatMessage(@NonNull String groupId,@NonNull Long maxSequenceNumber,@NonNull Integer num);

}
