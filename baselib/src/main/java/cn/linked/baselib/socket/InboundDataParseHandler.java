package cn.linked.baselib.socket;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import cn.linked.baselib.entity.NetworkData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundDataParseHandler extends ChannelInboundHandlerAdapter {

    public static final String TAG = "InboundDataParseHandler";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData<?> message= JSON.parseObject(msg.toString(),NetworkData.class);
        Log.i(TAG, "ChatClient收到消息:" + message.toString());
        super.channelRead(ctx, message);
    }

}
