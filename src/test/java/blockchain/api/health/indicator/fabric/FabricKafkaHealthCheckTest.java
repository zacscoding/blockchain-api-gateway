package blockchain.api.health.indicator.fabric;

import blockchain.api.common.FabricZookeeperMockServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO : check kafka running...
 */
@Ignore
public class FabricKafkaHealthCheckTest {

    /////////////////////////////////////
    // temp for dev
    @Test
    public void temp3() throws Exception {
        FabricZookeeperMockServer server = new FabricZookeeperMockServer(
            null, socket -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    int read = input.read();
                    if (read == IOUtils.EOF) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        server.start();

        FabricKafkaHealthCheck healthCheck = new FabricKafkaHealthCheck(
            "fabric-kafka", "localhost:" + server.getPort(), 1
        );

        System.out.println(
            healthCheck.check().isHealthy()
        );
    }

    @Test
    @Ignore
    public void temp2() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("10.0.164.32:2181", 3000, event -> {
        });

        Integer brokerId = 2;
        String path = "/brokers/ids/" + brokerId;

        boolean isRunning = false;

        try {
            byte[] data = zooKeeper.getData(path, null, null);
            isRunning = true;
        } catch (KeeperException e) {
            isRunning = false;
        } finally {
            zooKeeper.close();
        }

        System.out.println(
            "Check kafka broker : " + brokerId + " >> running : " + isRunning
        );
    }

    @Test
    public void temp1() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("10.0.164.32:2181", 3000, null);
        States state = zooKeeper.getState();
        System.out.println(
            state.isConnected()
        );

        System.out.println(
            state.isAlive()
        );

        byte[] data = zooKeeper.getData("/brokers/ids/1", null, null);
        System.out.println(
            "data :: " + new String(data)
        );

        List<String> children = zooKeeper.getChildren("/brokers/ids", null);
        System.out.println(children.size());
        for (String s : children) {
            System.out.println("Children :: " + s);
        }
    }
}
