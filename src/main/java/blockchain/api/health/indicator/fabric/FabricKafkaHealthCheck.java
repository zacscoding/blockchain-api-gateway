package blockchain.api.health.indicator.fabric;

import blockchain.api.health.indicator.AbstractHealthCheck;

/**
 * Fabric kafka health check
 */
public class FabricKafkaHealthCheck extends AbstractHealthCheck {

    public FabricKafkaHealthCheck(String name) {
        super(name);
    }

    @Override
    protected Result check() throws Exception {
        return null;
    }
}
