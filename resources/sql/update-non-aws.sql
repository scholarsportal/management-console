ALTER TABLE `duracloud_mill`
ADD COLUMN `audit_queue_type` varchar(255) DEFAULT 'AWS',
ADD COLUMN `rabbitmq_host` varchar(255),
ADD COLUMN `rabbitmq_port` int(11),
ADD COLUMN `rabbitmq_vhost` varchar(255),
ADD COLUMN `rabbitmq_exchange` varchar(255),
ADD COLUMN `rabbitmq_username` varchar(255),
ADD COLUMN `rabbitmq_password` varchar(255);

ALTER TABLE `global_properties`
CHANGE `instance_notification_topic_arn` `instance_notification_topic_arn` VARCHAR(255) NULL,
ADD COLUMN `notifier_type` varchar(255) DEFAULT 'AWS' NOT NULL,
ADD COLUMN `rabbitmq_host` varchar(255),
ADD COLUMN `rabbitmq_port` int(11),
ADD COLUMN `rabbitmq_vhost` varchar(255),
ADD COLUMN `rabbitmq_exchange` varchar(255),
ADD COLUMN `rabbitmq_username` varchar(255),
ADD COLUMN `rabbitmq_password` varchar(255);
