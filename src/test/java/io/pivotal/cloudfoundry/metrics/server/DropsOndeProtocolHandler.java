package io.pivotal.cloudfoundry.metrics.server;

import events.EnvelopeOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.pivotal.cloudfoundry.metrics.Signer;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by vcarvalho on 4/17/15.
 */

public class DropsOndeProtocolHandler extends SimpleChannelInboundHandler<DatagramPacket>{


    private Signer signer;
    private boolean verbose;

    public DropsOndeProtocolHandler(Signer signer, boolean verbose) {
        this.signer = signer;
        this.verbose = verbose;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] dst = new byte[packet.content().readableBytes()];
        packet.content().getBytes(0,dst);
        if(verbose){
            System.out.println(EnvelopeOuterClass.Envelope.parseFrom(signer.decrypt(dst)));
        }
    }
}
