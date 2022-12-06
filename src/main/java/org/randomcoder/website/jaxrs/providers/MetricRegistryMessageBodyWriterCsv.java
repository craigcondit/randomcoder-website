package org.randomcoder.website.jaxrs.providers;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Provider
@Produces("text/csv")
public class MetricRegistryMessageBodyWriterCsv implements MessageBodyWriter<MetricRegistry> {

    private static final Logger logger = LoggerFactory.getLogger(MetricRegistryMessageBodyWriterCsv.class);

    private static final String DELIMITER = ",";

    private static final String HEADER =
            "name,type,value,count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit\r\n";

    private static final String GAUGE_FMT = "\"%s\",\"%s\",\"%s\",,,,,,,,,,,,,,,,,\r\n";
    private static final String COUNTER_FMT = "\"%s\",\"%s\",%d,,,,,,,,,,,,,,,,,\r\n";
    private static final String HISTOGRAM_FMT = "\"%s\",\"%s\",,%d,%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,,,,,,\r\n";
    private static final String METER_FMT = "\"%s\",\"%s\",,%d,,,,,,,,,,,%f,%f,%f,%f,\"events/%s\",\r\n";
    private static final String TIMER_FMT = "\"%s\",\"%s\",,%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,\"calls/%s\",\"%s\"\r\n";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MetricRegistry.class;
    }

    @Override
    public void writeTo(MetricRegistry registry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(HEADER.getBytes(StandardCharsets.UTF_8));

        logger.info("header commas: {}", commaCount(HEADER));
        logger.info("gauge commas: {}", commaCount(GAUGE_FMT));
        logger.info("counter commas: {}", commaCount(COUNTER_FMT));
        logger.info("histogram commas: {}", commaCount(HISTOGRAM_FMT));
        logger.info("meter commas: {}", commaCount(METER_FMT));
        logger.info("timer commas: {}", commaCount(TIMER_FMT));

        for (var entry : registry.getGauges().entrySet()) {
            reportGauge(entityStream, entry.getKey(), entry.getValue());
        }
        for (var entry : registry.getCounters().entrySet()) {
            reportCounter(entityStream, entry.getKey(), entry.getValue());
        }
        for (var entry : registry.getHistograms().entrySet()) {
            reportHistogram(entityStream, entry.getKey(), entry.getValue());
        }
        for (var entry : registry.getMeters().entrySet()) {
            reportMeter(entityStream, entry.getKey(), entry.getValue());
        }
        for (var entry : registry.getTimers().entrySet()) {
            reportTimer(entityStream, entry.getKey(), entry.getValue());
        }
    }

    private int commaCount(String value) {
        int count = 0;
        for (char c : value.toCharArray()) {
            if (c == ',') {
                count++;
            }
        }
        return count;
    }

    private void reportGauge(OutputStream os, String key, Gauge<?> gauge) throws IOException {
        report(os, "gauge", key, GAUGE_FMT, gauge.getValue());
    }

    private void reportCounter(OutputStream os, String key, Counter counter) throws IOException {
        report(os, "counter", key, COUNTER_FMT, counter.getCount());
    }

    private void reportHistogram(OutputStream os, String key, Histogram histogram) throws IOException {
        var snapshot = histogram.getSnapshot();
        report(os, "counter", key, COUNTER_FMT,
                histogram.getCount(),
                snapshot.getMax(),
                snapshot.getMean(),
                snapshot.getMin(),
                snapshot.getStdDev(),
                snapshot.getMedian(),
                snapshot.get75thPercentile(),
                snapshot.get95thPercentile(),
                snapshot.get98thPercentile(),
                snapshot.get99thPercentile(),
                snapshot.get999thPercentile());
    }

    private void reportMeter(OutputStream os, String key, Meter meter) throws IOException {
        report(os, "meter", key, METER_FMT,
                meter.getCount(),
                convertRate(meter.getMeanRate()),
                convertRate(meter.getOneMinuteRate()),
                convertRate(meter.getFiveMinuteRate()),
                convertRate(meter.getFifteenMinuteRate()),
                getRateUnit());

    }

    private void reportTimer(OutputStream os, String key, Timer timer) throws IOException {
        var snapshot = timer.getSnapshot();
        report(os, "timer", key, TIMER_FMT,
                timer.getCount(),
                convertDuration(snapshot.getMax()),
                convertDuration(snapshot.getMean()),
                convertDuration(snapshot.getMin()),
                convertDuration(snapshot.getStdDev()),
                convertDuration(snapshot.getMedian()),
                convertDuration(snapshot.get75thPercentile()),
                convertDuration(snapshot.get95thPercentile()),
                convertDuration(snapshot.get98thPercentile()),
                convertDuration(snapshot.get99thPercentile()),
                convertDuration(snapshot.get999thPercentile()),
                convertRate(timer.getMeanRate()),
                convertRate(timer.getOneMinuteRate()),
                convertRate(timer.getFiveMinuteRate()),
                convertRate(timer.getFifteenMinuteRate()),
                getRateUnit(),
                getDurationUnit());
    }

    private String getRateUnit() {
        return "millisecond";
    }

    private String getDurationUnit() {
        return "millisecond";
    }

    private double convertDuration(double duration) {
        return duration / 1_000_000d;
    }

    private double convertRate(double rate) {
        return rate * 1000d;
    }

    private void report(OutputStream os, String type, String key, String template, Object... values) throws IOException {
        Object[] params = new Object[values.length + 2];
        params[0] = type;
        params[1] = key;
        for (int i = 0; i < values.length; i++) {
            params[i + 2] = values[i];
        }
        os.write(String.format(template, params).getBytes(StandardCharsets.UTF_8));
    }

}
