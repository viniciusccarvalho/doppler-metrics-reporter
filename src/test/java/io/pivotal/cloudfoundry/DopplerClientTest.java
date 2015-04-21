package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import events.Metric;
import io.pivotal.cloudfoundry.metrics.DopplerClient;
import org.junit.Test;

/**
 * Created by vcarvalho on 4/20/15.
 */
public class DopplerClientTest {

    @Test
    public void publishCustomMetric() throws Exception{
        DopplerClient client = new DopplerClient("10.68.105.28",3457,"c3887abea2f85c823456");
        EnvelopeOuterClass.Envelope envelope = EnvelopeOuterClass.Envelope.newBuilder().setOrigin("localhost").setEventType(EnvelopeOuterClass.Envelope.EventType.ValueMetric).setTimestamp(System.currentTimeMillis())
                .setValueMetric(Metric.ValueMetric.newBuilder().setValue(1.0).setName("name").setUnit("P")).build();
        for(int i=0;i<1000;i++) {
            client.publish(envelope);
            Thread.sleep(100L);
        }
    }
}
