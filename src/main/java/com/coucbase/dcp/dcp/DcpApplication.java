package com.coucbase.dcp.dcp;

import com.couchbase.client.dcp.*;
import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.dcp.transport.netty.ChannelFlowController;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.util.CharsetUtil;
import com.google.gson.JsonParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

//import com.couchbase.client.java.document.json.JsonObject;


@SpringBootApplication
public class DcpApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DcpApplication.class, args);

        final JsonParser parser = new JsonParser();
        final Client client = Client.configure()
                .hostnames("localhost")
                .bucket("sandbox")
                .username("Administrator")
                .password("admin123")
                .build();
        // Don't do anything with control events in this example
        client.controlEventHandler(new ControlEventHandler() {
            public void onEvent(ChannelFlowController flowController, ByteBuf event) {
                event.release();
            }
        });
        client.dataEventHandler(new DataEventHandler() {
            public void onEvent(ChannelFlowController flowController, ByteBuf event) {
                if (DcpMutationMessage.is(event)) {
                    com.google.gson.JsonObject obj = parser.parse(DcpMutationMessage.content(event).toString(CharsetUtil.UTF_8)).getAsJsonObject();
                    //if (obj.get("rating") != null && obj.get("rating").getAsInt() > 4) {
                    // OMG, marketing guys gonna love this stuff...
                    System.out.println("Couchbase Object: " + obj.toString());
                    // }
                } else if (DcpDeletionMessage.is(event)) {
                    // System.out.println("Goodbye, tasty beer! " + DcpDeletionMessage.toString(event));
                }
                event.release();
            }
        });
        // Connect the sockets
        client.connect().await();
        // Initialize the state (start now, never stop)
        client.initializeState(StreamFrom.BEGINNING, StreamTo.INFINITY).await();
        // Start streaming on all partitions
        client.startStreaming().await();
        // Sleep for some time to print the mutations
        // The printing happens on the IO threads!
        Thread.sleep(TimeUnit.MINUTES.toMillis(10));
        // Once the time is over, shutdown.
        client.disconnect().await();
    }
}
