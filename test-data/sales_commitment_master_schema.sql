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
/*Table structure for table `sales_commitment_master` */

DROP TABLE IF EXISTS `sales_commitment_master`;

CREATE TABLE `sales_commitment_master` (
  `sTransNox` char(12) NOT NULL,
  `dTransact` date DEFAULT NULL,
  `sClientID` char(12) DEFAULT NULL,
  `sSourceCd` varchar(4) DEFAULT NULL,
  `sSourceNo` varchar(12) DEFAULT NULL,
  `cIssuerxx` char(1) DEFAULT NULL,
  `sIssuerID` varchar(12) DEFAULT NULL,
  `cPayModex` char(1) DEFAULT NULL,
  `sTermCode` varchar(7) DEFAULT NULL,
  `sPONumber` varchar(32) DEFAULT NULL,
  `sATDNumbr` varchar(32) DEFAULT NULL,
  `nTranTotl` decimal(12,2) DEFAULT NULL,
  `nVATRatex` decimal(5,2) DEFAULT NULL,
  `nVATSales` decimal(12,4) DEFAULT NULL,
  `nVATAmtxx` decimal(12,4) DEFAULT NULL,
  `nVATExmpt` decimal(12,4) DEFAULT NULL,
  `nWTaxRate` decimal(5,2) DEFAULT NULL,
  `nTWithHld` decimal(12,4) DEFAULT NULL,
  `dAppliedx` date DEFAULT NULL,
  `dApproved` date DEFAULT NULL,
  `dDueDatex` date DEFAULT NULL,
  `sRemarksx` varchar(256) DEFAULT NULL,
  `nSalesAmt` decimal(12,4) DEFAULT NULL,
  `nEntryNox` tinyint(4) DEFAULT NULL,
  `cTranStat` char(1) DEFAULT NULL,
  `sModified` varchar(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sTransNox`),
  KEY `ClientID` (`sClientID`),
  KEY `IssuerID` (`sIssuerID`),
  KEY `PayMode` (`cPayModex`),
  KEY `PONumber` (`sPONumber`),
  KEY `ATDNumber` (`sATDNumbr`),
  KEY `DateApproved` (`dApproved`),
  KEY `TransState` (`cTranStat`),
  KEY `SourceCDNo` (`sSourceCd`,`sSourceNo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
