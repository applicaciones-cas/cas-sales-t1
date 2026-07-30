DROP TABLE IF EXISTS `barangay`;

CREATE TABLE `barangay` (
  `sBrgyIDxx` varchar(7) NOT NULL,
  `sBrgyName` varchar(30) DEFAULT NULL,
  `sTownIDxx` varchar(4) DEFAULT NULL,
  `cHasRoute` char(1) DEFAULT NULL,
  `cBlackLst` char(1) DEFAULT '0',
  `cRecdStat` char(1) DEFAULT NULL,
  `sModified` varchar(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sBrgyIDxx`),
  KEY `sTownIDxx` (`sTownIDxx`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
