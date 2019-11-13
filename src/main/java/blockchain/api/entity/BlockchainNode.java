package blockchain.api.entity;

import blockchain.api.enums.BlockchainType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract blockchain component such as eth node, fabric ca server etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BlockchainNode {

    private String networkId;

    public abstract String[] getGroup();

    public abstract BlockchainType getBlockchainType();
}