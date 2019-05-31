package blockchain.api.health.indicator.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class FabricPeerHealthCheckTest {

    @Test
    public void test_healthCheckTypeAfterCreated() {
        assertThat(
            FabricPeerHealthCheck.fromGrpcArgs("name", "192.168.100.100", 7051).isGrpcCheckType()
        ).isTrue();

        assertThat(
            FabricPeerHealthCheck.fromGrpcArgs("name", "192.168.100.100", 7051).isOperationsCheckType()
        ).isFalse();

        assertThat(
            FabricPeerHealthCheck.fromOperationsArgs("name", "http://192.168.100.100:9443").isOperationsCheckType()
        ).isTrue();

        assertThat(
            FabricPeerHealthCheck.fromOperationsArgs("name", "http://192.168.100.100:9443").isGrpcCheckType()
        ).isFalse();
    }


    @Test
    //@Ignore
    public void healthCheckForConsole() throws Exception {
        // 1) grpc
        FabricPeerHealthCheck check = FabricPeerHealthCheck.fromGrpcArgs(
            "peer0", "10.0.164.32", 7050
        );
        System.out.println("## gRpc result : " + check.check());

        // 2) operations server
        FabricPeerHealthCheck check2 = FabricPeerHealthCheck.fromOperationsArgs(
            "peer1", "http://10.0.164.32:9443"
        );

        System.out.println("## operations server result : " + check2.check());
    }
}
