package blockchain.api.health.indicator.fabric;

import blockchain.api.health.indicator.AbstractHealthCheck;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Fabric abstract health checker
 */
public abstract class FabricHealthCheck extends AbstractHealthCheck {

    public FabricHealthCheck(String name) {
        super(name);
    }

    /**
     * Request to fabric operations service's /healthz
     *
     * @param requestUrl : operations server url with /healthz path
     */
    protected Result fabricOperationHealthCheck(String requestUrl) {
        try {
            Map result = getRestTemplate().getForObject(requestUrl, Map.class);

            if (!"OK".equals(result.get("status"))) {
                System.out.println("## TEMP FOR CHECK : " + result);
                throw new Exception("/healthz result : " + result);
            }

            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e.getMessage());
        }
    }

    /**
     * Request to fabric ca server with /api/v1/cainfo path
     *
     * TODO : tls
     *
     * @param caServerAddress : ca serverAddress
     * @param caServerPort    : ca server port
     */
    protected Result fabricCaApiInfo(String caServerAddress, Integer caServerPort) {
        String httpUrl = "http://" + caServerAddress + ":" + caServerPort;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(httpUrl)
            .pathSegment("api", "v1", "cainfo");

        try {
            Map result = getRestTemplate().getForObject(builder.toUriString(), Map.class);
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e.getMessage());
        }
    }
}
