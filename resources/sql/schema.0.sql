-- MySQL dump 10.13  Distrib 5.5.38, for osx10.6 (i386)
--
-- Host: amadb.cc3ipdilwekc.us-east-1.rds.amazonaws.com    Database: ama
-- ------------------------------------------------------
-- Server version	5.5.40-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_cluster`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_cluster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `cluster_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_info`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `acct_name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `org_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subdomain` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `account_cluster_id` bigint(20) DEFAULT NULL,
  `server_details_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2d12xe3psrhkkv6ia9s8fef9q` (`account_cluster_id`),
  KEY `FK_81nuyb23r7k65chj0lyohu09j` (`server_details_id`),
  CONSTRAINT `FK_2d12xe3psrhkkv6ia9s8fef9q` FOREIGN KEY (`account_cluster_id`) REFERENCES `account_cluster` (`id`),
  CONSTRAINT `FK_81nuyb23r7k65chj0lyohu09j` FOREIGN KEY (`server_details_id`) REFERENCES `server_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1020 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_rights`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_rights` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `account_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pqeqe896mkrol3d5fx83xkcwq` (`account_id`),
  KEY `FK_rse1vjlmaucfng31iaw1fj7iu` (`user_id`),
  CONSTRAINT `FK_pqeqe896mkrol3d5fx83xkcwq` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`id`),
  CONSTRAINT `FK_rse1vjlmaucfng31iaw1fj7iu` FOREIGN KEY (`user_id`) REFERENCES `duracloud_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1074 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_rights_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_rights_role` (
  `account_rights_id` bigint(20) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FK_91wx1gbltwqx2ma0w10c798td` (`account_rights_id`),
  CONSTRAINT `FK_91wx1gbltwqx2ma0w10c798td` FOREIGN KEY (`account_rights_id`) REFERENCES `account_rights` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `compute_provider_account`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compute_provider_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `audit_queue` varchar(255) DEFAULT NULL,
  `elastic_ip` varchar(255) DEFAULT NULL,
  `keypair` varchar(255) DEFAULT NULL,
  `provider_type` varchar(255) DEFAULT NULL,
  `security_group` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1018 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `duracloud_group`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `duracloud_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7tt58lodlxiqwjhvbyi9ytlyp` (`account_id`),
  CONSTRAINT `FK_7tt58lodlxiqwjhvbyi9ytlyp` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `duracloud_instance`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `duracloud_instance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `host_name` varchar(255) DEFAULT NULL,
  `initialized` tinyint(1) NOT NULL,
  `provider_instance_id` varchar(255) DEFAULT NULL,
  `account_id` bigint(20) NOT NULL,
  `image_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_64axdgak3svvybdj78m2macpo` (`account_id`),
  KEY `FK_64axdgak3svvybdj78m2macpo` (`account_id`),
  KEY `FK_lkytdgr5uo49cyd5c8urhx9xn` (`image_id`),
  CONSTRAINT `FK_64axdgak3svvybdj78m2macpo` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`id`),
  CONSTRAINT `FK_lkytdgr5uo49cyd5c8urhx9xn` FOREIGN KEY (`image_id`) REFERENCES `server_image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=422 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `duracloud_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `duracloud_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `account_non_expired` tinyint(1) NOT NULL,
  `account_non_locked` tinyint(1) NOT NULL,
  `credentials_non_expired` tinyint(1) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `root` tinyint(1) NOT NULL,
  `security_answer` varchar(255) DEFAULT NULL,
  `security_question` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1105 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_user` (
  `group_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`group_id`,`user_id`),
  KEY `FK_ns02np32pqhrbm8cwpifjerp9` (`user_id`),
  KEY `FK_dx4jv6mpv63ufnjl3a7pec1vo` (`group_id`),
  CONSTRAINT `FK_dx4jv6mpv63ufnjl3a7pec1vo` FOREIGN KEY (`group_id`) REFERENCES `duracloud_group` (`id`),
  CONSTRAINT `FK_ns02np32pqhrbm8cwpifjerp9` FOREIGN KEY (`user_id`) REFERENCES `duracloud_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `server_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `compute_provider_account_id` bigint(20) NOT NULL,
  `primary_storage_provider_account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_45iqbwrm8q6qblm7960l8n83b` (`compute_provider_account_id`),
  UNIQUE KEY `UK_7gwng60y7kx7wojyu2jkiesg5` (`primary_storage_provider_account_id`),
  KEY `FK_45iqbwrm8q6qblm7960l8n83b` (`compute_provider_account_id`),
  KEY `FK_7gwng60y7kx7wojyu2jkiesg5` (`primary_storage_provider_account_id`),
  CONSTRAINT `FK_45iqbwrm8q6qblm7960l8n83b` FOREIGN KEY (`compute_provider_account_id`) REFERENCES `compute_provider_account` (`id`),
  CONSTRAINT `FK_7gwng60y7kx7wojyu2jkiesg5` FOREIGN KEY (`primary_storage_provider_account_id`) REFERENCES `storage_provider_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1018 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `server_image`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `dc_root_password` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `latest` tinyint(1) NOT NULL,
  `provider_image_id` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storage_provider_account`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_provider_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `provider_type` varchar(255) DEFAULT NULL,
  `rrs` tinyint(1) NOT NULL,
  `server_details_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hvnlrvdob3e30hby6lpqw3kxw` (`server_details_id`),
  CONSTRAINT `FK_hvnlrvdob3e30hby6lpqw3kxw` FOREIGN KEY (`server_details_id`) REFERENCES `server_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1025 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_invitation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_invitation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `account_dep` varchar(255) DEFAULT NULL,
  `account_name` varchar(255) DEFAULT NULL,
  `account_org` varchar(255) DEFAULT NULL,
  `account_subdomain` varchar(255) DEFAULT NULL,
  `admin_username` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `redemption_code` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `account_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_nlwoj3r09ksyu15sp9ob5y8gx` (`account_id`),
  CONSTRAINT `FK_nlwoj3r09ksyu15sp9ob5y8gx` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-05-08 13:59:26
