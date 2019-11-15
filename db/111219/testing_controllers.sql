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
-- Table structure for table `controllers`
--

DROP TABLE IF EXISTS `controllers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `controllers` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Model` varchar(45) DEFAULT NULL,
  `Lot_ID` int(6) DEFAULT NULL,
  `Serial_Number` char(20) DEFAULT NULL,
  `Current_Station` varchar(45) DEFAULT NULL,
  `Receiving_Time` timestamp NULL DEFAULT NULL,
  `Assembly_Time` timestamp NULL DEFAULT NULL,
  `Re_Assembly_Time` timestamp NULL DEFAULT NULL,
  `Burn_In_Start` timestamp NULL DEFAULT NULL,
  `Burn_In_End` timestamp NULL DEFAULT NULL,
  `Packing_Time` timestamp NULL DEFAULT NULL,
  `Shipping_Time` timestamp NULL DEFAULT NULL,
  `Burn_In_Result` varchar(45) DEFAULT NULL,
  `Firmware_Update_Time` timestamp NULL DEFAULT NULL,
  `Repair_Time` timestamp NULL DEFAULT NULL,
  `Is_Received` tinyint(4) DEFAULT '0',
  `Is_Assembly_Done` tinyint(4) DEFAULT '0',
  `Is_Firmware_Updated` tinyint(4) DEFAULT '0',
  `Is_Re_Assembly_Done` tinyint(4) DEFAULT '0',
  `Is_Burn_In_Processing` tinyint(4) DEFAULT '0',
  `Is_Burn_In_Done` tinyint(4) DEFAULT '0',
  `Is_Packing_Done` tinyint(4) DEFAULT '0',
  `Is_Shipping_Done` tinyint(4) DEFAULT '0',
  `Is_Repaired_Done` tinyint(4) DEFAULT '0',
  `Is_ReWork` tinyint(4) DEFAULT '0',
  `Is_Passed` tinyint(4) DEFAULT '0',
  `Symptoms_Fail` varchar(255) DEFAULT NULL,
  `Re_work_count` int(2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `controllers`
--

LOCK TABLES `controllers` WRITE;
/*!40000 ALTER TABLE `controllers` DISABLE KEYS */;
INSERT INTO `controllers` VALUES (8,'SMC-P04W R16',111219,'30N004000401','Result Station','2019-11-13 00:37:21','2019-11-13 00:47:59',NULL,'2019-11-13 00:48:08','2019-11-13 00:48:13',NULL,NULL,'PASS',NULL,NULL,1,1,0,0,1,1,0,0,0,0,1,'No Trouble Found.',0),(9,'SMC-P04W R16',111219,'30N004000391','Result Station','2019-11-13 00:37:25','2019-11-13 00:48:01',NULL,'2019-11-13 00:48:08','2019-11-13 00:48:14',NULL,NULL,'PASS',NULL,NULL,1,1,0,0,1,1,0,0,0,0,1,'No Trouble Found.',0);
/*!40000 ALTER TABLE `controllers` ENABLE KEYS */;
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
