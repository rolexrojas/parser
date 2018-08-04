-- -----------------------------------------------------
-- Table `logparserdb`.`access_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `access_log` ;

CREATE TABLE IF NOT EXISTS `access_log` (
  `id_access_log` INT NOT NULL AUTO_INCREMENT,
  `date_event` TIMESTAMP NULL,
  `ip_address` VARCHAR(45) NULL,
  `request_method` VARCHAR(45) NULL,
  `status_code` VARCHAR(45) NULL,
  `user_agent` VARCHAR(255) NULL,
  PRIMARY KEY (`id_access_log`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `logparserdb`.`blocked_ips`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `blocked_ips` ;

CREATE TABLE IF NOT EXISTS `blocked_ips` (
  `id_blocked_access_log` INT NOT NULL AUTO_INCREMENT,
  `ip_address` VARCHAR(45) NULL,
  `commentary` VARCHAR(255) NULL,
  PRIMARY KEY (`id_blocked_access_log`))
ENGINE = InnoDB;
