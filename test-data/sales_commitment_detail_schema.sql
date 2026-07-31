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
/*Table structure for table `sales_commitment_detail` */

DROP TABLE IF EXISTS `sales_commitment_detail`;

CREATE TABLE `sales_commitment_detail` (
  `sTransNox` char(12) NOT NULL,
  `nEntryNox` tinyint(3) NOT NULL,
  `sStockIDx` varchar(12) NOT NULL,
  `nQuantity` smallint(5) DEFAULT NULL,
  `nUnitPrce` decimal(12,4) DEFAULT NULL,
  `nQtyRelsd` smallint(5) DEFAULT NULL,
  `cReversex` char(1) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sTransNox`,`nEntryNox`,`sStockIDx`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
