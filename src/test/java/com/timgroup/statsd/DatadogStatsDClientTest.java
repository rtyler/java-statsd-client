package com.timgroup.statsd;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

public final class DatadogStatsDClientTest {

    private static final int STATSD_SERVER_PORT = 17254;

    private final NonBlockingDogStatsDClient client = new NonBlockingDogStatsDClient("my.prefix", "localhost", STATSD_SERVER_PORT);
    private final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

    @After
    public void stop() throws Exception {
        client.stop();
        server.stop();
    }

    @Test public void
    formats_tags_list_properly() throws Exception {
        ArrayList tags = new ArrayList();
        tags.add("development");
        tags.add("local");

        String message = client.messageFor("foo.var", "1", "c", tags);

        assertThat(message, equalTo("my.prefix.foo.var:1|c|#development,local"));
    }

    @Test public void
    formats_tags_map_properly() throws Exception {
        HashMap<String, String> tags = new HashMap<>();
        tags.put("partition", "2");

        String message = client.messageFor("foo.var", "1", "c", tags);

        assertThat(message, equalTo("my.prefix.foo.var:1|c|#partition:2"));
    }
}

