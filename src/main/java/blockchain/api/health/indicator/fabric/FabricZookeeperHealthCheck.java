package blockchain.api.health.indicator.fabric;

import blockchain.api.health.indicator.AbstractHealthCheck;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * Fabric Zookeeper health check
 */
@Slf4j
@Getter
public class FabricZookeeperHealthCheck extends AbstractHealthCheck {

    private static final String COMMAND = "srvr \n";
    private static final String DEFAULT_ADDRESS = "localhost";
    private static final Integer DEFAULT_PORT = 2181;
    private static final Integer DEFAULT_SOCKET_TIMEOUT = 3000;
    private static final String DEFAULT_MODE = null;

    private String address;
    private int port;
    private boolean secure; // TODO : add ssl
    // after checking health, one of {"null", "standalone", "leader", "follower" } mode is updated
    private String mode;

    public static FabricZookeeperHealthCheck of(String name) {
        return of(name, DEFAULT_ADDRESS);
    }

    public static FabricZookeeperHealthCheck of(String name, String address) {
        return of(name, address, DEFAULT_PORT);
    }

    public static FabricZookeeperHealthCheck of(String name, String address, Integer port) {
        return of(name, address, port, false);
    }

    public static FabricZookeeperHealthCheck of(String name, String address, Integer port, boolean secure) {
        return of(name, address, port, secure, DEFAULT_MODE);
    }

    public static FabricZookeeperHealthCheck of(String name, String address, Integer port
        , boolean secure, String mode) {

        return new FabricZookeeperHealthCheck(name, address, port, secure, mode);
    }

    private FabricZookeeperHealthCheck(String name, String address, Integer port, boolean secure, String mode) {
        super(name);

        this.address = Objects.requireNonNull(address, "address must be not null");
        this.port = Objects.requireNonNull(port, "port must be not null");
        this.secure = secure;
        this.mode = mode;
    }

    /**
     * Reference : org.apache.zookeeper.client.FourLetterWordMain
     * https://github.com/apache/zookeeper/blob/master/zookeeper-server/src/main/java/org/apache/zookeeper/client/FourLetterWordMain.java
     */
    @Override
    protected Result check() throws Exception {
        Socket sock = null;

        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            if (secure) {
                throw new UnsupportedOperationException("Not supported SSL yet");
            } else {
                sock = new Socket();
                sock.connect(socketAddress, DEFAULT_SOCKET_TIMEOUT);
            }

            sock.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
            OutputStream os = sock.getOutputStream();
            os.write(COMMAND.getBytes());
            os.flush();

            String output = IOUtils.toString(
                sock.getInputStream(), StandardCharsets.UTF_8
            );
            this.mode = extractZookeeperNodeMode(output);

            if (logger.isTraceEnabled()) {
                logger.trace("[Zookeeper health check] {}-{} >> {}", name, address, mode);
            }

            if (!secure) {
                sock.shutdownOutput();
            }

            return Result.healthy();
        } catch (Exception e) {
            e.printStackTrace();
            this.mode = null;
            return Result.unhealthy("Cannot connect to " + name);
        } finally {
            sock.close();
        }
    }

    /**
     * Getting zookeeper's mode
     *
     * @return null if unhealthy, otherwise one of {"standalone", "leader", "follower" , "unknown"}
     */
    public String getMode() {
        return mode;
    }

    private String extractZookeeperNodeMode(String result) {
        final String prefix = "Mode: ";

        int modeIdx = result.indexOf(prefix);

        if (modeIdx < 0) {
            logger.warn("Failed to find Mode:. result : {}", result);
            return "unknown";
        }

        int startIdx = modeIdx + prefix.length();
        int lastIdx = result.length() - 1;

        for (int i = modeIdx; i < result.length(); i++) {
            if (result.charAt(i) == '\n') {
                lastIdx = i;
                break;
            }
        }

        return result.substring(startIdx, lastIdx);
    }
}
