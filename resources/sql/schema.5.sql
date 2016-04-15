CREATE TABLE IF NOT EXISTS `global_properties` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `modified` datetime DEFAULT NULL,
 `instance_notification_topic_arn` varchar(255) NOT NULL,
 PRIMARY KEY (`id`)
) CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=0;

alter table global_properties add column duracloud_root_username varchar(255) NOT NULL DEFAULT 'root';
alter table global_properties add column duracloud_root_password varchar(255) NOT NULL DEFAULT 'rpw';
alter table global_properties add column cloud_front_account_id varchar(255) NOT NULL DEFAULT 'cloud front account id';
alter table global_properties add column cloud_front_key_id varchar(255) NOT NULL DEFAULT 'cloud front key id';
alter table global_properties add column cloud_front_key_path varchar(255) NOT NULL DEFAULT 'cloud front key path';

alter table server_details drop column compute_provider_account_id;

drop table if exists server_image;
drop table if exists duracloud_instance;
drop table if exists compute_provider_account;

alter table account_info add column primary_storage_provider_account_id bigint(20) not null;

 update account_info, server_details  set account_info.primary_storage_provider_account_id  = server_details.primary_storage_provider_account_id where account_info.server_details_id = server_details.id;

alter table storage_provider_account add column account_info_id bigint(20) default null;
alter table `storage_provider_account` add constraint foreign key `FK_STORAGE_PROVIDER_ACCOUNT_INFO` (`account_info_id`) references `account_info` (`id`);

update storage_provider_account,account_info set account_info_id = account_info.id where storage_provider_account.server_details_id = account_info.server_details_id ;

alter table storage_provider_account drop foreign key `<server_details_foreign_key_name>`;
drop index `<server_details_foreign_key_name>` on storage_provider_account;
alter table storage_provider_account drop column server_details_id;


alter table account_info drop foreign key `<server_details_foreign_key>`;
drop index `<server_details_foreign_key>` on account_info;
alter table account_info drop column server_details_id;

drop table server_details;

 SET FOREIGN_KEY_CHECKS=1;
