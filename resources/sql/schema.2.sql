alter table account_info drop foreign key `FK_2d12xe3psrhkkv6ia9s8fef9q`;
alter table account_info drop column `account_cluster_id`;
alter table account_info drop column `type`;
drop table `account_cluster`; 
