package io.pivotal.cloudfoundry.metrics;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by vcarvalho on 4/16/15.
 */
public class DopplerReporter extends ScheduledReporter {

    private static final Logger logger = LoggerFactory.getLogger(DopplerReporter.class);




    public DopplerReporter(MetricRegistry registry, String etcdHosts, String sharedSecret, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter metricFilter){
        super(registry,"doppler-reporter",metricFilter,rateUnit,durationUnit);
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {

    }
}
