package io.pivotal.cloudfoundry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.pivotal.cloudfoundry.metrics.DopplerReporter;

import java.net.InetSocketAddress;

/**
 * Created by vcarvalho on 4/17/15.
 */

public class DopplerServer implements Runnable{

    private Bootstrap b;
    private EventLoopGroup group;
    public DopplerServer() {
        this.group = new NioEventLoopGroup();
        this.b = new Bootstrap();
        this.b.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new DropsOndeProtocolHandler())
                .option(ChannelOption.SO_BROADCAST, false)
                ;
    }


    @Override
    public void run() {
        try {
            b.bind(new InetSocketAddress(3453)).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        } finally {

        }
    }
}
