package blockchain.api.health.indicator.fabric;

import java.util.Objects;
import org.apache.zookeeper.ZooKeeper;

/**
 * Fabric kafka health check
 */
public class FabricKafkaHealthCheck extends FabricHealthCheck {

    private String zookeeperConnectionString;
    private Integer brokerId;

    public FabricKafkaHealthCheck(String name, String zookeeperConnectionString, Integer brokerId) {
        super(name);
        this.zookeeperConnectionString = Objects.requireNonNull(zookeeperConnectionString,
            "zookeeperConnectionString must be not null");
        this.brokerId = Objects.requireNonNull(brokerId, "brokerId must be not null");
    }

    @Override
    protected Result check() throws Exception {
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(zookeeperConnectionString, 5000, event -> {
            });

            String path = "/brokers/ids/" + brokerId;

            // not used data
            // data sample : {"listener_security_protocol_map":{"PLAINTEXT":"PLAINTEXT"},"endpoints":["PLAINTEXT://kafka0.testnet.com:9092"],"jmx_port":-1,"host":"kafka0.testnet.com","timestamp":"1559260816238","port":9092,"version":4}
            byte[] data = zooKeeper.getData(path, null, null);

            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e.getMessage());
        } finally {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        }
    }
}
