package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.pivotal.cloudfoundry.metrics.Signer;


/**
 * Created by vcarvalho on 4/17/15.
 */

public class DropsOndeProtocolHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    Signer signer = new Signer("cl0udc0w");
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] dst = new byte[packet.content().readableBytes()];
        packet.content().getBytes(0,dst);
        System.out.println(EnvelopeOuterClass.Envelope.parseFrom(signer.decrypt(dst)));
    }
}
