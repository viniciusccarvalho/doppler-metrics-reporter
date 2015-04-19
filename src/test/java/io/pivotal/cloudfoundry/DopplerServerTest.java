package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import events.Metric;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.pivotal.cloudfoundry.metrics.DopplerClient;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by vcarvalho on 4/17/15.
 */

public class DopplerServerTest {

    private static DopplerServer server;

    @BeforeClass
    public static void beforeClass() throws Exception{
        Thread t1 = new Thread(new DopplerServer());
        t1.start();
    }

    @Test
    public void sendMetric() throws Exception{
        DopplerClient client = new DopplerClient("localhost",3453,"cl0udc0w");
        EnvelopeOuterClass.Envelope envelope = EnvelopeOuterClass.Envelope.newBuilder().setOrigin("localhost").setEventType(EnvelopeOuterClass.Envelope.EventType.ValueMetric).setTimestamp(System.currentTimeMillis())
                .setValueMetric(Metric.ValueMetric.newBuilder().setValue(1.0).setName("name").setUnit("P")).build();
        client.publish(envelope);
        Thread.sleep(2000L);

    }




}
