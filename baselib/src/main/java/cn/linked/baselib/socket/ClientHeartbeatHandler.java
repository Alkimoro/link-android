package cn.linked.baselib.socket;

import cn.linked.baselib.entity.NetworkData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state()==IdleState.WRITER_IDLE) {
            ctx.channel().writeAndFlush(NetworkData.heartbeatMessage().toJsonString());
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
