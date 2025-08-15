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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个性签名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称/头衔',
  `group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属组/部门',
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型 (student, teacher, admin)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` int DEFAULT NULL COMMENT '创建人ID',
  `update_user` int DEFAULT NULL COMMENT '更新人ID',
  `status` int DEFAULT '1' COMMENT '状态 (0-禁用, 1-启用)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`) USING BTREE COMMENT '邮箱唯一索引',
  KEY `idx_create_user` (`create_user`) USING BTREE COMMENT '创建人索引',
  KEY `idx_user_type` (`user_type`) USING BTREE COMMENT '用户类型索引'
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Teacher Li','hashed_password_123','li.teacher@school.edu.cn','0571-88888888','Zhejiang Hangzhou','Dedicated to teaching and student growth','Female','https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png','Senior Lecturer','School of Computer Science - Department of Software Engineering','teacher','2025-08-07 03:24:58','2025-08-07 03:24:58',1,1,1),(2,'Alice Johnson','hashed_password_456','alice.johnson@student.edu.cn','13800138001','Dormitory A, Room 101','Love learning!','Female','https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(3,'Bob Smith','hashed_password_789','bob.smith@student.edu.cn','13800138002','Dormitory B, Room 205','Future software engineer','Male','https://gw.alipayobjects.com/zos/rmsportal/ComBAopevLwENQdKWiIn.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(4,'Charlie Brown','hashed_password_012','charlie.brown@student.edu.cn','13800138003','Off-campus Apartment','Aspiring data scientist','Male','https://gw.alipayobjects.com/zos/rmsportal/nxkuOJlFJuAUhzlMTCEe.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(5,'Diana Prince','hashed_password_345','diana.prince@student.edu.cn','13800138004','Dormitory C, Room 310','Keen on AI','Female','https://gw.alipayobjects.com/zos/rmsportal/kZzEzemZyKLKFsojXItE.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(6,'Ethan Hunt','hashed_password_678','ethan.hunt@student.edu.cn','13800138005','Dormitory A, Room 112','Into cybersecurity','Male','https://gw.alipayobjects.com/zos/rmsportal/sfjbOqnsXXJgNCjCzDBL.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(7,'Fiona Apple','hashed_password_901','fiona.apple@student.edu.cn','13800138006','Off-campus Apartment','Passionate about frontend','Female','https://gw.alipayobjects.com/zos/rmsportal/ComBAopevLwENQdKWiIn.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(8,'George Lucas','hashed_password_234','george.lucas@student.edu.cn','13800138007','Dormitory B, Room 218','Dreams of creating the next big framework','Male','https://gw.alipayobjects.com/zos/rmsportal/nxkuOJlFJuAUhzlMTCEe.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(9,'Helen Keller','hashed_password_567','helen.keller@student.edu.cn','13800138008','Dormitory C, Room 305','Inspired by accessibility tech','Female','https://gw.alipayobjects.com/zos/rmsportal/kZzEzemZyKLKFsojXItE.png',NULL,NULL,'student','2025-08-07 04:39:23','2025-08-07 04:39:23',1,1,1),(10,'123','123123','123','123123123','12','123123','male','https://img.freepik.com/premium-vector/female-teacher-cute-woman-stands-with-pointer-book-school-learning-concept-teacher-s-day_335402-428.jpg','123','123','student','2025-08-14 14:03:18','2025-08-14 14:03:18',NULL,NULL,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
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
