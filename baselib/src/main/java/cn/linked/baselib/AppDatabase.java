package cn.linked.baselib;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.linked.baselib.entity.ChatGroup;
import cn.linked.baselib.entity.ChatGroupMember;
import cn.linked.baselib.entity.User;
import cn.linked.baselib.repository.dao.ChatDao;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.repository.dao.UserDao;

@Database(entities = {ChatMessage.class, ChatGroup.class, User.class, ChatGroupMember.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChatDao chatDao();

    public abstract UserDao userDao();

}
