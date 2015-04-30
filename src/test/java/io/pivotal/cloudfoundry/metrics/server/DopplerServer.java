package io.pivotal.cloudfoundry.metrics.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.pivotal.cloudfoundry.metrics.Signer;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vcarvalho on 4/17/15.
 */

public class DopplerServer implements Runnable{

    private Bootstrap b;
    private EventLoopGroup group;
    private int port;

    public DopplerServer(Signer signer, int port) {
        this.group = new NioEventLoopGroup();
        this.b = new Bootstrap();
        this.port = port;
        this.b.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new DropsOndeProtocolHandler(signer,true))
                .option(ChannelOption.SO_BROADCAST, false)
                ;
    }




    @Override
    public void run() {
        try {
            b.bind(new InetSocketAddress(port)).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        } finally {

        }
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
