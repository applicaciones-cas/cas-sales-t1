DROP TABLE IF EXISTS `transaction_attachment`;

CREATE TABLE `transaction_attachment` (
  `sTransNox` varchar(14) NOT NULL,
  `sSourceCd` varchar(4) NOT NULL,
  `sSourceNo` varchar(12) NOT NULL,
  `sDocuType` varchar(4) DEFAULT NULL,
  `sDescript` varchar(128) DEFAULT NULL,
  `sScanndID` varchar(4) DEFAULT NULL,
  `sFileName` varchar(128) NOT NULL,
  `sMD5Hashx` varchar(100) DEFAULT NULL,
  `sImagePth` varchar(128) DEFAULT NULL,
  `dEntryDte` datetime DEFAULT NULL,
  `sEntryByx` varchar(32) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `cSendStat` char(1) DEFAULT '0',
  `sModified` varchar(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sTransNox`),
  KEY `sSourceCd` (`sSourceCd`,`sSourceNo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
