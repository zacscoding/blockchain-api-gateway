package blockchain.api.health.indicator.fabric;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Objects;
import org.hyperledger.fabric.protos.gossip.GossipGrpc;
import org.hyperledger.fabric.protos.gossip.GossipGrpc.GossipBlockingStub;
import org.hyperledger.fabric.protos.gossip.Message.Empty;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Fabric peer health checker
 */
public class FabricPeerHealthCheck extends FabricHealthCheck {

    private HealthCheckType healthCheckType;

    // option1 ) grpc - proto/gossip/message.proto
    private String grpcAddress;
    private Integer grpcPort;

    // option 2) operations service : e.g) http://192.168.10.10:9443
    // https://hyperledger-fabric.readthedocs.io/en/release-1.4/operations_service.html
    private String operationServerUrl;

    // initialized if use gossip protocol
    private ManagedChannel channel;
    private GossipBlockingStub stub;
    // initialized if use operations service
    private RestTemplate restTemplate;


    /**
     * Create fabric peer health checker using grpc
     */
    public static FabricPeerHealthCheck fromGrpcArgs(String name, String grpcAddress, Integer grpcPort) {
        return new FabricPeerHealthCheck(name, grpcAddress, grpcPort, null);
    }

    /**
     * Create fabric peer health checker using operations
     */
    public static FabricPeerHealthCheck fromOperationsArgs(String name, String operationServerUrl) {
        return new FabricPeerHealthCheck(name, null, null, operationServerUrl);
    }

    private FabricPeerHealthCheck(String name, String grpcAddress, Integer grpcPort, String operationServerUrl) {
        super(name);

        this.grpcAddress = grpcAddress;
        this.operationServerUrl = operationServerUrl;

        if (StringUtils.hasText(grpcAddress)) {
            healthCheckType = HealthCheckType.GRPC;
            this.grpcPort = Objects.requireNonNull(grpcPort, "grpcPort must be not null");
            this.channel = ManagedChannelBuilder.forAddress(grpcAddress, grpcPort)
                .usePlaintext()
                .build();
            this.stub = GossipGrpc.newBlockingStub(channel);
        } else if (StringUtils.hasText(operationServerUrl)) {
            healthCheckType = HealthCheckType.OPERATION;
            this.operationServerUrl = Objects.requireNonNull(operationServerUrl, "operationServerUrl must be not null");
            this.restTemplate = getRestTemplate();
        } else {
            throw new IllegalArgumentException("Must have text grpcAddress or operationAddress");
        }
    }

    @Override
    protected Result check() throws Exception {
        switch (healthCheckType) {
            case GRPC:
                return handleGrpcHealthCheck();
            case OPERATION:
                return handleOperationsHealthCheck();
            default:
                throw new IllegalArgumentException("Invalid health check type : " + healthCheckType);
        }
    }

    // for
    public boolean isGrpcCheckType() {
        return HealthCheckType.GRPC.equals(healthCheckType);
    }

    public boolean isOperationsCheckType() {
        return HealthCheckType.OPERATION.equals(healthCheckType);
    }

    private Result handleGrpcHealthCheck() {
        try {
            Empty ping = stub.ping(Empty.newBuilder().build());
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e.getMessage());
        }
    }

    private Result handleOperationsHealthCheck() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(operationServerUrl)
            .pathSegment("healthz");

        String requestUrl = builder.toUriString();

        return fabricOperationHealthCheck(requestUrl);
    }

    private enum HealthCheckType {
        GRPC, OPERATION, UNKNOWN
    }
}
