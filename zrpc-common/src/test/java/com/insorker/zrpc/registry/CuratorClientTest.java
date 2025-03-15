package com.insorker.zrpc.registry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuratorClientTest {

    private static final Logger logger = LoggerFactory.getLogger(CuratorClientTest.class);

    @Test
    public void testConnect() throws Exception {
        CuratorClient curatorClient = new CuratorClient("1.94.213.53:2181");

        System.out.println(curatorClient.create("/app", "Hello".getBytes()));

        curatorClient.close();
    }
}
