package blockchain.api.health.indicator.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import blockchain.api.common.FabricZookeeperMockServer;
import blockchain.api.common.StubSocketServer;
import com.codahale.metrics.health.HealthCheck.Result;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Zookeeper health check console test
 */
@Slf4j
public class FabricZookeeperHealthCheckTest {

    FabricZookeeperHealthCheck healthCheck;
    StubSocketServer server;

    @After
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testCheckAndThenReturnSuccess() throws Exception {
        // given
        FabricZookeeperMockServer server = new FabricZookeeperMockServer("Mode: leader");
        server.start();
        healthCheck = FabricZookeeperHealthCheck.of(
            "zookeeper1", "127.0.0.1", server.getPort()
        );

        // when
        Result check = healthCheck.check();

        // then
        assertThat(check.isHealthy()).isTrue();
        assertThat(healthCheck.getMode()).isEqualTo("leader");
    }

    @Test
    @Ignore
    public void checkByConsole() throws Exception {
        healthCheck = FabricZookeeperHealthCheck.of(
            "zookeeper1", "10.0.164.32"
        );

        System.out.printf("Check result : %s | mode : %s", healthCheck.check(), healthCheck.getMode());
    }
}
