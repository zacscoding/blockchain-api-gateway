package blockchain.api.exception;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
public class GatewayUncheckedException extends RuntimeException {

    public GatewayUncheckedException(String message) {
        super(message);
    }

    public GatewayUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayUncheckedException(Throwable cause) {
        super(cause);
    }
}
