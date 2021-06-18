package cn.linked.baselib.room.converter;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateAndLongConverter {

    @TypeConverter
    public Long dateToLong(Date date) {
        if(date == null) { return null; }
        return date.getTime();
    }

    @TypeConverter
    public Date longToDate(Long time) {
        if(time == null) { return null; }
        return new Date(time);
    }

}
