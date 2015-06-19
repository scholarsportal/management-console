alter table server_image add column `cf_key_path` varchar(255)  NOT NULL DEFAULT "cloud front key path here";
alter table server_image add column `cf_key_id` varchar(255)  NOT NULL DEFAULT "cloud front key id here";
alter table server_image add column `cf_account_id` varchar(255)  NOT NULL DEFAULT "cloud front account id here";
