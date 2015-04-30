package io.pivotal.cloudfoundry;

import com.google.common.primitives.Longs;
import events.EnvelopeOuterClass;
import events.Metric;
import events.Uuid;
import io.pivotal.cloudfoundry.metrics.Signer;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by vcarvalho on 4/18/15.
 */
public class SignerTest {

    @Test
    public void testSigner() throws Exception{
        Signer signer = new Signer("cl0udc0w");
        Signer signer2 = new Signer("cl0udc0w");
        EnvelopeOuterClass.Envelope envelope = EnvelopeOuterClass.Envelope.newBuilder().setOrigin("localhost").setEventType(EnvelopeOuterClass.Envelope.EventType.ValueMetric).setTimestamp(System.currentTimeMillis())
                .setValueMetric(Metric.ValueMetric.newBuilder().setValue(1.0).setName("name").setUnit("P")).build();

        byte[] signed = signer.encrypt(envelope.toByteArray());
        Assert.assertEquals(envelope.getOrigin(), EnvelopeOuterClass.Envelope.parseFrom(signer2.decrypt(signed)).getOrigin());
    }


}
