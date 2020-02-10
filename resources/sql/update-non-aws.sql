ALTER TABLE `duracloud_mill`
ADD COLUMN `audit_queue_type` varchar(255) DEFAULT 'AWS',
ADD COLUMN `rabbitmq_host` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_port` int(11) NOT NULL,
ADD COLUMN `rabbitmq_vhost` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_exchange` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_username` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_password` varchar(255) NOT NULL,
ADD COLUMN `aws_access_key` varchar(255) DEFAULT NULL,
ADD COLUMN `aws_secret_key` varchar(255) DEFAULT NULL,
ADD COLUMN `swift_endpoint` varchar(255) DEFAULT NULL,
ADD COLUMN `swift_signer_type` varchar(255) DEFAULT NULL,
ADD COLUMN `s3_type` varchar(255) DEFAULT 'AWS';

ALTER TABLE `global_properties`
ADD COLUMN `notifier_type` varchar(255) DEFAULT 'AWS',
ADD COLUMN `rabbitmq_host` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_port` int(11) NOT NULL,
ADD COLUMN `rabbitmq_vhost` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_exchange` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_username` varchar(255) NOT NULL,
ADD COLUMN `rabbitmq_password` varchar(255) NOT NULL;
