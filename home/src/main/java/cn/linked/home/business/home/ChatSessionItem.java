package cn.linked.home.business.home;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.linked.baselib.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSessionItem {

    private static DateFormat dateFormat = new SimpleDateFormat("ahh:mm", Locale.getDefault());
    private static final String MESSAGE_SEPARATE = "：";

    private String groupId;
    private String title;
    private String imageURL;
    private String prefix;
    private Long num;

    private ChatMessage chatMessage;

    public String getTime() {
        if(chatMessage != null) {
            return dateFormat.format(chatMessage.getSendTime());
        }
        return null;
    }

    public String getNum() {
        if(num == null) {
            return null;
        }else {
            if(num <= 99) {
                return num + "";
            }else {
                return "99+";
            }
        }
    }

    public String getMessage() {
        String f = "";
        if(prefix != null && !"".equals(prefix)) {
            f = prefix + MESSAGE_SEPARATE;
        }
        if(chatMessage != null) {
            f += chatMessage.getMessage();
        }
        return f;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(groupId == null) { return false; }
        if(obj instanceof ChatSessionItem) {
            return groupId.equals(((ChatSessionItem) obj).groupId);
        }
        return false;
    }

}
