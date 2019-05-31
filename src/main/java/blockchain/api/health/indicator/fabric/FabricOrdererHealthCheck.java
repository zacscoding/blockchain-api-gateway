package blockchain.api.health.indicator.fabric;

import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Fabric orderer health checker
 */
public class FabricOrdererHealthCheck extends FabricHealthCheck {

    private HealthCheckType healthCheckType;

    // option 1) operations service : e.g) http://192.168.10.10:9443
    // https://hyperledger-fabric.readthedocs.io/en/release-1.4/operations_service.html
    private String operationServerUrl;
    private RestTemplate restTemplate;

    public static FabricOrdererHealthCheck fromOperationsArgs(String name, String operationServerUrl) {
        return new FabricOrdererHealthCheck(name, operationServerUrl);
    }

    private FabricOrdererHealthCheck(String name, String operationServerUrl) {
        super(name);

        this.operationServerUrl = operationServerUrl;
        if (StringUtils.hasText(operationServerUrl)) {
            this.healthCheckType = HealthCheckType.OPERATION;
            this.operationServerUrl = operationServerUrl;
            this.restTemplate = getRestTemplate();
        } else {
            throw new IllegalArgumentException("Invalid operationServerUrl " + operationServerUrl);
        }
    }

    @Override
    protected Result check() throws Exception {
        switch (healthCheckType) {
            case OPERATION:
                return handleOperationsHealthCheck();
            default:
                throw new IllegalArgumentException("Invalid health check type : " + healthCheckType);
        }
    }

    private Result handleOperationsHealthCheck() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(operationServerUrl)
            .pathSegment("healthz");
        String requestUrl = builder.toUriString();
        return fabricOperationHealthCheck(requestUrl);
    }

    private enum HealthCheckType {
        OPERATION, UNKNOWN
    }
}
