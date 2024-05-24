CREATE TABLE Timestamps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_instance_key BIGINT,
    transaction_id VARCHAR(255),
    exported_time VARCHAR(255),
    imported_time VARCHAR(255),
    zeebe_time VARCHAR(255)
);