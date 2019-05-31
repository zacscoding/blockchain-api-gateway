package blockchain.api.health;

import blockchain.api.health.indicator.AbstractHealthCheck;
import com.codahale.metrics.health.HealthCheck;

/**
 * Health check event listener
 */
public interface HealthCheckListener {

    /**
     * if returned true about name, then this listener will receive events.
     * otherwise skip to receive.
     */
    boolean filter(String name);

    /**
     * After success to register, this method will called
     */
    void onRegister(AbstractHealthCheck healthCheck);

    /**
     * After success to remove, this method will called
     */
    void onRemoved(AbstractHealthCheck healthCheck);

    /**
     * After changed health status, this method will called
     */
    void onStatusChanged(AbstractHealthCheck healthCheck, HealthCheck.Result currentStatus);
}
