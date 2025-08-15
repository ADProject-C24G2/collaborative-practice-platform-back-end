-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: collaborative_practice_platform
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `access_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问方式: byLink 或 byName',
  `token` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '访问令牌（仅 byLink 时生成）',
  `access_expiration` datetime DEFAULT NULL COMMENT '访问过期时间',
  `access_available` datetime DEFAULT NULL COMMENT '访问生效时间',
  `max_members` int NOT NULL DEFAULT '100' COMMENT '最大成员数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` int NOT NULL COMMENT '创建人ID',
  `update_user` int NOT NULL COMMENT '更新人ID',
  `teacher_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`) USING BTREE COMMENT '唯一索引：token',
  KEY `idx_create_user` (`create_user`) USING BTREE COMMENT '创建人索引',
  KEY `idx_access_expiration` (`access_expiration`) USING BTREE COMMENT '过期时间索引'
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (1,'C24','This is an art class.','byLink','0387b2b2-0b1d-4790-82de-af98da90da2e','2025-09-10 23:59:59','2025-08-06 00:00:00',43,'2025-08-06 22:09:55','2025-08-07 03:53:00',1,1,1),(2,'C24','This is an art class.','byLink','a5b46836-fb1b-454b-8b8a-c3ddbd1a009c','2025-09-10 23:59:59','2025-08-06 00:00:00',43,'2025-08-06 22:49:21','2025-08-07 03:53:02',1,1,1),(3,'SA59','Hello Word','byName',NULL,'2025-09-13 23:59:59','2025-08-04 00:00:00',21,'2025-08-06 23:43:22','2025-08-07 03:53:04',1,1,1),(4,'Python','Let\'s play python','byLink','02f4fe40-ee5c-433c-9dd2-94946d6e2b79','2025-09-11 23:59:59','2025-08-13 00:00:00',23,'2025-08-07 04:03:48','2025-08-07 04:03:48',1,1,1),(5,'Java','study java','byLink','9832f496-489d-4956-9276-4609795366a2','2025-09-12 23:59:59','2025-08-13 00:00:00',12,'2025-08-07 04:49:41','2025-08-07 04:49:41',1,1,1),(6,'SA61','Learn java.','byLink','06b5caf8-c7de-4f1a-a9a6-0e486011f4ed','2025-09-26 23:59:59','2025-08-15 00:00:00',45,'2025-08-07 10:26:53','2025-08-07 10:26:53',1,1,1);
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-15 14:00:35
