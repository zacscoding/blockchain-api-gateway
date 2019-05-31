package blockchain.api.health.indicator;

import com.codahale.metrics.health.HealthCheck;
import lombok.Getter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Health indicator
 */
@Getter
public abstract class AbstractHealthCheck extends HealthCheck {

    private static Object LOCK = new Object();

    // unique name
    protected String name;
    private RestTemplate restTemplate;

    public AbstractHealthCheck(String name) {
        this.name = name;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            synchronized (LOCK) {
                if (restTemplate == null) {
                    this.restTemplate = createRestTemplate();
                }
            }
        }

        return restTemplate;
    }

    protected RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);

        return new RestTemplate(httpRequestFactory);
    }
}
