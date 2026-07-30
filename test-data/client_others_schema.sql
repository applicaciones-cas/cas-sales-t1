DROP TABLE IF EXISTS `client_others`;

CREATE TABLE `client_others` (
  `sClientID` varchar(12) NOT NULL,
  `sLTOIDxxx` varchar(32) DEFAULT NULL,
  `sHouseNox` varchar(5) DEFAULT NULL,
  `sAddressx` varchar(128) DEFAULT NULL,
  `sBrgyIDxx` varchar(7) DEFAULT NULL,
  `sTownIDxx` varchar(4) DEFAULT NULL,
  `sFacebook` varchar(256) DEFAULT NULL,
  `dModified` datetime DEFAULT NULL,
  `dTimeStmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sClientID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
