/*
SQLyog Ultimate v8.55 
MySQL - 5.7.44-log : Database - gcasys_dbf
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `sales_giveaways_master` */

DROP TABLE IF EXISTS `sales_giveaways_master`;

CREATE TABLE `sales_giveaways_master` (
  `sGAWayCde` varchar(12) NOT NULL,
  `sGAWayDsc` varchar(64) DEFAULT NULL,
  `sIndstCdx` varchar(2) DEFAULT NULL,
  `sCategrCd` varchar(7) DEFAULT NULL,
  `sRemarksx` varchar(256) DEFAULT NULL,
  `dFromDate` date DEFAULT NULL,
  `dThruDate` date DEFAULT NULL,
  `cTranStat` char(1) DEFAULT NULL,
  `nEntryNox` smallint(5) DEFAULT NULL,
  `sEntryByx` varchar(32) DEFAULT NULL,
  `dEntryDte` datetime DEFAULT NULL,
  `sModified` varchar(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sGAWayCde`),
  KEY `IndustryCode` (`sIndstCdx`),
  KEY `CategoryCode` (`sCategrCd`),
  KEY `GAWayPeriod` (`dFromDate`,`dThruDate`),
  KEY `TransState` (`cTranStat`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
