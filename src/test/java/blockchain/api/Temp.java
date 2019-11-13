package blockchain.api;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;

import blockchain.api.entity.EthereumNode;
import blockchain.api.exception.GatewayUncheckedException;

/**
 * Tests for proxy web3j
 *
 * @GitHub : https://github.com/zacscoding
 */
public class Temp {

    @Test
    public void runTest() throws Exception {
        List<EthereumNode> nodes = Arrays.asList(
                EthereumNode.builder()
                            .blockTime(5000L)
                            .name("node1")
                            .rpcUrl("http://192.168.79.130:8545")
                            .networkId("1")
                            .build()
                , EthereumNode.builder()
                              .blockTime(5000L)
                              .name("node2")
                              .rpcUrl("http://192.168.79.130:8546")
                              .networkId("1")
                              .build()
                , EthereumNode.builder()
                              .blockTime(5000L)
                              .name("node3")
                              .rpcUrl("http://192.168.79.130:8547")
                              .networkId("1")
                              .build()

        );

        ProxyWeb3j proxyWeb3j = new ProxyWeb3j(nodes);
        TimeUnit.SECONDS.sleep(1L); // wait for first health check

        for (int i = 0; i < 10; i++) {
            System.out.printf("Check [#%d] : %s\n", i, proxyWeb3j.getActiveWeb3j());
            TimeUnit.SECONDS.sleep(1L);
        }
    }

    public static class ProxyWeb3j {

        private final ReentrantReadWriteLock lock;
        private List<EthereumNode> nodes;
        private Map<String, EthereumNode> healthyNodes;
        private Map<String, EthereumNode> unhealthyNodes;

        public ProxyWeb3j(List<EthereumNode> nodes) {
            this.nodes = new ArrayList<>(nodes);
            this.healthyNodes = new HashMap<>();
            this.unhealthyNodes = new HashMap<>();
            this.lock = new ReentrantReadWriteLock();
            startHealthCheck();
        }

        public Web3j getActiveWeb3j() {
            return (Web3j) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                    , new Class[] { Web3jService.class }
                    , (proxy, method, args) -> {
                        Optional<EthereumNode> activeNode = getActiveNode();
                        if (!activeNode.isPresent()) {
                            throw new GatewayUncheckedException("no available ethereum nodes");
                        }

                        return method.invoke(activeNode.get(), args);
                    });
        }

        private Optional<EthereumNode> getActiveNode() {
            try {
                lock.readLock().lock();
                if (healthyNodes.isEmpty()) {
                    return Optional.empty();
                }

                List<EthereumNode> nodes = new ArrayList<>(healthyNodes.values());
                Collections.shuffle(nodes);
                return Optional.of(nodes.get(0));
            } finally {
                lock.readLock().unlock();
            }
        }

        private void startHealthCheck() {
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.println(">> Start health check <<");

                        StringBuilder healthyNodeValues = new StringBuilder();
                        StringBuilder unHealthyNodeValue = new StringBuilder();

                        for (EthereumNode node : nodes) {
                            boolean isHealthy = false;
                            try {
                                Web3j web3j = node.getWeb3j();
                                web3j.ethSyncing().send();
                                isHealthy = true;
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                            }

                            try {
                                lock.writeLock().lock();
                                if (isHealthy) {
                                    unhealthyNodes.remove(node.getName());
                                    healthyNodes.put(node.getName(), node);
                                    healthyNodeValues.append(node.getName()).append(" ");
                                } else {
                                    healthyNodes.remove(node.getName());
                                    unhealthyNodes.put(node.getName(), node);
                                    unHealthyNodeValue.append(node.getName()).append(" ");
                                }
                            } finally {
                                lock.writeLock().unlock();
                            }
                        }

                        System.out.println(">> healthy nodes : " + healthyNodeValues.toString());
                        System.out.println(">> unhealthy nodes : " + unHealthyNodeValue.toString());

                        TimeUnit.SECONDS.sleep(10L);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }
}