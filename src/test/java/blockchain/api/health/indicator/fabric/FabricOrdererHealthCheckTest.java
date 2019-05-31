package blockchain.api.health.indicator.fabric;

import org.junit.Test;

/**
 * Fabric orderer health checker
 */
public class FabricOrdererHealthCheckTest {

    @Test
    //@Ignore
    public void healthCheckForConsole() throws Exception {
        // 1) operations server
        FabricOrdererHealthCheck check1 = FabricOrdererHealthCheck.fromOperationsArgs(
            "orderer1", "http://10.0.164.32:8443"
        );

        System.out.println("## operations server result : " + check1.check());
    }
}
