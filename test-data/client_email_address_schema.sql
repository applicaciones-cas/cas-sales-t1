DROP TABLE IF EXISTS `client_email_address`;

CREATE TABLE `client_email_address` (
  `sEmailIDx` char(12) NOT NULL,
  `sClientID` char(12) DEFAULT NULL,
  `sEMailAdd` varchar(128) DEFAULT NULL,
  `cOwnerxxx` char(1) DEFAULT NULL,
  `cPrimaryx` char(1) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sEmailIDx`),
  UNIQUE KEY `sClientID` (`sClientID`,`sEMailAdd`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
