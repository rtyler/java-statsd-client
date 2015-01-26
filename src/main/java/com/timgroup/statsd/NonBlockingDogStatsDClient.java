package com.timgroup.statsd;

import java.util.AbstractList;
import java.util.Locale;

/**
 * A simple StatsD client which implements some extensions to the StatsD
 * protocol as defined by Datadog: http://docs.datadoghq.com/guides/dogstatsd/#datagram-format
 */
public class NonBlockingDogStatsDClient extends NonBlockingStatsDClient {
    public NonBlockingDogStatsDClient(String prefix, String hostname, int port) throws StatsDClientException {
        super(prefix, hostname, port, NO_OP_HANDLER);
    }

    protected String messageFor(String aspect, String value, String type, AbstractList tags) {
        return messageFor(aspect, value, type, tags, 1.0);
    }

    protected String messageFor(String aspect, String value, String type, AbstractList tags, double sampleRate) {
        String commaSeparatedTags = String.join(",", tags);

        return String.format((Locale)null, "%s|#%s", messageFor(aspect, value, type, sampleRate), commaSeparatedTags);
    }
}
