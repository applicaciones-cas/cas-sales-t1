DROP TABLE IF EXISTS `province`;

CREATE TABLE `province` (
  `sProvIDxx` char(4) NOT NULL,
  `sDescript` char(32) NOT NULL,
  `sRegionID` varchar(2) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `sModified` char(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sProvIDxx`),
  KEY `cRecdStat` (`cRecdStat`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
