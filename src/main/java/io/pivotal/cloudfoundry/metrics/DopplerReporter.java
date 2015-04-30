package io.pivotal.cloudfoundry.metrics;

import com.codahale.metrics.*;
import events.*;
import events.EnvelopeOuterClass.Envelope;
import events.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by vcarvalho on 4/16/15.
 */
public class DopplerReporter extends ScheduledReporter {

    private static final Logger logger = LoggerFactory.getLogger(DopplerReporter.class);

    public static Builder forRegistry(MetricRegistry registry){
        return  new Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private String secret;
        private String host;
        private Integer port;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private String origin;


        private Builder(MetricRegistry registry){
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        public Builder withHost(String host){
            this.host = host;
            return this;
        }

        public Builder withOrigin(String origin){
            this.origin = origin;
            return this;
        }

        public Builder withPort(Integer port){
            this.port = port;
            return this;
        }

        public Builder withSecret(String secret){
            this.secret = secret;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public DopplerReporter build(){
            return  new DopplerReporter(registry,host,port,secret,rateUnit,durationUnit,filter,clock,origin);
        }

    }


    private DopplerClient client;
    private Clock clock;
    private String origin;

    private DopplerReporter(MetricRegistry registry, String dopplerHost, Integer dopplerPort, String sharedSecret, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter metricFilter, Clock clock, String origin){
        super(registry,"doppler-reporter",metricFilter,rateUnit,durationUnit);
        this.client = new DopplerClient(dopplerHost,dopplerPort,sharedSecret);
        this.clock = clock;
        this.origin = origin;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        for(Map.Entry<String, Gauge> entry : gauges.entrySet()){
            reportGauge(entry.getKey(),entry.getValue());
        }
    }


    private void reportGauge(String name, Gauge gauge){
        if(!Number.class.isAssignableFrom(gauge.getValue().getClass())){
            logger.warn("Metric {} is not a number and can't be sent do doppler as a Metric value",name);
            return;
        }
       try {
           Envelope envelope = Envelope.newBuilder().setEventType(Envelope.EventType.ValueMetric).setOrigin(origin).setTimestamp(clock.getTime())
                   .setValueMetric(Metric.ValueMetric.newBuilder().setValue(Double.valueOf(gauge.getValue().toString())).setName(name).setUnit("count").build()).build();
           client.publish(envelope);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

}
