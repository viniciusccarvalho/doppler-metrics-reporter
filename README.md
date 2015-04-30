# doppler-metrics-reporter

A [dropwizard metric](https://dropwizard.github.io/metrics/3.1.0/) [reporter](https://dropwizard.github.io/metrics/3.1.0/manual/third-party/)

This reporter will send data to a cloudfoundry firehose.

WIP: For now it only sends gauge data as a [Dropsonde ValueMetric](https://github.com/cloudfoundry/dropsonde-protocol/blob/master/events/README.md#events.ValueMetric) event.

## Quickstart

Package and install this library and create a simple spring boot app adding this as depedency. 

Deploy to your CF, and add the following environment variables:
```
DOPPLER_HOST
DOPPLER_PORT
DOPPLER_SECRET
```

``` java
@SpringBootApplication
public class Application {


    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    @Bean
    public MetricRegistry registry() throws Exception{
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new GarbageCollectorMetricSet());
        registry.registerAll(new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        registry.registerAll(new MemoryUsageGaugeSet());
        registry.registerAll(new ThreadStatesGaugeSet());

        DopplerReporter reporter = DopplerReporter.forRegistry(registry).withOrigin(env.getProperty("cloud.application.name")+"__"+env.getProperty("cloud.application.instance_index"))
                                    .withHost(env.getProperty("doppler.host"))
                                    .withPort(Integer.valueOf(env.getProperty("doppler.port")))
                                    .withSecret(env.getProperty("doppler.secret")).build();
        reporter.start(5L, TimeUnit.SECONDS);

        return registry;
    }


}
```

This will start the reporter and uses the Dropwizard plugin for JVM metrics.


The important bits are:

```
origin: This is how your metric will be identified by the firehose, in this example we are using a convention on CF components: this would output appname__index
Host: Your doppler Host
Port: Your doppler Port
Secret: Your doppler Shared secret (more bellow)
```

## How to find doppler info

 * You can find on your cf deployment manifest, look for loggregator_endpoint: shared_secret
 * You can ssh into any bosh vm and look for /var/vcap/jobs/metron_agent/config/agent_config.json file

## How to visualize the data

I'm working on a sibiling project for that, but you could use [NOAA](https://github.com/cloudfoundry/noaa) or my [XD firehose](https://github.com/viniciusccarvalho/xd-firehose)
 
