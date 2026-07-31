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

/*Table structure for table `category` */



DROP TABLE IF EXISTS `category`;



CREATE TABLE `category` (
  `sCategrCd` varchar(7) NOT NULL,
  `sDescript` varchar(128) DEFAULT NULL,
  `sDescCode` varchar(8) DEFAULT NULL,
  `sParentID` varchar(7) DEFAULT NULL,
  `sIndstCdx` varchar(2) NOT NULL,
  `sInvTypCd` varchar(4) DEFAULT NULL,
  `cSerialze` char(1) DEFAULT NULL,
  `cLevelxxx` char(1) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT NULL,
  `sModified` varchar(10) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sCategrCd`,`sIndstCdx`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

