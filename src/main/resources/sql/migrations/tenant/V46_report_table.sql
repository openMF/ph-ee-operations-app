CREATE TABLE REPORTREQUEST (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               report_name VARCHAR(255),
                               report_type VARCHAR(255),
                               report_subType VARCHAR(255),
                               report_category VARCHAR(255),
                               description VARCHAR(255),
                               report_sql TEXT
);

CREATE TABLE report_parameters (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   report_request_id BIGINT,
                                   parameter_key VARCHAR(255),
                                   parameter_value VARCHAR(255),
                                   FOREIGN KEY (report_request_id) REFERENCES REPORTREQUEST(id)
);

