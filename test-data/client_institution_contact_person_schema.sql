DROP TABLE IF EXISTS `client_institution_contact_person`;

CREATE TABLE `client_institution_contact_person` (
  `sContctID` char(12) NOT NULL,
  `sClientID` char(12) NOT NULL,
  `sCategrCd` varchar(7) NOT NULL,
  `cCPrsonID` varchar(12) DEFAULT NULL,
  `sCPerson1` varchar(64) DEFAULT NULL,
  `sCPPosit1` varchar(32) DEFAULT NULL,
  `sJobTitle` varchar(64) DEFAULT NULL,
  `sDeprtmnt` varchar(64) DEFAULT NULL,
  `sRoleIDxx` varchar(8) DEFAULT NULL,
  `sMobileNo` varchar(30) DEFAULT NULL,
  `sTelNoxxx` varchar(30) DEFAULT NULL,
  `sFaxNoxxx` varchar(30) DEFAULT NULL,
  `sEMailAdd` varchar(64) DEFAULT NULL,
  `sAccount1` varchar(64) DEFAULT NULL,
  `sAccount2` varchar(64) DEFAULT NULL,
  `sAccount3` varchar(64) DEFAULT NULL,
  `sRemarksx` varchar(128) DEFAULT NULL,
  `cPayeexxx` char(1) DEFAULT NULL,
  `cPrimaryx` char(1) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `sModified` varchar(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sContctID`),
  UNIQUE KEY `sClientID` (`sClientID`,`sCategrCd`,`cCPrsonID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
