package blockchain.api.health.indicator;

import com.codahale.metrics.health.HealthCheck;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Health indicator
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public abstract class AbstractHealthCheck extends HealthCheck {

    // unique name
    protected String name;
}
