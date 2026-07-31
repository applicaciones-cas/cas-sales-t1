DROP TABLE IF EXISTS `client_social_media`;

CREATE TABLE `client_social_media` (
  `sSocialID` char(12) NOT NULL,
  `sClientID` char(12) DEFAULT NULL,
  `sAccountx` char(64) DEFAULT NULL,
  `sRemarksx` char(64) DEFAULT NULL,
  `cSocialTp` char(1) DEFAULT NULL,
  `cRecdStat` char(1) DEFAULT '1',
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sSocialID`),
  UNIQUE KEY `sClientID` (`sClientID`,`sAccountx`,`cSocialTp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
