CREATE TABLE `timestamps` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `workflow_instance_key` BIGINT,
  `transaction_id` VARCHAR(255),
  `exported_time` VARCHAR(255),
  `imported_time` VARCHAR(255),
  `zeebe_time` VARCHAR(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;