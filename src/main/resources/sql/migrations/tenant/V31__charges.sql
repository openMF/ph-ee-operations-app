DROP TABLE IF EXISTS `m_charge`;

CREATE TABLE `m_charge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` varchar(100) DEFAULT NULL,
    `amount` decimal(19,6) NOT NULL,
    `currency_code` varchar(3) NOT NULL,
    `charge_applies_to` varchar(100) NOT NULL,
    `charge_calculation_enum` SMALLINT NOT NULL,
    `is_active` tinyint NOT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    `min_cap` decimal(19,6) NOT NULL,
    `max_cap` decimal(19,6) NOT NULL,
    `fee_frequency` SMALLINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;