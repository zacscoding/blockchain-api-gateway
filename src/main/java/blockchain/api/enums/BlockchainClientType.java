package blockchain.api.enums;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class BlockchainClientType {

    public enum EthClientType {
        GOETHEREUM, PARITY
    }

    public enum FabricClientType {
        CA_SERVER, CA_CLIENT, ZOOKEEPER, KAFKA, PEER, ORDERER
    }

}
