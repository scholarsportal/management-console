CREATE TABLE `storage_provider_account_properties` (
	  `storage_provider_account_id` bigint(20) NOT NULL AUTO_INCREMENT,
	  `map_value` varchar(255) DEFAULT NULL,
	  `map_key` varchar(255) NOT NULL,
	  PRIMARY KEY (`storage_provider_account_id`,`map_key`),
	  CONSTRAINT `FK_storage_provider_account_id` FOREIGN KEY (`storage_provider_account_id`) REFERENCES `storage_provider_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

alter table duracloud_user add column allowableipaddress_range varchar(255) NULL;

alter table server_image add column iam_role varchar(255) NULL;

alter table storage_provider_account add column `storage_limit` int(11) NOT NULL DEFAULT 1;

alter table compute_provider_account drop column `audit_queue`;
