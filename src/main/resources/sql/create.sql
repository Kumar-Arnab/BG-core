-- create user
DROP USER if exists 'bguser'@'localhost' ;

CREATE USER 'bguser'@'localhost' IDENTIFIED BY 'bguser';

GRANT ALL PRIVILEGES ON * . * TO 'bguser'@'localhost';


-- create schema
CREATE DATABASE  IF NOT EXISTS `bg_schema`;
USE `bg_schema`;

-- Table structure for table `chapter`
DROP TABLE IF EXISTS `chapters`;

CREATE TABLE `chapters` (
  `chapter_number` INT NOT NULL AUTO_INCREMENT,
  `bg_ch_id` VARCHAR(10) UNIQUE NOT NULL,
  `no_of_verses` INT NOT NULL,
  `hnd_name` VARCHAR(500) NOT NULL,
  `eng_translation` VARCHAR(500) NOT NULL,
  `eng_transliteration` VARCHAR(500) NOT NULL,
  `hnd_meaning` VARCHAR(1000) NOT NULL,
  `eng_meaning` VARCHAR(1000) NOT NULL,
  `eng_summary` TEXT NOT NULL,
  `hnd_summary` TEXT NOT NULL,
  PRIMARY KEY (`chapter_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16;


-- Table structure for table `sloka`
DROP TABLE IF EXISTS `sloka`;

CREATE TABLE `sloka` (
  `sloka_id` VARCHAR(25) NOT NULL,
  `bg_ch_id` VARCHAR(10) NOT NULL,
  `verse_no` INT NOT NULL,
  `slok_org` VARCHAR(500) NOT NULL,
  `eng_transliteration` VARCHAR(500) NOT NULL,
  `hnd_meaning` TEXT NOT NULL,
  `eng_meaning` TEXT NOT NULL,
  `wtwm` TEXT NOT NULL,
  PRIMARY KEY (`sloka_id`),
  CONSTRAINT `sloka_fk_1` FOREIGN KEY (`bg_ch_id`) REFERENCES `chapters` (`bg_ch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16;