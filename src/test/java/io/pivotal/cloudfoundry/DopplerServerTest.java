package io.pivotal.cloudfoundry;

import events.EnvelopeOuterClass;
import events.Metric;
import io.pivotal.cloudfoundry.metrics.DopplerClient;
import io.pivotal.cloudfoundry.metrics.Signer;
import io.pivotal.cloudfoundry.metrics.server.DopplerServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by vcarvalho on 4/17/15.
 */

public class DopplerServerTest {

    private static DopplerServer server;
    private static final String sharedSecret = "cl0udc0w";
    private static final int port = 3453;

    @BeforeClass
    public static void beforeClass() throws Exception{
        server = new DopplerServer(new Signer(sharedSecret),port);
        Thread t1 = new Thread(server);
        t1.start();
    }

    @Test
    public void sendMetric() throws Exception{
        DopplerClient client = new DopplerClient("localhost",port,sharedSecret);
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        int messages = 10;
        CountDownLatch latch = new CountDownLatch(messages);
        Long start = System.currentTimeMillis();
        for(int i=0;i<poolSize;i++){
            pool.submit(new MetricProducer(client,latch));
        }
        latch.await();
        Long end = System.currentTimeMillis();
        System.out.println(String.format("Producing %d messages took %d ms", messages, (end - start)));
    }

    @AfterClass
    public static void clean(){
        server.shutdown();
    }



    class MetricProducer implements Runnable{

        private DopplerClient client;
        private CountDownLatch latch;
        private Random random = new Random();

        public MetricProducer(DopplerClient client, CountDownLatch latch) {
            this.client = client;
            this.latch = latch;
        }

        @Override
        public void run() {
          while(true){

              try {
                  EnvelopeOuterClass.Envelope envelope = EnvelopeOuterClass.Envelope.newBuilder().setOrigin("localhost").setEventType(EnvelopeOuterClass.Envelope.EventType.ValueMetric).setTimestamp(System.currentTimeMillis())
                          .setValueMetric(Metric.ValueMetric.newBuilder().setValue(random.nextDouble()).setName("name").setUnit("P")).build();
                  client.publish(envelope);
                  latch.countDown();
              } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("Producer shutting down");
                  break;
              }
          }
        }
    }



}
