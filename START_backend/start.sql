-- MySQL dump 10.15  Distrib 10.0.38-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: start
-- ------------------------------------------------------
-- Server version	10.0.38-MariaDB-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

 SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
 SET AUTOCOMMIT = 0;
 START TRANSACTION;
 SET time_zone = "+00:00";

--
-- Table structure for table `children`
--

DROP TABLE IF EXISTS `children`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `children` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `photo` varchar(255) NOT NULL,
  `name` varbinary(400) NOT NULL,
  `surname` varbinary(400) NOT NULL,
  `patronymic` varbinary(400) NOT NULL,
  `state` varbinary(400) NOT NULL,
  `address` varbinary(400) NOT NULL,
  `gender` varbinary(200) NOT NULL,
  `birth_date` varbinary(200) NOT NULL,
  `latitude` varbinary(200) DEFAULT NULL,
  `longitude` varbinary(200) DEFAULT NULL,
  `diagnosis` varbinary(600) DEFAULT NULL,
  `diagnosis_clinic` varbinary(400) DEFAULT NULL,
  `diagnosis_datetime` varbinary(200) DEFAULT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  `hand` varbinary(400) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `children`
--

LOCK TABLES `children` WRITE;
/*!40000 ALTER TABLE `children` DISABLE KEYS */;
/*!40000 ALTER TABLE `children` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_languages`
--

DROP TABLE IF EXISTS `cms_languages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_languages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_dir` char(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `language_name` char(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `language_dir` (`language_dir`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_languages`
--

LOCK TABLES `cms_languages` WRITE;
/*!40000 ALTER TABLE `cms_languages` DISABLE KEYS */;
INSERT INTO `cms_languages` VALUES (4,'en','English');
/*!40000 ALTER TABLE `cms_languages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_log`
--

DROP TABLE IF EXISTS `cms_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cms_user_id` int(10) unsigned NOT NULL,
  `subj_table` char(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `subj_id` int(10) unsigned NOT NULL,
  `action` char(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `descr` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reg_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `subj_table` (`subj_table`,`subj_id`),
  KEY `action` (`action`),
  KEY `cms_user_id` (`cms_user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_log`
--

LOCK TABLES `cms_log` WRITE;
/*!40000 ALTER TABLE `cms_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_users`
--

DROP TABLE IF EXISTS `cms_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `role` char(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'admin',
  `password` char(96) COLLATE utf8_unicode_ci NOT NULL,
  `name` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `birth_date` date DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lang` char(2) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'az',
  `reg_by` int(10) unsigned NOT NULL,
  `reg_date` datetime NOT NULL,
  `last_login_date` datetime DEFAULT NULL,
  `is_menu_collapsed` enum('0','1') COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `is_blocked` enum('0','1') COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `login_attempts` tinyint(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  KEY `reg_by` (`reg_by`)
) ENGINE=MyISAM AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_users`
--

LOCK TABLES `cms_users` WRITE;
/*!40000 ALTER TABLE `cms_users` DISABLE KEYS */;
INSERT INTO `cms_users` VALUES (1,'admin@admin.com','admin','0378dff2bc015d42e3a805cba1c67694e28d502bbd9d42af6ca6009d63fb49ec4db45ef8584dbc43f3f315c4528f9416','Super user','1986-03-28',NULL,'en',1,'2015-03-26 12:26:32','2019-08-23 11:45:11','1','0',0);
/*!40000 ALTER TABLE `cms_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_users_actions`
--

DROP TABLE IF EXISTS `cms_users_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_users_actions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cms_user_id` int(10) unsigned NOT NULL,
  `controller` varchar(32) NOT NULL DEFAULT 'base',
  `action` varchar(32) NOT NULL DEFAULT '404',
  `is_readonly` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cms_user_id_2` (`cms_user_id`,`controller`,`action`),
  KEY `cms_user_id` (`cms_user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_users_actions`
--

LOCK TABLES `cms_users_actions` WRITE;
/*!40000 ALTER TABLE `cms_users_actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_users_actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_users_roles`
--

DROP TABLE IF EXISTS `cms_users_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_users_roles` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role` varchar(64) NOT NULL,
  `landing_page` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_users_roles`
--

LOCK TABLES `cms_users_roles` WRITE;
/*!40000 ALTER TABLE `cms_users_roles` DISABLE KEYS */;
INSERT INTO `cms_users_roles` VALUES (1,'admin','?controller=children&action=list'),(2,'clinician','?controller=children&action=list'),(3,'researcher','?controller=children&action=list');
/*!40000 ALTER TABLE `cms_users_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_users_roles_actions`
--

DROP TABLE IF EXISTS `cms_users_roles_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cms_users_roles_actions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role` varchar(32) NOT NULL DEFAULT 'all',
  `controller` varchar(32) NOT NULL DEFAULT 'base',
  `action` varchar(32) NOT NULL DEFAULT '404',
  `is_readonly` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`,`controller`,`action`)
) ENGINE=MyISAM AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_users_roles_actions`
--

LOCK TABLES `cms_users_roles_actions` WRITE;
/*!40000 ALTER TABLE `cms_users_roles_actions` DISABLE KEYS */;
INSERT INTO `cms_users_roles_actions` VALUES (1,'all','base','password_recovery','0'),(2,'all','base','sign_out','0'),(3,'all','base','sign_in','0'),(4,'all','base','404','0'),(5,'all','base','change_password','0'),(6,'all','base','ulogin','0'),(7,'admin','cms_users','list','0'),(8,'admin','statistics','dashboard','0'),(9,'admin','cms_users','add','0'),(10,'admin','cms_users','manage_privilegies','0'),(11,'admin','cms_users','delete','0'),(12,'admin','cms_users','edit','0'),(15,'admin','cms_users','ajax_set_ban','0'),(48,'admin','children','list','0'),(47,'admin','base','save_menubar_status','0'),(46,'admin','social_workers','ajax_set_ban','0'),(45,'admin','social_workers','edit','0'),(44,'admin','social_workers','delete','0'),(43,'admin','social_workers','add','0'),(42,'admin','social_workers','list','0'),(49,'admin','children','download_child_photo','0'),(50,'admin','children','view_info','0'),(51,'admin','parents','download_parent_signature','0'),(52,'admin','exercises','list','0'),(53,'clinician','children','list','1'),(54,'clinician','children','download_child_photo','1'),(55,'clinician','children','view_info','1'),(56,'clinician','parents','download_parent_signature','1'),(57,'admin','exercises','add','0'),(58,'admin','exercises','edit','0'),(59,'admin','exercises','delete','0'),(60,'researcher','children','list','1'),(61,'researcher','children','download_child_photo','1'),(80,'clinician','children','download_child_survey','0'),(64,'admin','children','download_child_survey','0'),(65,'researcher','children','download_child_survey','0'),(66,'admin','children','survey','0'),(67,'researcher','children','survey','0'),(68,'admin','children','delete','0'),(69,'admin','children','download_child_survey_assestment','0'),(70,'researcher','children','download_child_survey_assestment','0'),(71,'admin','statistics','overview','0'),(72,'clinician','statistics','overview','0'),(73,'researcher','statistics','overview','0'),(74,'admin','parent_assestment','list','0'),(75,'admin','parent_assestment','add','0'),(76,'admin','parent_assestment','delete','0'),(77,'admin','log','list','1'),(78,'clinician','children','survey','0'),(79,'clinician','children','download_child_survey_assestment','0'),(81,'admin','children','close_survey','0'),(82,'clinician','children','close_survey','0'),(83,'admin','children','delete_survey','0'),(84,'admin','children','download_surveys','0'),(85,'clinician','children','download_surveys','0'),(86,'researcher','children','download_surveys','0'),(87,'admin','children','open_survey','0'),(88,'clinician','children','open_survey','0'),(89,'admin','children','edit','0'),(90,'admin','parents','edit','0'),(91,'admin','log','download_log','0'),(92,'clinician','cms_users','change_my_password','0'),(93,'researcher','cms_users','change_my_password','0'),(94,'admin','consent_form','edit','0'),(95,'admin','parent_assestment','edit','0'),(96,'admin','states','list','0'),(97,'admin','states','add','0'),(98,'admin','states','edit','0'),(99,'admin','states','delete','0'),(100,'admin','languages','list','0'),(101,'admin','languages','add','0'),(102,'admin','languages','edit','0'),(103,'admin','languages','delete','0');
/*!40000 ALTER TABLE `cms_users_roles_actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_bubbles_jubbing_tests`
--

DROP TABLE IF EXISTS `content_bubbles_jubbing_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_bubbles_jubbing_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `bubble_img` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_bubbles_jubbing_tests`
--

LOCK TABLES `content_bubbles_jubbing_tests` WRITE;
/*!40000 ALTER TABLE `content_bubbles_jubbing_tests` DISABLE KEYS */;
INSERT INTO `content_bubbles_jubbing_tests` VALUES (1,'Small Blue Bubble','bubble.png','2017-05-19 12:49:36',1,'2017-05-19 19:38:36',1,'0');
/*!40000 ALTER TABLE `content_bubbles_jubbing_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_choose_touching_tests`
--

DROP TABLE IF EXISTS `content_choose_touching_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_choose_touching_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `video_1_social` varchar(255) NOT NULL,
  `video_1_nonsocial` varchar(255) NOT NULL,
  `video_2_social` varchar(255) NOT NULL,
  `video_2_nonsocial` varchar(255) NOT NULL,
  `video_3_social` varchar(255) NOT NULL,
  `video_3_nonsocial` varchar(255) NOT NULL,
  `video_4_social` varchar(255) NOT NULL,
  `video_4_nonsocial` varchar(255) NOT NULL,
  `demo_social` varchar(255) NOT NULL,
  `demo_nonsocial` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_choose_touching_tests`
--

LOCK TABLES `content_choose_touching_tests` WRITE;
/*!40000 ALTER TABLE `content_choose_touching_tests` DISABLE KEYS */;
INSERT INTO `content_choose_touching_tests` VALUES (1,'Choose touching test content','video_1_social.mp4','video_1_nonsocial.mp4','video_2_social.mp4','video_2_nonsocial.mp4','video_3_social.mp4','video_3_nonsocial.mp4','video_4_social.mp4','video_4_nonsocial.mp4','demo_social.mp4','demo_nonsocial.mp4','2017-05-20 14:27:26',1,'2018-04-23 18:29:50',1,'0');
/*!40000 ALTER TABLE `content_choose_touching_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_coloring_tests`
--

DROP TABLE IF EXISTS `content_coloring_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_coloring_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `img_1` varchar(255) NOT NULL,
  `img_2` varchar(255) NOT NULL,
  `img_3` varchar(255) NOT NULL,
  `img_4` varchar(255) NOT NULL,
  `img_5` varchar(255) NOT NULL,
  `img_6` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_coloring_tests`
--

LOCK TABLES `content_coloring_tests` WRITE;
/*!40000 ALTER TABLE `content_coloring_tests` DISABLE KEYS */;
INSERT INTO `content_coloring_tests` VALUES (1,'Coloring assestment','snail.png','ball.png','flower.png','butterfly.png','flower_1.png','butterfly_1.png','2017-06-27 08:57:34',1,'2017-11-28 19:52:02',1,'0');
/*!40000 ALTER TABLE `content_coloring_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_eye_tracking_pairs_tests`
--

DROP TABLE IF EXISTS `content_eye_tracking_pairs_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_eye_tracking_pairs_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `pair_1_img_1` varchar(255) NOT NULL,
  `pair_1_img_2` varchar(255) NOT NULL,
  `pair_2_img_1` varchar(255) NOT NULL,
  `pair_2_img_2` varchar(255) NOT NULL,
  `pair_3_img_1` varchar(255) NOT NULL,
  `pair_3_img_2` varchar(255) NOT NULL,
  `pair_4_img_1` varchar(255) NOT NULL,
  `pair_4_img_2` varchar(255) NOT NULL,
  `pair_5_img_1` varchar(255) NOT NULL,
  `pair_5_img_2` varchar(255) NOT NULL,
  `pair_6_img_1` varchar(255) NOT NULL,
  `pair_6_img_2` varchar(255) NOT NULL,
  `pair_7_img_1` varchar(255) NOT NULL,
  `pair_7_img_2` varchar(255) NOT NULL,
  `pair_8_img_1` varchar(255) NOT NULL,
  `pair_8_img_2` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_eye_tracking_pairs_tests`
--

LOCK TABLES `content_eye_tracking_pairs_tests` WRITE;
/*!40000 ALTER TABLE `content_eye_tracking_pairs_tests` DISABLE KEYS */;
INSERT INTO `content_eye_tracking_pairs_tests` VALUES (1,'Eye tracking pairs','objectleft1.mp4','','socialleft1.mp4','','objectleft2.mp4','','socialleft2.mp4','','objectleft3.mp4','','socialleft3.mp4','','objectleft4.mp4','','socialleft4.mp4','','2017-06-15 16:05:33',1,'2017-11-06 18:37:09',1,'0');
/*!40000 ALTER TABLE `content_eye_tracking_pairs_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_eye_tracking_slide_tests`
--

DROP TABLE IF EXISTS `content_eye_tracking_slide_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_eye_tracking_slide_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `moving_fragment` varchar(255) NOT NULL,
  `slide` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mod_by` (`mod_by`),
  KEY `add_by` (`add_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_eye_tracking_slide_tests`
--

LOCK TABLES `content_eye_tracking_slide_tests` WRITE;
/*!40000 ALTER TABLE `content_eye_tracking_slide_tests` DISABLE KEYS */;
INSERT INTO `content_eye_tracking_slide_tests` VALUES (1,'Eye tracking with moving slide','rabbit.png','ball_colored.png','2017-06-15 18:10:54',1,'2017-07-13 15:53:22',31,'0');
/*!40000 ALTER TABLE `content_eye_tracking_slide_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_motoric_following_tests`
--

DROP TABLE IF EXISTS `content_motoric_following_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_motoric_following_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `moving_fragment` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_motoric_following_tests`
--

LOCK TABLES `content_motoric_following_tests` WRITE;
/*!40000 ALTER TABLE `content_motoric_following_tests` DISABLE KEYS */;
INSERT INTO `content_motoric_following_tests` VALUES (1,'Motoric following test','bee.png','2017-06-15 18:39:12',1,NULL,NULL,'0');
/*!40000 ALTER TABLE `content_motoric_following_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_parent_assestment_tests`
--

DROP TABLE IF EXISTS `content_parent_assestment_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_parent_assestment_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` enum('common','video') NOT NULL DEFAULT 'common',
  `title` varchar(255) NOT NULL,
  `question_text` text NOT NULL,
  `question_text_hindi` varchar(255) DEFAULT NULL,
  `video_left` varchar(255) DEFAULT NULL,
  `video_right` varchar(255) DEFAULT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  `choicesBlock` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_parent_assestment_tests`
--

LOCK TABLES `content_parent_assestment_tests` WRITE;
/*!40000 ALTER TABLE `content_parent_assestment_tests` DISABLE KEYS */;
INSERT INTO `content_parent_assestment_tests` VALUES (7,'common','2','Does your child call himself by his/her name like \"Vivek will eat food\".','क्या आपका बच्चा अपने आप को नाम से बुलाता है जैसे \"विवेक खाना खाएगा\"|',NULL,NULL,'2017-09-15 21:36:43',1,NULL,NULL,'0',NULL),(8,'common','3','Does your child show you the things he/she likes by pointing fingers to them?','क्या आपका बच्चा अपनी पसंद की चीजों की तरफ ऊँगली से ईशारा करके आपको दिखाता है|',NULL,NULL,'2017-09-15 21:37:10',1,NULL,NULL,'0',NULL),(9,'common','4','Does your child repeat any kind of movement frequently? Like constantly making flapping/wriggling movement with his hands/fingers, constantly moving the body back and forth while sitting, constantly moving the head or body in unusual manner, etc.','क्या आपका बच्चा किसी एक तरह की हरकत को बार-बार दोहराता है? जैसे की हाँथो / उँगलियों  को हिलाते रहना, बैठ कर आगे-पीछे हिलते रहना, सिर या शरीर को किसी अजीब तरह से बार-बार हिलाते रहना आदि|',NULL,NULL,'2017-09-15 21:37:32',1,NULL,NULL,'0',NULL),(6,'common','1','Does your child look at your face or eyes, when you talk to him/her?','जब आप अपने बच्चे से बात करते हैं तो क्या वह आपके चेहरे या आँखों की तरफ देखता है?',NULL,NULL,'2017-09-15 21:36:06',1,NULL,NULL,'0',0),(10,'common','5','Does your child look at you / responds when called by name?','क्या आपका बच्चा नाम से बुलाये जाने पर आपकी तरफ देखता या जवाब देता है?',NULL,NULL,'2017-09-15 21:37:56',1,NULL,NULL,'0',NULL),(11,'common','6','Does your child repeat certain voices, such as the sharp (high pitched) meaningless sounds, repeating your spoken words without context or meaning, repeating any sound heard on TV/redio/computer meaninglessly?','क्या आपका बच्चा कुछ आवाजें बार-बार निकलता रहता है जैसे की बिना मतलब वाली तेज आवाजें, आपके बोले हुए शब्दों की बिना मतलब दोहराना, टीवी/रेडिओ/कम्प्यूटर पर सुनी हुई कोई आवाज बार-बार दोहराना।',NULL,NULL,'2017-09-15 21:38:20',1,NULL,NULL,'0',NULL),(12,'common','7','Does your child come to you and show you when he/she has done something good?','कुछ अच्छा करने पर क्या आपका बच्चा आपके पास आ कर आपको दिखाता है?',NULL,NULL,'2017-09-15 21:38:46',1,NULL,NULL,'0',NULL),(13,'common','8','Does your child play oddly with toys? Such as instead of using them meaningfully he/she just lines them up, or instead of running the toy car he spends long time looking at its wheels, smells or rubs toys on his body.','क्या आपका बच्चा खिलौनों से अजीब तरह से खेलता है? जैसे की बजाये उनको इस्तेमाल करने के उन्हें लाइन में लगाना, या गाड़ी को चलाने की बजाए उसके पहियों को बहुत देर तक देखते रहना, चीजों को सूंघना या हाँथ-पैर पर रगड़ना',NULL,NULL,'2017-09-15 21:39:12',1,NULL,NULL,'0',NULL),(14,'common','10','Does your child engage in pretend play, such as using something like an imaginative phone by putting it on ear and talking, pretending to cook using toy utensils, making sound of a car/auto/bike/rail while moving something etc.','क्या आपका बच्चा काल्पनिक खेल खेलता है जैसे की किसी चीज को कान पर लगा कर फ़ोन की तरह बातें करना, खिलौनें वाले बर्तनों में खाना बनाना और खाने की नक़ल करना, गाड़ी / ऑटो-रिख्शा / मोटर-साईकल / रेल  की आवाज निकाल कर किसी चीज को चलाना, आदि|',NULL,NULL,'2017-09-15 21:45:50',1,NULL,NULL,'0',NULL),(18,'common','14','Does your child get disturbed by usual sound or light? Such as getting annoyed by the sound of the kitchen utensils and trying to close the ears with hands/fingers, not able to bear the sound of the vehicles, unable to bear the fairy/festival lights, gets irritable by the sharp light of the bulb, etc. (Social worker please ask the opposite behaviour too, such as does the child like loud sounds or sharp lights? He/she watches bright lights by going close to them and/or listen to the radio / TV by sticking ears to them?)','क्या आपका बच्चा साधरण आवाजों या रौशनी से परेशान हो जाता? जैसे की कूकर/ बर्तनों की आवाज से चिढ़ जाना और कान बंद करना, गाड़ी / ऑटो-रिख्शा / मोटर-साईकल / रेल की आवाज बर्दाश्त न कर पाना, दिवाली/ईद/ त्यौहारों की रौशनी को बर्दाश्त न कर पाना, तेज़ बल्ब की रौशनी से ',NULL,NULL,'2017-09-15 21:48:52',1,NULL,NULL,'0',NULL),(17,'common','13','Does your child play cooperatively with other children or with you? Like throwing ball, hide and seek, peek-a-boo etc.','क्या आपका बच्चा अन्य बच्चों या आपके साथ मिल-जुल के खेल खेलता है? जैसे बॉल फेंकना, लुक्का - छिपी खेलना, चेहरे को किसी चीज के पीछे कुछ देर के लिए छुपाना और फिर दिखाना आदि|',NULL,NULL,'2017-09-15 21:48:11',1,NULL,NULL,'0',NULL),(19,'common','15','Does your child imitate you? Like making gesture for “bye-bye” or hello, or wearing a scarf or bag like you?','क्या आपका बच्चा आपकी नकल करता है? जैसे बॉय करना, नमस्ते करना, आपकी तरह दुपट्टा या बैग ले कर चलना?',NULL,NULL,'2017-09-15 21:49:14',1,NULL,NULL,'0',NULL),(20,'common','16','Does your child get annoyed with cloth tags, woollen or tight cloths, toothbrushes, socks etc. Or does he like rubbing some items / cloth on his body repeatedly even if it results in scratches.','क्या आपका बच्चा कपड़ों के टैग (चिट), ऊनी कपडे, कस्से कपडे, टूथ ब्रश, जुराब आदि से परेशान हो जाता है या फिर किसी चीज/कपड़े को बार-बार अपने हाँथ-पैर पर रगड़ता रहता है भले ही उससे खरोंच आ जाए',NULL,NULL,'2017-09-15 21:49:33',1,NULL,NULL,'0',0),(21,'common','17','Is your child able to use language according to his/her age? Like adding words to make sentence \"let’s go out\", or to answer you correctly and asking questions \"what is that?\", \"when are we going?\" etc.','क्या आपका बच्चा अपनी उम्र के हिसाब से शब्दों का  इस्तेमाल बात चीत के लिए कर पाता है. जैसे दो शब्द जोड़ कर बोलना \"बाहार चलो\", या आपकी बातों का सही से जवाब देना और आपसे सवाल पूछना \"वो क्या है\", \"हम कब जाएंगें\" आदि',NULL,NULL,'2017-09-15 21:49:53',1,NULL,NULL,'0',0);
/*!40000 ALTER TABLE `content_parent_assestment_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_parent_child_play_tests`
--

DROP TABLE IF EXISTS `content_parent_child_play_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_parent_child_play_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `instruction` text NOT NULL,
  `instruction_hindi` text,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_parent_child_play_tests`
--

LOCK TABLES `content_parent_child_play_tests` WRITE;
/*!40000 ALTER TABLE `content_parent_child_play_tests` DISABLE KEYS */;
INSERT INTO `content_parent_child_play_tests` VALUES (1,'Parent-child play','Preparation for Health worker: Make child/parent on the floor/bed and keep the toys towards the parent.\r\nMake the parents and child sit in a position so that their faces can clearly be seen and recorded by the tablet camera. \r\nDuring the task, you might need to move the camera to ensure that it captures child\'s facial expressions, verbal responses and play behaviour. \r\nMake sure that parent or child do not have a mobile phone around them and there is no other distractor like TV, radio, or any other source of noise.\r\nPlease make sure that there is only one parent in the room with the child. No other family member (siblings, grandparents etc) should be allowed.     \r\nBefore starting the recording please ensure that parent knows what they are expected to do. \r\n\r\nInstructions for the parent:  Here is a box of toys. Please play with these toys as you would at home. In a few minutes, I will make a sound. At that point, please start folding the provided cloths slowly and appear busy, allowing your child to play independently. If he/she tries to interact with you, you can respond normally, then try to quickly return to folding clothes. After a couple minutes, I will make a sound again. At that point, it will be time to clean up. Please try to have your child help you place the toys back in the bin.','तैयारी सोशल वर्कर : बच्चे और माँ/पिता को ज़मीन/पलंग  पर बैठाएं और खिलौनों को माँ/पिता की तरफ रख दें |\r\nमाता/पिता  और बच्चे को इस तरह बैठाएं की उन दोनों के चहरे कैमरे मैं साफ़ नज़र आयें | \r\nगेम के दौरान आपको कैमरे की जगह बदलनी पड़ सकती है ताकि बच्चे के हाव-भाव, आवाज़ , व खेल को साफ़ तरह से रिकॉर्ड किया जा सके| \r\nअभिभावक या बच्चे के आस-पास मोबाइल फ़ोन ना रखें \r\nटीवी, रेडिओ, या कोई भी और शोर को बन्द कर दें \r\nआस-पास एक से अधिक अभिभावक न रहें | कृपया जांच लें की कोई और घर का सदस्य (भाई -बहन, दादा-दादी) कमरे में नहीं है |\r\nकैमरा शुरू करने के पहले निश्चित कर लें की अभिभावक जानते हैं की उन्हें क्या करना है \r\n\r\nअभिभावक के लिए निर्देश : कृपया दिए गए खिलौनों को इस्तेमाल करते हुए बच्चे के साथ उसी तरह खेलें जैसे की आप हमेशा खेलते हैं \r\nजब मैं आपको ईशारा करूँ तो कृपया खेल को वहीँ छोड़ दें व पास रखे हुए कपड़ों को धीरे -धीरे मोड़ने लगें |  इस समय बच्चे को अपने आप खेलने दें | इस समय यदि बच्चा आपसे बात करने की कोशिश करे तो उसे थोड़ी देर को ध्यान दें व वापिस कपडे मोड़ने लगें | \r\nजब मैं आपको दूसरा ईशारा दूँ तो कृपया खिलौनों को समेटने का प्रयास करें| इस समय बच्चे को खिलौने समेटने में आपकी मदद करने के लिए प्रोत्साहित करें|','2017-07-12 08:57:34',1,'2018-03-15 14:32:56',1,'0');
/*!40000 ALTER TABLE `content_parent_child_play_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_wheel_tests`
--

DROP TABLE IF EXISTS `content_wheel_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_wheel_tests` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `video` varchar(255) NOT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(11) NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(11) DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_wheel_tests`
--

LOCK TABLES `content_wheel_tests` WRITE;
/*!40000 ALTER TABLE `content_wheel_tests` DISABLE KEYS */;
INSERT INTO `content_wheel_tests` VALUES (1,'Wheel test','wheel.mp4','2017-09-07 15:09:34',1,'2017-10-11 18:26:46',1,'0');
/*!40000 ALTER TABLE `content_wheel_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `languages`
--

DROP TABLE IF EXISTS `languages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `languages` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` char(100) NOT NULL,
  `name_hindi` char(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `languages`
--

LOCK TABLES `languages` WRITE;
/*!40000 ALTER TABLE `languages` DISABLE KEYS */;
INSERT INTO `languages` VALUES (1,'Assamese','असमी'),(2,'Bengali','बंगाली'),(3,'Bodo','बोडो'),(4,'Dogri','डोगरी'),(5,'Gujarati','गुजराती'),(6,'Hindi','हिंदी'),(7,'Kannada','कन्नड़'),(8,'Kashmiri','कश्मीरी'),(9,'Konkani','कोंकणी'),(10,'Maithili','मैथिली'),(11,'Malayalam','मलयालम'),(12,'Meitei (Manipuri)','मेईटी (मणिपुरी)'),(13,'Marathi','मराठी'),(14,'Nepali','नेपाली'),(15,'Odia','ओरिया'),(16,'Punjabi','पंजाबी'),(17,'Sanskrit','संस्कृत'),(18,'Santhali','संथाली'),(19,'Sindhi','सिंधी'),(20,'Tamil','तामिल'),(21,'Telugu','तेलुगू'),(22,'Urdu','उर्दू'),(23,'Other','अन्य');
/*!40000 ALTER TABLE `languages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parents`
--

DROP TABLE IF EXISTS `parents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parents` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `child_id` int(10) unsigned NOT NULL,
  `child_relationship` enum('parent','guardian') NOT NULL DEFAULT 'parent',
  `name` varbinary(400) NOT NULL,
  `surname` varbinary(400) NOT NULL,
  `patronymic` varbinary(400) NOT NULL,
  `state` varbinary(400) NOT NULL,
  `address` varbinary(400) NOT NULL,
  `gender` varbinary(200) NOT NULL,
  `birth_date` varbinary(200) NOT NULL,
  `spoken_language` varbinary(400) NOT NULL,
  `phone` varbinary(400) DEFAULT NULL,
  `email` varbinary(400) DEFAULT NULL,
  `preferable_contact` varbinary(200) DEFAULT NULL,
  `signature_scan` varchar(255) DEFAULT NULL,
  `add_datetime` datetime NOT NULL,
  `add_by` int(10) unsigned NOT NULL,
  `mod_datetime` datetime DEFAULT NULL,
  `mod_by` int(10) unsigned DEFAULT NULL,
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `child_id` (`child_id`),
  KEY `add_by` (`add_by`),
  KEY `mod_by` (`mod_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parents`
--

LOCK TABLES `parents` WRITE;
/*!40000 ALTER TABLE `parents` DISABLE KEYS */;
/*!40000 ALTER TABLE `parents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_languages`
--

DROP TABLE IF EXISTS `site_languages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site_languages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_dir` char(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `language_name` char(64) NOT NULL,
  `is_published` enum('0','1') NOT NULL DEFAULT '0',
  `is_default` enum('0','1') NOT NULL DEFAULT '0',
  `is_rtl` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `language_dir` (`language_dir`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_languages`
--

LOCK TABLES `site_languages` WRITE;
/*!40000 ALTER TABLE `site_languages` DISABLE KEYS */;
/*!40000 ALTER TABLE `site_languages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_settings`
--

DROP TABLE IF EXISTS `site_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site_settings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `option` char(64) NOT NULL,
  `value` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `option` (`option`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_settings`
--

LOCK TABLES `site_settings` WRITE;
/*!40000 ALTER TABLE `site_settings` DISABLE KEYS */;
INSERT INTO `site_settings` VALUES (2,'site_default_lang_dir','en'),(3,'cms_name','START CMS'),(4,'cms_default_lang','en'),(5,'cms_name_formatted','<b>START</b> CMS'),(6,'cms_default_landing_page','?controller=children&action=list');
/*!40000 ALTER TABLE `site_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `social_workers`
--

DROP TABLE IF EXISTS `social_workers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_workers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` char(96) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `color` varchar(24) NOT NULL,
  `gender` enum('M','F') NOT NULL DEFAULT 'M',
  `birth_date` date NOT NULL,
  `reg_date` datetime NOT NULL,
  `reg_by` int(10) unsigned NOT NULL,
  `is_blocked` enum('0','1') NOT NULL DEFAULT '0',
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  `login_attempts` tinyint(3) unsigned NOT NULL,
  `password_recovery_attempts` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `reg_by` (`reg_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `social_workers`
--

LOCK TABLES `social_workers` WRITE;
/*!40000 ALTER TABLE `social_workers` DISABLE KEYS */;
/*!40000 ALTER TABLE `social_workers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states`
--

DROP TABLE IF EXISTS `states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `states` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` char(100) NOT NULL,
  `name_hindi` char(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states`
--

LOCK TABLES `states` WRITE;
/*!40000 ALTER TABLE `states` DISABLE KEYS */;
INSERT INTO `states` VALUES (1,'Andhra Pradesh','आंध्र प्रदेश'),(2,'Arunachal Pradesh','अरुणाचल प्रदेश'),(3,'Assam','असम'),(4,'Bihar','बिहार'),(5,'Chhattisgarh','छत्तीसगढ़'),(6,'Goa','गोवा'),(7,'Gujarat','गुजरात'),(8,'Haryana','हरयाणा'),(10,'Himachal Pradesh','हिमाचल प्रदेश'),(11,'Jammu & Kashmir','जम्मू और कश्मीर'),(13,'Jharkhand','झारखंड'),(14,'Karnataka','कर्नाटक'),(15,'Kerala','केरल'),(16,'Madhya Pradesh','मध्य प्रदेश'),(17,'Maharashtra','महाराष्ट्र'),(18,'Manipur','मणिपुर'),(19,'Meghalaya','मेघालय'),(20,'Mizoram','मिजोरम'),(21,'Nagaland','नगालैंड'),(22,'Odisha','ओडिशा'),(23,'Punjab','पंजाब'),(24,'Rajasthan','राजस्थान'),(25,'Sikkim','सिक्किम'),(26,'Tamil Nadu','तमिलनाडु'),(27,'Telangana','तेलंगाना'),(28,'Tripura','त्रिपुरा'),(29,'Uttarakhand','उत्तराखंड'),(30,'Uttar Pradesh','उत्तर प्रदेश'),(31,'West Bengal','पश्चिम बंगाल'),(32,'Andaman and Nicobar Islands','अंडमान व नोकोबार द्वीप समूह'),(33,'Chandigarh','चंडीगढ़'),(34,'Delhi','दिल्ली'),(35,'Dadra and Nagar Haveli','दादरा और नगर हवेली'),(36,'Daman and Diu','दमन और दीव'),(37,'Lakshadweep','लक्षद्वीप'),(38,'Puducherry','पुडुचेरी'),(39,'test-english','test-hindi');
/*!40000 ALTER TABLE `states` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_attachments`
--

DROP TABLE IF EXISTS `survey_attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_attachments` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `survey_id` int(10) unsigned NOT NULL,
  `assestment_type` varchar(255) DEFAULT NULL,
  `attempt` int(10) unsigned NOT NULL DEFAULT '1',
  `attachment_file` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_id_2` (`survey_id`,`attachment_file`),
  KEY `survey_id` (`survey_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_attachments`
--

LOCK TABLES `survey_attachments` WRITE;
/*!40000 ALTER TABLE `survey_attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_results`
--

DROP TABLE IF EXISTS `survey_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_results` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `survey_id` int(10) unsigned NOT NULL,
  `assestment_type` varchar(255) DEFAULT NULL,
  `attempt` int(10) unsigned NOT NULL DEFAULT '1',
  `result_file` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `survey_id_2` (`survey_id`,`result_file`),
  KEY `survey_id` (`survey_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_results`
--

LOCK TABLES `survey_results` WRITE;
/*!40000 ALTER TABLE `survey_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveys`
--

DROP TABLE IF EXISTS `surveys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `surveys` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `child_id` int(10) unsigned NOT NULL,
  `created_datetime` datetime NOT NULL,
  `created_by_social_worker` int(10) unsigned NOT NULL,
  `is_completed` enum('0','1') NOT NULL DEFAULT '0',
  `completed_datetime` datetime DEFAULT NULL,
  `is_closed` enum('0','1') NOT NULL DEFAULT '0',
  `is_inspected` enum('0','1') NOT NULL DEFAULT '0',
  `is_deleted` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `child_id` (`child_id`),
  KEY `created_by_social_worker` (`created_by_social_worker`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
/*!40000 ALTER TABLE `surveys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `children`
--
ALTER TABLE `children`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `cms_languages`
--
ALTER TABLE `cms_languages`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `cms_log`
--
ALTER TABLE `cms_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `cms_users`
--
ALTER TABLE `cms_users`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `cms_users_actions`
--
ALTER TABLE `cms_users_actions`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `cms_users_roles`
--
ALTER TABLE `cms_users_roles`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `cms_users_roles_actions`
--
ALTER TABLE `cms_users_roles_actions`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- AUTO_INCREMENT for table `content_bubbles_jubbing_tests`
--
ALTER TABLE `content_bubbles_jubbing_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_choose_touching_tests`
--
ALTER TABLE `content_choose_touching_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_coloring_tests`
--
ALTER TABLE `content_coloring_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_eye_tracking_pairs_tests`
--
ALTER TABLE `content_eye_tracking_pairs_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_eye_tracking_slide_tests`
--
ALTER TABLE `content_eye_tracking_slide_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_motoric_following_tests`
--
ALTER TABLE `content_motoric_following_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_parent_assestment_tests`
--
ALTER TABLE `content_parent_assestment_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `content_parent_child_play_tests`
--
ALTER TABLE `content_parent_child_play_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `content_wheel_tests`
--
ALTER TABLE `content_wheel_tests`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `languages`
--
ALTER TABLE `languages`
  MODIFY `id` mediumint(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `parents`
--
ALTER TABLE `parents`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `site_languages`
--
ALTER TABLE `site_languages`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `site_settings`
--
ALTER TABLE `site_settings`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `social_workers`
--
ALTER TABLE `social_workers`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `states`
--
ALTER TABLE `states`
  MODIFY `id` mediumint(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `surveys`
--
ALTER TABLE `surveys`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `survey_attachments`
--
ALTER TABLE `survey_attachments`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `survey_results`
--
ALTER TABLE `survey_results`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
COMMIT;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-23 10:27:02
