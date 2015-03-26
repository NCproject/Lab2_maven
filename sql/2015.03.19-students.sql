/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50538
Source Host           : localhost:3306
Source Database       : students

Target Server Type    : MYSQL
Target Server Version : 50538
File Encoding         : 65001

Date: 2015-03-18 23:56:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `faculties`
-- ----------------------------
DROP TABLE IF EXISTS `faculties`;
CREATE TABLE `faculties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of faculties
-- ----------------------------
INSERT INTO `faculties` VALUES ('1', 'ЭлИТ');
INSERT INTO `faculties` VALUES ('2', 'ТЕСЕТ');

-- ----------------------------
-- Table structure for `groups`
-- ----------------------------
DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `faculty_id` int(11) NOT NULL,
  `number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `faculty_id` (`faculty_id`),
  CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`faculty_id`) REFERENCES `faculties` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of groups
-- ----------------------------
INSERT INTO `groups` VALUES ('1', '1', 'Ек-1');
INSERT INTO `groups` VALUES ('2', '1', 'Ел-2');
INSERT INTO `groups` VALUES ('3', '2', 'Те-1');
INSERT INTO `groups` VALUES ('4', '2', 'Тп-2');

-- ----------------------------
-- Table structure for `students`
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `enrolled` varchar(10) NOT NULL,
  `date_add` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES ('1', '1', 'ААА', 'БББ', '', '2015-03-17 23:13:11');
INSERT INTO `students` VALUES ('2', '2', 'РРОЛ', 'ОЛР', '', '2015-03-17 23:13:21');
INSERT INTO `students` VALUES ('3', '3', 'РОЛ', 'ОЛД', '', '2015-03-17 23:13:27');
INSERT INTO `students` VALUES ('4', '4', 'ОЛР', 'ДЛОЛД', '', '2015-03-17 23:13:32');

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'user', '111');
