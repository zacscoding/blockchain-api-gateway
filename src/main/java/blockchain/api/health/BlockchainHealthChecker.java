package blockchain.api.health;

import blockchain.api.health.indicator.AbstractHealthCheck;

/**
 *
 */
public interface BlockchainHealthChecker {

    /**
     * Start health checker
     */
    void start();

    /**
     * Stop health checker
     */
    void stop();

    /**
     * Check running or not
     */
    void isRunning();

    /**
     * Register health check
     *
     * @return true : success to register, otherwise false
     */
    boolean registerBlockchainComponent(AbstractHealthCheck healthCheck);

    /**
     * Remove health check
     */
    void removeBlockchainComponent(String name);

    /**
     * Checking blockchain component with name or not
     */
    boolean isCheckingBlockchainComponent(String name);

    /**
     * Adds health check events listener.
     * after event occur, all listeners will receive events
     */
    void addListener(HealthCheckListener listener);
}
