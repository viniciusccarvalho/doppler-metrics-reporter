package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


/**
 * Created by vcarvalho on 4/17/15.
 */

public class DropsOndeProtocolHandler extends SimpleChannelInboundHandler<DatagramPacket>{


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

        System.out.println(EnvelopeOuterClass.Envelope.parseFrom(packet.content().array()));
    }
}
