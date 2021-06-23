package cn.linked.baselib.socket;

import com.alibaba.fastjson.JSON;

import cn.linked.baselib.entity.NetworkData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundDataParseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData<?> message= JSON.parseObject(msg.toString(),NetworkData.class);
        super.channelRead(ctx, message);
    }

}
