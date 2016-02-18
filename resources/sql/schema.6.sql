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

SET FOREIGN_KEY_CHECKS=1;


