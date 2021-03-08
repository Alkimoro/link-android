package cn.linked.baselib.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;

public abstract class SocketClient extends ChannelInitializer<SocketChannel> {

    abstract public void init();
    abstract public Future<?> connect();
    abstract public Future<?> close();

}
