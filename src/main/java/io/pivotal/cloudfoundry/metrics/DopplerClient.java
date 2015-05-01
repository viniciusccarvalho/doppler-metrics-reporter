package io.pivotal.cloudfoundry.metrics;

import events.EnvelopeOuterClass;
import events.Metric;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Created by vcarvalho on 4/17/15.
 */
public class DopplerClient {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private String host;
    private Integer port;
    private Signer signer;

    public DopplerClient(String host, Integer port, String secret){
        this.host = host;
        this.port = port;
        this.signer = new Signer(secret);
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group).option(ChannelOption.SO_BROADCAST,false)
                .channel(NioDatagramChannel.class)
                .handler(new DiscardHandler());

    }


    public void publish(EnvelopeOuterClass.Envelope envelope){
        Channel channel;
        try {
            channel = bootstrap.bind(new InetSocketAddress(0)).sync().channel();
            byte[] signedData = signer.encrypt(envelope.toByteArray());
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(signedData),new InetSocketAddress(host,port))).sync();
            channel.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @ChannelHandler.Sharable
    class DiscardHandler extends SimpleChannelInboundHandler<DatagramPacket>{

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            System.out.printf("Received message of %d bytes",msg.content().array().length);
        }
    }



}
