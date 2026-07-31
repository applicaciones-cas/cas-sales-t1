DROP TABLE IF EXISTS `sales_inquiry_sources`;

CREATE TABLE `sales_inquiry_sources` (
  `sSourceID` char(3) NOT NULL,
  `sDescript` char(32) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT NULL,
  `sModified` char(32) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sSourceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
