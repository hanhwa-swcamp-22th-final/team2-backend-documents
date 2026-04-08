/*M!999999\- enable the sandbox mode */ 

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `company` (`company_id`, `company_address_en`, `company_address_kr`, `company_email`, `company_fax`, `company_name`, `company_seal_image_url`, `company_tel`, `company_website`, `updated_at`) VALUES (1,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(2,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(3,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(4,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(5,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(6,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(7,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(8,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(9,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(10,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL),
(11,'86 Cheonggyecheon-ro, Jung-gu, Seoul','서울특별시 중구 청계천로 86','contact@hanwha.com','02-729-2799','한화솔루션',NULL,'02-729-2700','https://www.hanwhasolutions.com',NULL);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `departments` (`department_id`, `created_at`, `department_name`) VALUES (1,'2026-04-06 09:43:34.000000','영업부'),
(2,'2026-04-06 09:43:34.000000','생산부'),
(3,'2026-04-06 09:43:34.000000','출하부'),
(4,'2026-04-06 09:43:34.000000','경영지원부'),
(5,'2026-04-06 10:12:27.000000','영업부'),
(6,'2026-04-06 10:12:27.000000','생산부'),
(7,'2026-04-06 10:12:27.000000','출하부'),
(8,'2026-04-06 10:12:27.000000','경영지원부'),
(9,'2026-04-06 10:12:45.000000','영업부'),
(10,'2026-04-06 10:12:45.000000','생산부'),
(11,'2026-04-06 10:12:45.000000','출하부'),
(12,'2026-04-06 10:12:45.000000','경영지원부'),
(13,'2026-04-06 10:12:54.000000','영업부'),
(14,'2026-04-06 10:12:54.000000','생산부'),
(15,'2026-04-06 10:12:54.000000','출하부'),
(16,'2026-04-06 10:12:54.000000','경영지원부'),
(17,'2026-04-06 10:13:03.000000','영업부'),
(18,'2026-04-06 10:13:03.000000','생산부'),
(19,'2026-04-06 10:13:03.000000','출하부'),
(20,'2026-04-06 10:13:03.000000','경영지원부'),
(21,'2026-04-06 10:13:13.000000','영업부'),
(22,'2026-04-06 10:13:13.000000','생산부'),
(23,'2026-04-06 10:13:13.000000','출하부'),
(24,'2026-04-06 10:13:13.000000','경영지원부'),
(25,'2026-04-06 10:13:23.000000','영업부'),
(26,'2026-04-06 10:13:23.000000','생산부'),
(27,'2026-04-06 10:13:23.000000','출하부'),
(28,'2026-04-06 10:13:23.000000','경영지원부'),
(29,'2026-04-06 10:13:34.000000','영업부'),
(30,'2026-04-06 10:13:34.000000','생산부'),
(31,'2026-04-06 10:13:34.000000','출하부'),
(32,'2026-04-06 10:13:34.000000','경영지원부'),
(33,'2026-04-06 10:13:47.000000','영업부'),
(34,'2026-04-06 10:13:47.000000','생산부'),
(35,'2026-04-06 10:13:47.000000','출하부'),
(36,'2026-04-06 10:13:47.000000','경영지원부'),
(37,'2026-04-06 10:14:03.000000','영업부'),
(38,'2026-04-06 10:14:03.000000','생산부'),
(39,'2026-04-06 10:14:03.000000','출하부'),
(40,'2026-04-06 10:14:03.000000','경영지원부'),
(41,'2026-04-06 10:14:25.000000','영업부'),
(42,'2026-04-06 10:14:25.000000','생산부'),
(43,'2026-04-06 10:14:25.000000','출하부'),
(44,'2026-04-06 10:14:25.000000','경영지원부');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `positions` (`position_id`, `created_at`, `position_level`, `position_name`) VALUES (1,'2026-04-06 09:43:34.000000',1,'팀장'),
(2,'2026-04-06 09:43:34.000000',2,'팀원'),
(3,'2026-04-06 10:12:27.000000',1,'팀장'),
(4,'2026-04-06 10:12:27.000000',2,'팀원'),
(5,'2026-04-06 10:12:45.000000',1,'팀장'),
(6,'2026-04-06 10:12:45.000000',2,'팀원'),
(7,'2026-04-06 10:12:54.000000',1,'팀장'),
(8,'2026-04-06 10:12:54.000000',2,'팀원'),
(9,'2026-04-06 10:13:03.000000',1,'팀장'),
(10,'2026-04-06 10:13:03.000000',2,'팀원'),
(11,'2026-04-06 10:13:13.000000',1,'팀장'),
(12,'2026-04-06 10:13:13.000000',2,'팀원'),
(13,'2026-04-06 10:13:23.000000',1,'팀장'),
(14,'2026-04-06 10:13:23.000000',2,'팀원'),
(15,'2026-04-06 10:13:34.000000',1,'팀장'),
(16,'2026-04-06 10:13:34.000000',2,'팀원'),
(17,'2026-04-06 10:13:47.000000',1,'팀장'),
(18,'2026-04-06 10:13:47.000000',2,'팀원'),
(19,'2026-04-06 10:14:03.000000',1,'팀장'),
(20,'2026-04-06 10:14:03.000000',2,'팀원'),
(21,'2026-04-06 10:14:26.000000',1,'팀장'),
(22,'2026-04-06 10:14:26.000000',2,'팀원');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `refresh_tokens` (`refresh_token_id`, `created_at`, `token_expires_at`, `token_value`, `user_id`) VALUES (1,'2026-04-06 09:45:02.377294','2026-04-13 09:45:02.361309','1fec4935-b7da-4d84-bf07-f9bcb0e2e483',1),
(2,'2026-04-06 09:45:27.994762','2026-04-13 09:45:27.994455','e133ab51-f79f-4b9a-9dc3-e6631a584081',1),
(3,'2026-04-06 10:15:18.927754','2026-04-13 10:15:18.915796','f111cfce-da9a-446d-bd6f-f5d308e39768',1),
(4,'2026-04-06 10:18:48.873590','2026-04-13 10:18:48.873343','2840aff2-76d8-42b1-94ac-d04e60d072f1',1),
(5,'2026-04-06 10:31:53.888376','2026-04-13 10:31:53.888093','231849f8-3fb9-4c56-a04a-f22e6742ad39',1),
(6,'2026-04-06 10:51:49.104603','2026-04-13 10:51:49.104273','c6a05ca3-a346-4522-8f8b-5478f9ecc2df',1),
(7,'2026-04-07 00:04:18.145391','2026-04-14 00:04:18.145176','3c793900-6d22-4141-8808-975aa2c3cff6',1),
(8,'2026-04-07 00:21:54.504149','2026-04-14 00:21:54.503818','9302ecf3-a6ac-4015-96ae-e26dcca8c54e',1),
(9,'2026-04-07 00:22:25.603895','2026-04-14 00:22:25.603646','cec53ee8-2bb2-4959-b04e-45336ffd0722',1),
(12,'2026-04-07 00:35:26.320849','2026-04-14 00:35:26.320503','d24eb1eb-fac6-425d-8589-678d66eee643',1),
(13,'2026-04-07 00:43:48.754800','2026-04-14 00:43:48.754547','7f518bb2-d44c-4891-9906-a45fd548744c',1),
(26,'2026-04-07 00:48:08.777781','2026-04-14 00:48:08.777002','574408c0-1f76-4b87-b6bf-c49a119ff5bc',1);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `users` (`user_id`, `created_at`, `employee_no`, `updated_at`, `user_email`, `user_name`, `user_pw`, `user_role`, `user_status`, `department_id`, `position_id`) VALUES (1,'2026-04-06 09:43:34.000000','26030101','2026-04-06 09:43:34.000000','admin@hanwha.com','관리자','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','admin','active',4,1),
(2,'2026-04-06 09:43:34.000000','26030102','2026-04-06 09:43:34.000000','kim.sales@hanwha.com','김영업','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','sales','active',1,2),
(3,'2026-04-06 09:43:34.000000','26030103','2026-04-06 09:43:34.000000','lee.sales@hanwha.com','이영업','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','sales','active',1,1),
(4,'2026-04-06 09:43:34.000000','26030201','2026-04-06 09:43:34.000000','park.prod@hanwha.com','박생산','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','production','active',2,2),
(5,'2026-04-06 09:43:34.000000','26030202','2026-04-06 09:43:34.000000','choi.prod@hanwha.com','최생산','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','production','active',2,1),
(6,'2026-04-06 09:43:34.000000','26030301','2026-04-06 09:43:34.000000','jung.ship@hanwha.com','정출하','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','shipping','active',3,2),
(7,'2026-04-06 09:43:34.000000','26030104','2026-04-06 09:43:34.000000','kang.quit@hanwha.com','강퇴직','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','sales','retired',1,2),
(8,'2026-04-06 09:43:34.000000','26030203','2026-04-06 09:43:34.000000','yoon.leave@hanwha.com','윤휴직','$2b$10$SxK6lE4ZQQ6taakdICBgQ.huW.4qW.KFx0k0904M3GNh4XGo06gAO','production','on_leave',2,2);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

