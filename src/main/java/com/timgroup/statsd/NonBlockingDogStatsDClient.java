package com.timgroup.statsd;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * A simple StatsD client which implements some extensions to the StatsD
 * protocol as defined by Datadog: http://docs.datadoghq.com/guides/dogstatsd/#datagram-format
 */
public class NonBlockingDogStatsDClient extends NonBlockingStatsDClient {
    public NonBlockingDogStatsDClient(String prefix, String hostname, int port) throws StatsDClientException {
        super(prefix, hostname, port, NO_OP_HANDLER);
    }


    public void count(String aspect, long delta, AbstractList tags) {
        count(aspect, delta, tags, 1.0);
    }

    public void count(String aspect, long delta, AbstractList tags, double sampleRate) {
        send(messageFor(aspect, Long.toString(delta), "c", tags, sampleRate));
    }

    public void count(String aspect, long delta, AbstractMap<String, String> tags) {
        count(aspect, delta, tags, 1.0);
    }

    public void count(String aspect, long delta, AbstractMap<String, String> tags, double sampleRate) {
        send(messageFor(aspect, Long.toString(delta), "c", tags, sampleRate));
    }


    public void recordGaugeValue(String aspect, long value, AbstractList tags) {
        sendGaugeWithTags(recordGaugeCommon(aspect, Long.toString(value), value < 0, false), tags);
    }

    public void recordGaugeValue(String aspect, long value, AbstractMap<String, String> tags) {
        String gauge = recordGaugeCommon(aspect, Long.toString(value), value < 0, false);
        sendGaugeWithTags(gauge, commaSeparatedTagValues(tags));
    }

    public void recordGaugeDelta(String aspect, long value, AbstractList tags) {
        sendGaugeWithTags(recordGaugeCommon(aspect, Long.toString(value), value < 0, true), tags);
    }

    public void recordGaugeDelta(String aspect, long value, AbstractMap<String, String> tags) {
        String gauge = recordGaugeCommon(aspect, Long.toString(value), value < 0, true);
        sendGaugeWithTags(gauge, commaSeparatedTagValues(tags));
    }


    protected void sendGaugeWithTags(String gauge, AbstractList tags) {
        send(String.format((Locale)null, "%s|#%s", gauge, String.join(",", tags)));
    }

    protected String messageFor(String aspect, String value, String type, AbstractList tags) {
        return messageFor(aspect, value, type, tags, 1.0);
    }

    protected String messageFor(String aspect, String value, String type, AbstractList tags, double sampleRate) {
        String commaSeparatedTags = String.join(",", tags);

        return String.format((Locale)null, "%s|#%s", messageFor(aspect, value, type, sampleRate), commaSeparatedTags);
    }

    protected String messageFor(String aspect, String value, String type, AbstractMap<String, String> tags) {
        return messageFor(aspect, value, type, tags, 1.0);
    }

    protected String messageFor(String aspect, String value, String type, AbstractMap<String, String> tags, double sampleRate) {

        return messageFor(aspect, value, type, commaSeparatedTagValues(tags), sampleRate);
    }


    private ArrayList commaSeparatedTagValues(AbstractMap<String, String> tags) {
        ArrayList tagValues = new ArrayList();
        for (Entry<String, String> entry : tags.entrySet()) {
            tagValues.add(String.format((Locale)null, "%s:%s", entry.getKey(), entry.getValue()));
        }

        return tagValues;
    }
}
