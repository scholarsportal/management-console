CREATE TABLE IF NOT EXISTS 'global_properties' (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `modified` datetime DEFAULT NULL,
 `instance_notification_topic_arn` varchar(255) NOT NULL,
 PRIMARY KEY (`id`)
) CHARSET=utf8;
