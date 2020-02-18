ALTER TABLE `duracloud_mill`
ADD COLUMN `audit_queue_type` varchar(255) DEFAULT 'AWS',
ADD COLUMN `rabbitmq_host` varchar(255),
ADD COLUMN `rabbitmq_port` int(11),
ADD COLUMN `rabbitmq_vhost` varchar(255),
ADD COLUMN `rabbitmq_exchange` varchar(255),
ADD COLUMN `rabbitmq_username` varchar(255),
ADD COLUMN `rabbitmq_password` varchar(255);

ALTER TABLE `global_properties`
ADD COLUMN `notifier_type` varchar(255) DEFAULT 'AWS',
ADD COLUMN `rabbitmq_host` varchar(255),
ADD COLUMN `rabbitmq_port` int(11),
ADD COLUMN `rabbitmq_vhost` varchar(255),
ADD COLUMN `rabbitmq_exchange` varchar(255),
ADD COLUMN `rabbitmq_username` varchar(255),
ADD COLUMN `rabbitmq_password` varchar(255);
