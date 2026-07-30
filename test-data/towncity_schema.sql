DROP TABLE IF EXISTS `towncity`;

CREATE TABLE `towncity` (
  `sTownIDxx` char(4) NOT NULL,
  `sTownName` char(30) DEFAULT NULL,
  `sZippCode` char(4) DEFAULT NULL,
  `sProvIDxx` char(4) DEFAULT NULL,
  `sMuncplCd` char(6) DEFAULT NULL,
  `cHasRoute` char(1) DEFAULT NULL,
  `cBlackLst` char(1) DEFAULT '0',
  `cRecdStat` char(1) DEFAULT NULL,
  `sModified` char(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sTownIDxx`),
  KEY `sZippCode` (`sZippCode`),
  KEY `sTownName` (`sTownName`),
  KEY `sProvIDxx` (`sProvIDxx`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
