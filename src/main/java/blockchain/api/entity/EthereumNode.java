package blockchain.api.entity;

import static java.util.Objects.requireNonNull;

import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.IpcService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Async;

import blockchain.api.enums.BlockchainType;
import blockchain.api.exception.GatewayUncheckedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumNode extends BlockchainNode {

    private String name;
    private String rpcUrl;
    private String[] group;
    private Web3j web3j;
    private long blockTime;

    @Builder
    public EthereumNode(String networkId, String name, String rpcUrl, long blockTime) {
        super(requireNonNull(networkId, "networkId"));
        this.name = requireNonNull(name, "name");
        this.rpcUrl = requireNonNull(rpcUrl, "rpcUrl");
        this.group = new String[] { networkId };
        this.blockTime = blockTime;
        this.web3j = createWeb3j();
    }

    @Override
    public String[] getGroup() {
        return group;
    }

    @Override
    public BlockchainType getBlockchainType() {
        return BlockchainType.ETHEREUM;
    }

    private Web3j createWeb3j() {
        try {
            if (!StringUtils.hasText(rpcUrl)) {
                throw new GatewayUncheckedException("invalid rpc url");
            }

            // 1) websocket
            if (rpcUrl.startsWith("ws://")) {
                return createWeb3jFromWebsocketService();
            }

            // 2) http
            if (rpcUrl.startsWith("http://") || rpcUrl.startsWith("https://")) {
                return createWeb3jFromHttpService();
            }

            // 3) ipc
            return createWeb3jFromIpcService();
        } catch (Exception e) {
            throw new GatewayUncheckedException(e);
        }
    }

    private Web3j createWeb3jFromIpcService() {
        boolean isWindow = System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
        IpcService ipcService = isWindow ? new WindowsIpcService(rpcUrl) : new UnixIpcService(rpcUrl);
        return Web3j.build(ipcService, blockTime, Async.defaultExecutorService());
    }

    private Web3j createWeb3jFromHttpService() {
        return Web3j.build(new HttpService(rpcUrl), blockTime, Async.defaultExecutorService());
    }

    private Web3j createWeb3jFromWebsocketService() throws Exception {
        WebSocketService webSocketService = new WebSocketService(rpcUrl, false);
        webSocketService.connect();
        return Web3j.build(webSocketService, blockTime, Async.defaultExecutorService());
    }
}
