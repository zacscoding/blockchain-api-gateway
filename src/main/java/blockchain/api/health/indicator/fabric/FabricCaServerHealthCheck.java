package blockchain.api.health.indicator.fabric;

import java.util.Objects;

/**
 * Fabric ca server health checker
 */
public class FabricCaServerHealthCheck extends FabricHealthCheck {

    private String caServerAddress;
    private Integer caServerPort;

    private FabricCaServerHealthCheck(String name, String caServerAddress, Integer caServerPort) {
        super(name);

        this.caServerAddress = Objects.requireNonNull(caServerAddress, "caServerAddress must be not null");
        this.caServerPort = Objects.requireNonNull(caServerPort, "caServerPort must be not null");
    }

    @Override
    protected Result check() throws Exception {
        return fabricCaApiInfo(caServerAddress, caServerPort);
    }
}
