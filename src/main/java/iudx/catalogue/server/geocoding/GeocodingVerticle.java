package iudx.catalogue.server.geocoding;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.serviceproxy.ServiceBinder;

import static iudx.catalogue.server.util.Constants.*;

/**
 * The Geocoding Verticle.
 * <h1>A Geocoding Verticle</h1>
 * <p>
 * The Geocoding Verticle implementation in the the IUDX Catalogue Server exposes the
 * {@link iudx.catalogue.server.geocoding.GeocodingService} over the Vert.x Event Bus.
 * </p>
 * 
 * @version 1.0
 * @since 2020-05-31
 */

public class GeocodingVerticle extends AbstractVerticle {


  private static final Logger LOGGER = LogManager.getLogger(GeocodingVerticle.class);
  private GeocodingService Geocoding;
  private String pelias;

  /**
   * This method is used to start the Verticle. It deploys a verticle in a cluster, registers the
   * service with the Event bus against an address, publishes the service with the service discovery
   * interface.
   * 
   * @throws Exception which is a startup exception
   */

  @Override
  public void start() throws Exception {

    pelias = config().getString("pelias");
    Geocoding = new GeocodingServiceImpl(createWebClient(vertx, config()),pelias);

    new ServiceBinder(vertx).setAddress(GEOCODING_SERVICE_ADDRESS)
      .register(GeocodingService.class, Geocoding);
  }

  static WebClient createWebClient(Vertx vertx, JsonObject config) {
    return createWebClient(vertx, config, false);
  }

  /**
   * Helper function to create a WebClient to talk to the geocoding server.
   * @param vertx the vertx instance
   * @param properties the properties field of the verticle
   * @param testing a bool which is used to disable client side ssl checks for testing purposes
   * @return a web client initialized with the relevant client certificate
   */
  static WebClient createWebClient(Vertx vertx, JsonObject config, boolean testing) {
    /* Initialize properties from the config file */
    WebClientOptions webClientOptions = new WebClientOptions();
    if (testing) webClientOptions.setTrustAll(true).setVerifyHost(false);
    return WebClient.create(vertx, webClientOptions);
  }
}
