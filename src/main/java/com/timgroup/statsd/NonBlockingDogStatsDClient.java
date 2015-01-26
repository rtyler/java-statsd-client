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
        ArrayList tagValues = new ArrayList();
        for (Entry<String, String> entry : tags.entrySet()) {
            tagValues.add(String.format((Locale)null, "%s:%s", entry.getKey(), entry.getValue()));
        }

        return messageFor(aspect, value, type, tagValues, sampleRate);
    }
}
