package cn.linked.baselib;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.linked.baselib.repository.dao.ChatDao;
import cn.linked.baselib.entity.ChatMessage;

@Database(entities = {ChatMessage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChatDao chatDao();

}
