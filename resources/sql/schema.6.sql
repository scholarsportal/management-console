SET FOREIGN_KEY_CHECKS=0;

alter table global_properties add column duracloud_root_username varchar(255) NOT NULL DEFAULT 'root';
alter table global_properties add column duracloud_root_word varchar(255) NOT NULL DEFAULT 'rpw';

alter table server_details drop column compute_provider_account_id;

drop table if exists server_image;
drop table if exists duracloud_instance;
drop table if exists compute_provider_account;


SET FOREIGN_KEY_CHECKS=1;


