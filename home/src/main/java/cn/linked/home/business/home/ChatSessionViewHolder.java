package cn.linked.home.business.home;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.linked.commonlib.view.RoundRectShadowCoverView;
import cn.linked.home.R;
import lombok.Getter;

@Getter
public class ChatSessionViewHolder extends RecyclerView.ViewHolder {

    private RoundRectShadowCoverView sessionImgView;
    private TextView sessionTitleText;
    private TextView sessionMessageText;
    private TextView sessionMessageTimeText;
    private TextView sessionMessageNumText;

    public ChatSessionViewHolder(@NonNull View itemView) {
        super(itemView);
        sessionImgView = itemView.findViewById(R.id.sessionImg);
        sessionTitleText = itemView.findViewById(R.id.sessionTitle);
        sessionMessageText = itemView.findViewById(R.id.sessionMessage);
        sessionMessageTimeText = itemView.findViewById(R.id.sessionMessageTime);
        sessionMessageNumText = itemView.findViewById(R.id.sessionMessageNum);
    }

}
