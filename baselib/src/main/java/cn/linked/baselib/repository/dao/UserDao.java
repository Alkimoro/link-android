package cn.linked.baselib.repository.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import cn.linked.baselib.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertUser(User user);

    @Query("SELECT * FROM user WHERE id = :id")
    User findUserById(@NonNull Long id);

}
