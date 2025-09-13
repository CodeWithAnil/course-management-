package com.nt.course_service_lms.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;

/**
 * Configuration class for setting up the application's database connection using
 * AWS Secrets Manager to retrieve sensitive credentials.
 * <p>
 * This configuration is activated only when the 'local' and 'test' profiles are not active.
 */
@Configuration
@Profile("!local & !test")
public class DatabaseConfig {
    /**
     * Spring {@link Environment} object used to access environment-specific properties.
     * <p>
     * This is used to retrieve AWS Secrets Manager configuration values such as
     * secret name and region from application properties.
     * </p>
     */
    @Autowired
    private Environment environment;

    /**
     * Creates and configures a {@link DataSource} bean using credentials retrieved
     * from AWS Secrets Manager.
     *
     * @return a configured Postgres SQL {@link DataSource} instance
     * @throws Exception if an error occurs while retrieving or parsing the secret
     */
    @Bean
    public DataSource getDataSource() throws Exception {
        JsonNode secret = getSecretDetails();

        String username = secret.get("username").asText();
        String password = secret.get("password").asText();
        String hostname = secret.get("hostname").asText();
        String dbName = secret.get("db_name").asText();
        String schema = secret.get("schema_name").asText();

        String jdbcUrl = String.format("jdbc:postgresql://%s:5432/%s?currentSchema=%s", hostname, dbName, schema);

        return DataSourceBuilder.create()
                .username(username)
                .password(password)
                .url(jdbcUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    /**
     * Retrieves secret details from AWS Secrets Manager and parses them into a JSON object.
     *
     * @return {@link JsonNode} containing secret details.
     */
    private JsonNode getSecretDetails() {
        String secretName = environment.getProperty("aws.secretsmanager.secretName");
        String region = environment.getProperty("aws.secretsmanager.region");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);

        String secretString = response.secretString();

        try {
            return new ObjectMapper().readTree(secretString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AWS secret JSON", e);
        }
    }
}
