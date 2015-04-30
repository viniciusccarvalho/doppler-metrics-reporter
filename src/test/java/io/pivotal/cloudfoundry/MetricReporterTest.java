package io.pivotal.cloudfoundry;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import io.pivotal.cloudfoundry.metrics.DopplerReporter;
import io.pivotal.cloudfoundry.metrics.Signer;
import io.pivotal.cloudfoundry.metrics.server.DopplerServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by vcarvalho on 4/30/15.
 */
public class MetricReporterTest {

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
    public void reporter() throws Exception{
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new GarbageCollectorMetricSet());
        DopplerReporter reporter = DopplerReporter.forRegistry(registry).withHost("localhost").withPort(port).withOrigin("app-0").withSecret(sharedSecret).build();
        reporter.start(1, TimeUnit.SECONDS);
        Thread.sleep(5000L);
    }

    @AfterClass
    public static void clean(){
        server.shutdown();
    }

}
