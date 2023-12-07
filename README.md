## Purpose

Payment Hub Enterprise Edition's Operations App is meant for backoffice use monitoring and interacting with real-time payment systems.

## Environment-specific configuration

The following configuration variables can be used to configure the application:

```yaml
tenants:
  connections:
    - name: <tenant_name>
      schema_server: <postgres_host>
      schema_name: <schema_name>
      schema_server_port: 5432
      schema_username: <username>
      schema_password: <password>
      driver_class: "org.postgresql.Driver"
      jdbcProtocol: "jdbc"
      jdbcSubProtocol: postgresql
      auto_update: true
      pool_initial_size: 5
      pool_validation_interval: 30000
      pool_remove_abandoned: 1
      pool_remove_abandoned_timeout: 60
      pool_log_abandoned: 1
      pool_abandon_when_percentage_full: 50
      pool_test_on_borrow: 1
      pool_max_active: 40
      pool_min_idle: 20
      pool_max_idle: 10
      pool_suspect_timeout: 60
      pool_time_between_eviction_runs_millis: 34000
      pool_min_evictable_idle_time_millis: 60000
      deadlock_max_retries: 0
      deadlock_max_retry_interval: 1
      schema_connection_parameters:
channel-connector:
  url: <url>
  transfer-path: /channel/transfer
baasflow:
  events:
    kafka:
      msk: true
      brokers: "<broker endpoints, comma separated>
      glue-registry-name: "<registry_name>"
      glue-schema-name: "<schema_name>"
    channels:
      audit:
        topic: "audit"
                                                                                                                                                                                                                                                                                  
frontend:
  callback-url-base: "<frontend_callback_url>"
```
All yaml properties can be overridden using environment variables. For example, to override the `baasflow.events.kafka.brokers` property, set the environment variable `BAASFLOW_EVENTS_KAFKA_BROKERS` to the desired value.


## Initial database migration
The application's first start will require a database migration to be performed. This can be done by running the application with the SPRING_PROFILES_ACTIVE environment variable set to "migrate". The application will then perform the migration and exit. After the migration has been performed, the application can be started normally.
