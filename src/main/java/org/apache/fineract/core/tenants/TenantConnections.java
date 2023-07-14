package org.apache.fineract.core.tenants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("tenants")
@Data
public class TenantConnections {

    List<TenantConnectionProperties> connections;

}
