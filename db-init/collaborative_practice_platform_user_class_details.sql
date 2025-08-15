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
-- Table structure for table `user_class_details`
--

DROP TABLE IF EXISTS `user_class_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_class_details` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` int NOT NULL COMMENT '班级ID，关联 class 表的 id',
  `student_id` int NOT NULL COMMENT '学生ID，关联 user 表的 id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`,`student_id`) USING BTREE COMMENT '班级学生唯一索引',
  KEY `idx_class_id` (`class_id`) USING BTREE COMMENT '班级ID索引',
  KEY `idx_student_id` (`student_id`) USING BTREE COMMENT '学生ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户班级关联详情表 (学生-班级多对多关系)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_class_details`
--

LOCK TABLES `user_class_details` WRITE;
/*!40000 ALTER TABLE `user_class_details` DISABLE KEYS */;
INSERT INTO `user_class_details` VALUES (2,1,2,'2025-08-07 04:39:33'),(3,1,3,'2025-08-07 04:39:33'),(4,2,2,'2025-08-07 04:39:33'),(5,2,4,'2025-08-07 04:39:33'),(6,2,6,'2025-08-07 04:39:33'),(7,2,7,'2025-08-07 04:39:33'),(8,3,3,'2025-08-07 04:39:33'),(9,3,5,'2025-08-07 04:39:33'),(10,4,1,'2025-08-07 04:39:33'),(11,4,4,'2025-08-07 04:39:33'),(12,4,5,'2025-08-07 04:39:33'),(13,4,6,'2025-08-07 04:39:33'),(14,4,8,'2025-08-07 04:39:33'),(16,3,1,'2025-08-10 19:51:07');
/*!40000 ALTER TABLE `user_class_details` ENABLE KEYS */;
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
