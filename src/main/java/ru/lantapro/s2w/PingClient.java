package ru.lantapro.s2w;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

@Singleton
@Startup
public class PingClient {

    private final Logger logger = LoggerFactory.getLogger(PingClient.class);

    private WebTarget target;

    @PostConstruct
    public void init() {
        ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder();
        resteasyClientBuilder.establishConnectionTimeout(600, TimeUnit.MILLISECONDS);
        resteasyClientBuilder.connectionCheckoutTimeout(600, TimeUnit.MILLISECONDS);
        resteasyClientBuilder.socketTimeout(600, TimeUnit.MILLISECONDS);
        ResteasyClient client = resteasyClientBuilder.build();
        target = client.target("http://109.248.46.61:8180/");
    }

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    @AccessTimeout(value = 10, unit = TimeUnit.SECONDS)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Lock(LockType.WRITE)
    public void poke() {
        try {
            for (int step = 0; step < 5; step ++) {
                target.request(MediaType.APPLICATION_JSON).get(String.class);
            }
        } catch (Exception ex) {
            logger.error("POKE failed: {" + ex.getClass().toString() + "} " + ex.getMessage(), ex);
        }
    }

}
