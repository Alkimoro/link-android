package cn.linked.home.business.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.linked.baselib.ui.SimpleViewHolder;
import cn.linked.home.R;
import lombok.Getter;

public class ChatSessionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Getter
    private final List<ChatSessionItem> contentList;
    private final LayoutInflater layoutInflater;

    public ChatSessionRecyclerAdapter(@NonNull Context context, @NonNull List<ChatSessionItem> contentList) {
        layoutInflater = LayoutInflater.from(context);
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            return new SimpleViewHolder(layoutInflater.inflate(R.layout.empty_layout, parent, false));
        }
        return new ChatSessionViewHolder(layoutInflater.inflate(R.layout.item_home_session, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(position == 0) { return; }
        ChatSessionViewHolder holder = (ChatSessionViewHolder) viewHolder;
        ChatSessionItem item = contentList.get(position);
        holder.getSessionImgView().setContentBackGroundURL(item.getImageURL());
        holder.getSessionTitleText().setText(item.getTitle());
        holder.getSessionMessageText().setText(item.getMessage());
        holder.getSessionMessageTimeText().setText(item.getTime());
        if(item.getNum() != null) {
            holder.getSessionMessageNumText().setText(String.format("%s", item.getNum()));
        }else {
            holder.getSessionMessageNumText().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }



}
