package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import events.Metric;
import io.pivotal.cloudfoundry.metrics.Signer;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by vcarvalho on 4/18/15.
 */
public class SignerTest {

    @Test
    public void testSigner() throws Exception{
        Signer signer = new Signer("cl0udc0w");
        EnvelopeOuterClass.Envelope envelope = EnvelopeOuterClass.Envelope.newBuilder().setOrigin("localhost").setEventType(EnvelopeOuterClass.Envelope.EventType.ValueMetric).setTimestamp(System.currentTimeMillis())
                .setValueMetric(Metric.ValueMetric.newBuilder().setValue(1.0).setName("name").setUnit("P")).build();

        byte[] signed = signer.encrypt(envelope.toByteArray());
        Assert.assertEquals(envelope.getOrigin(), EnvelopeOuterClass.Envelope.parseFrom(signer.decrypt(signed)).getOrigin());

    }


}
