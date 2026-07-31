DROP TABLE IF EXISTS `sales_inquiry_detail`;

CREATE TABLE `sales_inquiry_detail` (
  `sTransNox` char(12) NOT NULL,
  `nEntryNox` smallint(6) NOT NULL,
  `nPriority` smallint(6) DEFAULT NULL,
  `sCategrCd` char(4) DEFAULT NULL,
  `sStockIDx` varchar(12) DEFAULT NULL,
  `sModelIDx` varchar(9) DEFAULT NULL,
  `sColorIDx` varchar(7) DEFAULT NULL,
  `sVrntIDxx` varchar(5) DEFAULT NULL,
  `nSelPrice` decimal(14,4) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sTransNox`,`nEntryNox`),
  KEY `sStockIDx` (`sStockIDx`),
  KEY `sModelIDx` (`sModelIDx`),
  KEY `sColorIDx` (`sColorIDx`),
  KEY `sVrntIDxx` (`sVrntIDxx`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
