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
  `Note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES (1,'a1956','2019-11-12 17:21:29','Receiving Station','30N004000401',''),(2,'a1956','2019-11-12 17:21:47','Receiving Station','30N004000391',''),(3,'a1956','2019-11-12 17:22:20','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(4,'a1956','2019-11-12 17:23:20','Added to waiting list burn in','30N004000401','Get ready to burn in.'),(5,'a1956','2019-11-12 17:23:25','Burn In Station','30N004000401','Added to burn in station'),(6,'a1956','2019-11-12 17:26:32','Assembly Station','30N004000401','Rework: FromBurn In Station to Assembly Station'),(7,'a1956','2019-11-12 17:35:42','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(8,'a1956','2019-11-12 17:36:06','Added to waiting list burn in','30N004000401','Get ready to burn in.'),(9,'a1956','2019-11-12 17:45:15','Assembly Station','30N004000401','Rework: FromRe_Work Processing to Assembly Station'),(10,'a1956','2019-11-12 17:45:44','Added to waiting list burn in','30N004000401','Get ready to burn in.'),(11,'a1956','2019-11-12 18:34:58','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(12,'a1956','2019-11-12 18:35:15','Added to waiting list burn in','30N004000401','Added to burn in system.'),(13,'a1956','2019-11-12 18:48:10','Added to waiting list burn in','30N004000391','Added to burn in system.'),(14,'a1956','2019-11-12 18:49:48','Added to waiting list burn in','30N004000391','Added to burn in system.'),(15,'a1956','2019-11-12 18:50:41','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(16,'a1956','2019-11-12 18:50:45','Added to waiting list burn in','30N004000391','Added to burn in system.'),(17,'a1956','2019-11-12 18:51:06','Burn In Station','30N004000401','Starting Burn In Process'),(18,'a1956','2019-11-12 18:51:06','Burn In Station','30N004000391','Starting Burn In Process'),(19,'a1956','2019-11-13 00:31:50','Result Station','30N004000401','Marked Passed!'),(20,'a1956','2019-11-13 00:33:03','Result Station','30N004000391','Marked Passed!'),(21,'a1956','2019-11-13 00:37:21','Receiving Station','30N004000401',''),(22,'a1956','2019-11-13 00:37:26','Receiving Station','30N004000391',''),(23,'a1956','2019-11-13 00:37:58','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(24,'a1956','2019-11-13 00:38:35','Added to waiting list burn in','30N004000401','Added to burn in system.'),(25,'a1956','2019-11-13 00:39:08','Burn In Station','30N004000401','Starting Burn In Process'),(26,'a1956','2019-11-13 00:39:21','Result Station','30N004000401','Marked Passed!'),(27,'a1956','2019-11-13 00:47:04','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(28,'a1956','2019-11-13 00:47:09','Added to waiting list burn in','30N004000401','Added to burn in system.'),(29,'a1956','2019-11-13 00:47:11','Burn In Station','30N004000401','Starting Burn In Process'),(30,'a1956','2019-11-13 00:47:19','Result Station','30N004000401','Marked Passed!'),(31,'a1956','2019-11-13 00:47:59','Assembly Station','30N004000401','Receiving Station to Assembly Station'),(32,'a1956','2019-11-13 00:48:01','Assembly Station','30N004000391','Receiving Station to Assembly Station'),(33,'a1956','2019-11-13 00:48:05','Added to waiting list burn in','30N004000401','Added to burn in system.'),(34,'a1956','2019-11-13 00:48:06','Added to waiting list burn in','30N004000391','Added to burn in system.'),(35,'a1956','2019-11-13 00:48:08','Burn In Station','30N004000401','Starting Burn In Process'),(36,'a1956','2019-11-13 00:48:08','Burn In Station','30N004000391','Starting Burn In Process'),(37,'a1956','2019-11-13 00:48:13','Result Station','30N004000401','Marked Passed!'),(38,'a1956','2019-11-13 00:48:14','Result Station','30N004000391','Marked Passed!');
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

-- Dump completed on 2019-11-12 16:52:14
