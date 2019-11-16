CREATE DATABASE  IF NOT EXISTS `testing` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `testing`;
-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: localhost    Database: testing
-- ------------------------------------------------------
-- Server version	8.0.18

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
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `QA` varchar(45) DEFAULT NULL,
  `Time` timestamp NULL DEFAULT NULL,
  `Station` varchar(45) DEFAULT NULL,
  `Controller_Serial_Number` char(20) DEFAULT NULL,
  `Note` longtext,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES (74,'a1956','2019-11-13 19:38:20','Receiving Station','30N004000401',''),(75,'a1956','2019-11-13 19:38:34','Receiving Station','30N004000391',''),(76,'a1956','2019-11-13 19:38:43','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(77,'a1956','2019-11-13 19:38:51','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(78,'a1956','2019-11-13 20:03:36','Firmware Update Station','30N004000401','Assembly Station to Firmware Update Station'),(79,'a1956','2019-11-13 20:04:04','Added to waiting list burn in','30N004000401','Added to burn in system.'),(80,'a1956','2019-11-13 20:04:06','Added to waiting list burn in','30N004000391','Added to burn in system.'),(81,'a1956','2019-11-13 20:04:12','Burn In Station','30N004000401','Starting Burn In Process'),(82,'a1956','2019-11-13 20:04:12','Burn In Station','30N004000391','Starting Burn In Process'),(83,'a1956','2019-11-13 20:20:52','Result Station','30N004000401','Marked Passed!'),(84,'a1956','2019-11-13 20:21:00','Result Station','30N004000391','Marked Fail: '),(85,'a1956','2019-11-13 21:26:45','Receiving Station','30N004000401',''),(86,'a1956','2019-11-13 21:26:50','Receiving Station','30N004000391',''),(87,'a1956','2019-11-13 21:26:54','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(88,'a1956','2019-11-13 21:26:59','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(89,'a1956','2019-11-13 21:27:03','Firmware Update Station','30N004000401','Assembly Station to Firmware Update Station'),(90,'a1956','2019-11-13 21:27:08','Added to waiting list burn in','30N004000401','Added to burn in system.'),(91,'a1956','2019-11-13 21:27:09','Added to waiting list burn in','30N004000391','Added to burn in system.'),(92,'a1956','2019-11-13 21:27:11','Burn In Station','30N004000401','Starting Burn In Process'),(93,'a1956','2019-11-13 21:27:11','Burn In Station','30N004000391','Starting Burn In Process'),(94,'a1956','2019-11-13 21:27:21','Result Station','30N004000401','Marked Passed!'),(95,'a1956','2019-11-13 21:27:30','Result Station','30N004000391','Marked Fail: '),(96,'a1956','2019-11-13 21:35:17','Receiving Station','30N004000401',''),(97,'a1956','2019-11-13 21:35:19','Receiving Station','30N004000391',''),(98,'a1956','2019-11-13 21:35:24','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(99,'a1956','2019-11-13 21:35:24','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(100,'a1956','2019-11-13 21:35:31','Firmware Update Station','30N004000401','Assembly Station to Firmware Update Station'),(101,'a1956','2019-11-13 21:35:35','Added to waiting list burn in','30N004000401','Added to burn in system.'),(102,'a1956','2019-11-13 21:35:36','Added to waiting list burn in','30N004000391','Added to burn in system.'),(103,'a1956','2019-11-13 21:35:38','Burn In Station','30N004000401','Starting Burn In Process'),(104,'a1956','2019-11-13 21:35:38','Burn In Station','30N004000391','Starting Burn In Process'),(105,'a1956','2019-11-13 21:35:45','Result Station','30N004000401','Marked Passed!'),(106,'a1956','2019-11-13 21:36:29','Result Station','30N004000391','Marked Fail: No Power On'),(107,'a1956','2019-11-13 21:39:39','Repair Station','30N004000391','REPAIR: Take out cable and do abc'),(108,'a1956','2019-11-13 22:09:48','Packing Station','30N004000401','Package is ready!'),(109,'a1956','2019-11-13 22:10:49','Firmware Update Station','30N004000391','Assembly Station to Firmware Update Station'),(110,'a1956','2019-11-13 22:11:04','Added to waiting list burn in','30N004000391','Added to burn in system.'),(111,'a1956','2019-11-13 22:11:06','Burn In Station','30N004000391','Starting Burn In Process'),(112,'a1956','2019-11-13 22:11:20','Result Station','30N004000391','Marked Fail: No Power'),(113,'a1956','2019-11-13 22:13:44','Packing Station','30N004000391','Package is ready!'),(114,'a1956','2019-11-14 00:00:23','Shipping Station','30N004000391',''),(115,'a1956','2019-11-14 00:00:46','Shipping Station','30N004000401',''),(116,'a1956','2019-11-14 00:18:13','Receiving Station','30N004000401',''),(117,'a1956','2019-11-14 00:18:15','Receiving Station','30N004000391',''),(118,'a1956','2019-11-14 00:18:27','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(119,'a1956','2019-11-14 00:18:40','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(120,'a1956','2019-11-14 00:19:06','Firmware Update Station','30N004000401','Assembly Station to Firmware Update Station'),(121,'a1956','2019-11-14 00:19:27','Added to waiting list burn in','30N004000401','Added to burn in system.'),(122,'a1956','2019-11-14 00:19:29','Added to waiting list burn in','30N004000391','Added to burn in system.'),(123,'a1956','2019-11-14 00:19:33','Burn In Station','30N004000401','Starting Burn In Process'),(124,'a1956','2019-11-14 00:19:33','Burn In Station','30N004000391','Starting Burn In Process'),(125,'a1956','2019-11-14 00:20:01','Result Station','30N004000401','Marked Passed!'),(126,'a1956','2019-11-14 00:20:47','Result Station','30N004000391','Marked Fail: Power no on'),(127,'a1956','2019-11-14 00:21:37','Repair Station','30N004000391','REPAIR: kfkjdhf '),(128,'a1956','2019-11-14 00:22:02','Added to waiting list burn in','30N004000401','Added to burn in system.'),(129,'a1956','2019-11-14 00:22:06','Burn In Station','30N004000401','Starting Burn In Process'),(130,'a1956','2019-11-14 00:22:15','Result Station','30N004000401','Marked Passed!'),(131,'a1956','2019-11-14 00:22:22','Packing Station','30N004000401','Package is ready!'),(132,'a1956','2019-11-14 00:23:04','Shipping Station','30N004000401',''),(133,'a1956','2019-11-14 22:35:20','Receiving Station','30N004000401',''),(134,'a1956','2019-11-14 22:35:24','Receiving Station','30N004000391',''),(135,'a1956','2019-11-14 22:35:43','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(136,'a1956','2019-11-14 22:35:47','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(137,'a1956','2019-11-14 22:35:54','Firmware Update Station','30N004000401','Assembly Station to Firmware Update Station'),(138,'a1956','2019-11-14 22:36:04','Added to waiting list burn in','30N004000401','Added to burn in system.'),(139,'a1956','2019-11-14 22:36:23','Added to waiting list burn in','30N004000391','Added to burn in system.'),(140,'a1956','2019-11-14 22:36:28','Burn In Station','30N004000401','Starting Burn In Process'),(141,'a1956','2019-11-14 22:36:28','Burn In Station','30N004000391','Starting Burn In Process'),(142,'a1956','2019-11-14 22:36:43','Firmware Update Station','30N004000401','Rework: FromBurn In Station to Firmware Update Station'),(143,'a1956','2019-11-14 22:37:50','Added to waiting list burn in','30N004000401','Added to burn in system.'),(144,'a1956','2019-11-14 22:42:30','Burn In Station','30N004000401','Starting Burn In Process');
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-14 16:45:37
