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
INSERT INTO `buyers` (`buyer_id`, `buyer_email`, `buyer_name`, `buyer_position`, `buyer_tel`, `created_at`, `updated_at`, `client_id`) VALUES (1,'j.smith@globaltech.com','John Smith','Procurement Manager','+1-212-555-0101','2026-04-06 09:43:32.000000','2026-04-06 09:43:32.000000',1),
(2,'s.johnson@globaltech.com','Sarah Johnson','Senior Buyer','+1-212-555-0102','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',1),
(3,'m.davis@globaltech.com','Mike Davis','Technical Director','+1-212-555-0103','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',1),
(4,'d.lee@pacifictrading.com','David Lee','Import Manager','+1-310-555-0201','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',2),
(5,'j.park@pacifictrading.com','Jennifer Park','Buyer','+1-310-555-0202','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',2),
(6,'y.tanaka@tokyoelec.co.jp','Tanaka Yuki','Purchasing Manager','+81-3-5555-0301','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',3),
(7,'k.suzuki@tokyoelec.co.jp','Suzuki Kenji','Assistant Manager','+81-3-5555-0302','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',3),
(8,'a.yamamoto@tokyoelec.co.jp','Yamamoto Aiko','Engineer','+81-3-5555-0303','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',3),
(9,'l.wang@shanghaiie.cn','Wang Lei','Trade Director','+86-21-5555-0401','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',4),
(10,'w.zhang@shanghaiie.cn','Zhang Wei','Buyer','+86-21-5555-0402','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',4),
(11,'h.mueller@hamburgchem.de','Hans Mueller','Procurement Head','+49-40-5555-0501','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',5),
(12,'a.schmidt@hamburgchem.de','Anna Schmidt','Senior Buyer','+49-40-5555-0502','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',5),
(13,'k.weber@hamburgchem.de','Klaus Weber','Technical Buyer','+49-40-5555-0503','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',5),
(14,'j.wilson@londonmat.co.uk','James Wilson','Managing Director','+44-20-5555-0601','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',6),
(15,'e.brown@londonmat.co.uk','Emily Brown','Buyer','+44-20-5555-0602','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',6),
(16,'an.nguyen@vnmanufacturing.vn','Nguyen Van An','General Manager','+84-28-5555-0701','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',7),
(17,'mai.tran@vnmanufacturing.vn','Tran Thi Mai','Procurement Officer','+84-28-5555-0702','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',7),
(18,'wm.tan@sgpsolutions.sg','Tan Wei Ming','Operations Director','+65-6555-0801','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',8),
(19,'ml.lim@sgpsolutions.sg','Lim Mei Ling','Procurement Manager','+65-6555-0802','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',8),
(20,'ks.ng@sgpsolutions.sg','Ng Kah Seng','Engineer','+65-6555-0803','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',8),
(21,'h.sato@osakatrading.co.jp','Sato Hiroshi','Division Head','+81-6-5555-0901','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',9),
(22,'s.ito@osakatrading.co.jp','Ito Sakura','Buyer','+81-6-5555-0902','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',9),
(23,'m.li@shenzhentech.cn','Li Ming','CEO','+86-755-5555-1001','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',10),
(24,'x.chen@shenzhentech.cn','Chen Xiao','Technical Manager','+86-755-5555-1002','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',10),
(25,'r.taylor@houstonenergy.com','Robert Taylor','VP of Procurement','+1-713-555-1101','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',11),
(26,'l.anderson@houstonenergy.com','Lisa Anderson','Supply Chain Manager','+1-713-555-1102','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',11),
(27,'a.rashid@dubaiintl.ae','Ahmed Al Rashid','Managing Partner','+971-4-555-1201','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',12),
(28,'f.hassan@dubaiintl.ae','Fatima Hassan','Import Coordinator','+971-4-555-1202','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',12),
(29,'o.khalil@dubaiintl.ae','Omar Khalil','Buyer','+971-4-555-1203','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',12),
(30,'s.prasert@bangkokpoly.th','Somchai Prasert','Director','+66-2-555-1301','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',13),
(31,'p.suksai@bangkokpoly.th','Pranee Suksai','Purchasing','+66-2-555-1302','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',13),
(32,'c.thompson@sydneyres.com.au','Chris Thompson','Operations Manager','+61-2-5555-1401','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',14),
(33,'o.martin@sydneyres.com.au','Olivia Martin','Buyer','+61-2-5555-1402','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',14),
(34,'r.patel@mumbaiind.in','Rajesh Patel','Purchase Head','+91-22-5555-1501','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',15),
(35,'p.sharma@mumbaiind.in','Priya Sharma','Senior Buyer','+91-22-5555-1502','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',15),
(36,'a.singh@mumbaiind.in','Amit Singh','Quality Engineer','+91-22-5555-1503','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',15),
(37,'p.dubois@parisluxe.fr','Pierre Dubois','Directeur Achats','+33-1-5555-1601','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',16),
(38,'m.laurent@parisluxe.fr','Marie Laurent','Acheteuse','+33-1-5555-1602','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',16),
(39,'f.bauer@berlineng.de','Friedrich Bauer','Einkaufsleiter','+49-30-5555-1701','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',17),
(40,'g.hoffmann@berlineng.de','Greta Hoffmann','Projektingenieurin','+49-30-5555-1702','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',17),
(41,'duc.le@hanoiimport.vn','Le Van Duc','Import Manager','+84-24-5555-1801','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',18),
(42,'lan.pham@hanoiimport.vn','Pham Thi Lan','Coordinator','+84-24-5555-1802','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',18),
(43,'junho.park@incheonpartners.kr','박준호','구매팀장','+82-32-555-1901','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',19),
(44,'sujung.choi@incheonpartners.kr','최수정','구매담당','+82-32-555-1902','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',19),
(45,'k.watanabe@yokohamaplast.co.jp','Watanabe Koji','Manager','+81-45-5555-2001','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',20),
(46,'y.nakamura@yokohamaplast.co.jp','Nakamura Yui','Buyer','+81-45-5555-2002','2026-04-06 09:43:33.000000','2026-04-06 09:43:33.000000',20);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `clients` (`client_id`, `client_address`, `client_city`, `client_code`, `client_email`, `client_manager`, `client_name`, `client_name_kr`, `client_reg_date`, `client_status`, `client_tel`, `created_at`, `department_id`, `updated_at`, `country_id`, `currency_id`, `payment_term_id`, `port_id`) VALUES (1,'123 Broadway, New York, NY 10001','New York','CLI001','contact@globaltech.com','김영업','Global Tech Inc.','글로벌테크','2024-01-15','active','+1-212-555-0100','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',1,1,1,4),
(2,'456 Sunset Blvd, LA, CA 90028','Los Angeles','CLI002','info@pacifictrading.com','김영업','Pacific Trading Co.','퍼시픽트레이딩','2024-02-01','active','+1-310-555-0200','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',1,1,1,5),
(3,'1-1 Marunouchi, Chiyoda-ku, Tokyo','Tokyo','CLI003','sales@tokyoelec.co.jp','김영업','Tokyo Electronics Ltd.','도쿄일렉트로닉스','2024-02-15','active','+81-3-5555-0300','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',3,4,2,7),
(4,'100 Nanjing Road, Shanghai','Shanghai','CLI004','trade@shanghaiie.cn','이영업','Shanghai Import Export','상하이무역','2024-03-01','active','+86-21-5555-0400','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',4,5,2,9),
(5,'Hafenstrasse 10, 20457 Hamburg','Hamburg','CLI005','info@hamburgchem.de','이영업','Hamburg Chemicals GmbH','함부르크케미칼','2024-03-15','active','+49-40-5555-0500','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',5,2,2,11),
(6,'10 Canary Wharf, London E14','London','CLI006','procurement@londonmat.co.uk','김영업','London Materials Ltd.','런던머티리얼즈','2024-04-01','active','+44-20-5555-0600','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',6,6,1,12),
(7,'72 Le Loi, District 1, HCMC','Ho Chi Minh','CLI007','order@vnmanufacturing.vn','이영업','Vietnam Manufacturing','베트남제조','2024-04-15','active','+84-28-5555-0700','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',8,1,1,14),
(8,'1 Raffles Place, Singapore 048616','Singapore','CLI008','biz@sgpsolutions.sg','김영업','Singapore Solutions Pte.','싱가포르솔루션즈','2024-05-01','active','+65-6555-0800','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',10,1,1,13),
(9,'2-3 Nakanoshima, Kita-ku, Osaka','Osaka','CLI009','trade@osakatrading.co.jp','이영업','Osaka Trading Corp.','오사카트레이딩','2024-05-15','active','+81-6-5555-0900','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',3,4,2,8),
(10,'88 Shennan Road, Futian, Shenzhen','Shenzhen','CLI010','info@shenzhentech.cn','김영업','Shenzhen Tech Co.','선전테크','2024-06-01','active','+86-755-5555-1000','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',4,5,3,10),
(11,'1000 Main St, Houston, TX 77002','Houston','CLI011','energy@houstonenergy.com','이영업','Houston Energy Corp.','휴스턴에너지','2024-06-15','active','+1-713-555-1100','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',1,1,1,6),
(12,'Jebel Ali Free Zone, Dubai','Dubai','CLI012','trade@dubaiintl.ae','김영업','Dubai International Trading','두바이국제무역','2024-07-01','active','+971-4-555-1200','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',13,1,1,15),
(13,'99 Silom Road, Bangrak, Bangkok','Bangkok','CLI013','poly@bangkokpoly.th','이영업','Bangkok Polymers Co.','방콕폴리머','2024-07-15','active','+66-2-555-1300','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',9,1,3,NULL),
(14,'200 George St, Sydney NSW 2000','Sydney','CLI014','resources@sydneyres.com.au','김영업','Sydney Resources Pty.','시드니리소시스','2024-08-01','active','+61-2-5555-1400','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',11,1,1,NULL),
(15,'Nariman Point, Mumbai 400021','Mumbai','CLI015','sales@mumbaiind.in','이영업','Mumbai Industries Ltd.','뭄바이인더스트리','2024-08-15','active','+91-22-5555-1500','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',12,1,3,NULL),
(16,'8 Rue de Rivoli, 75001 Paris','Paris','CLI016','luxe@parisluxe.fr','김영업','Paris Luxe Materials','파리럭스머티리얼','2024-09-01','active','+33-1-5555-1600','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',7,2,2,NULL),
(17,'Unter den Linden 1, 10117 Berlin','Berlin','CLI017','eng@berlineng.de','이영업','Berlin Engineering AG','베를린엔지니어링','2024-09-15','active','+49-30-5555-1700','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',5,2,2,11),
(18,'36 Tran Hung Dao, Hoan Kiem, Hanoi','Hanoi','CLI018','import@hanoiimport.vn','김영업','Hanoi Import Co.','하노이임포트','2024-10-01','active','+84-24-5555-1800','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',8,1,1,14),
(19,'송도국제대로 123, 인천','Incheon','CLI019','partner@incheonpartners.kr','이영업','Incheon Partners Co.','인천파트너스','2024-10-15','inactive','+82-32-555-1900','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',2,3,1,2),
(20,'2-1 Minato Mirai, Nishi-ku, Yokohama','Yokohama','CLI020','plastics@yokohamaplast.co.jp','김영업','Yokohama Plastics Ltd.','요코하마플라스틱','2024-11-01','inactive','+81-45-5555-2000','2026-04-06 09:43:32.000000',1,'2026-04-06 09:43:32.000000',3,4,2,7);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `countries` (`country_id`, `country_code`, `country_name`, `country_name_kr`) VALUES (1,'US','United States','미국'),
(2,'KR','South Korea','대한민국'),
(3,'JP','Japan','일본'),
(4,'CN','China','중국'),
(5,'DE','Germany','독일'),
(6,'GB','United Kingdom','영국'),
(7,'FR','France','프랑스'),
(8,'VN','Vietnam','베트남'),
(9,'TH','Thailand','태국'),
(10,'SG','Singapore','싱가포르'),
(11,'AU','Australia','호주'),
(12,'IN','India','인도'),
(13,'AE','United Arab Emirates','아랍에미리트');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `currencies` (`currency_id`, `currency_code`, `currency_name`, `currency_symbol`) VALUES (1,'USD','US Dollar','$'),
(2,'EUR','Euro','€'),
(3,'KRW','Korean Won','₩'),
(4,'JPY','Japanese Yen','¥'),
(5,'CNY','Chinese Yuan','¥'),
(6,'GBP','British Pound','£');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `incoterms` (`incoterm_id`, `incoterm_code`, `incoterm_default_named_place`, `incoterm_description`, `incoterm_name`, `incoterm_name_kr`, `incoterm_seller_segments`, `incoterm_transport_mode`) VALUES (1,'EXW','매도인 공장','매수인이 매도인의 영업장에서 물품을 인수','Ex Works','공장인도','매도인 영업장','복합운송'),
(2,'FCA','매도인 영업장','매도인이 지정된 장소에서 운송인에게 물품 인도','Free Carrier','운송인인도','지정 장소','복합운송'),
(3,'CPT','목적지 터미널','매도인이 지정 목적지까지 운송비 부담','Carriage Paid To','운송비지급인도','지정 목적지','복합운송'),
(4,'CIP','목적지 터미널','매도인이 운송비와 보험료 부담','Carriage and Insurance Paid To','운송비보험료지급인도','지정 목적지','복합운송'),
(5,'DAP','매수인 영업장','매도인이 지정 목적지에서 양하 준비된 상태로 인도','Delivered at Place','도착장소인도','지정 목적지','복합운송'),
(6,'DPU','목적지 터미널','매도인이 지정 목적지에서 양하하여 인도','Delivered at Place Unloaded','도착지양하인도','지정 목적지','복합운송'),
(7,'DDP','매수인 영업장','매도인이 수입통관 및 관세까지 부담','Delivered Duty Paid','관세지급인도','지정 목적지','복합운송'),
(8,'FAS','선적항','매도인이 지정 선적항에서 본선 선측에 물품 인도','Free Alongside Ship','선측인도','선적항 선측','해상운송'),
(9,'FOB','선적항','매도인이 지정 선적항에서 본선에 물품 적재','Free on Board','본선인도','선적항 본선','해상운송'),
(10,'CFR','목적항','매도인이 지정 목적항까지 운임 부담','Cost and Freight','운임포함인도','목적항','해상운송'),
(11,'CIF','목적항','매도인이 운임과 보험료 부담','Cost, Insurance and Freight','운임보험료포함인도','목적항','해상운송');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `items` (`item_id`, `created_at`, `item_category`, `item_code`, `item_hs_code`, `item_name`, `item_name_kr`, `item_pack_unit`, `item_reg_date`, `item_spec`, `item_status`, `item_unit`, `item_unit_price`, `item_weight`, `updated_at`) VALUES (1,'2026-04-06 09:43:32.000000','태양광소재','ITM001','3920.10','EVA Film','EVA 필름','ROLL','2024-01-10','0.38mm/Clear','active','KG',2.50,25.000,'2026-04-06 09:43:32.000000'),
(2,'2026-04-06 09:43:32.000000','태양광소재','ITM002','3920.99','PV Backsheet','PV 백시트','ROLL','2024-01-15','TPT/White/1100mm','active','M2',1.80,15.000,'2026-04-06 09:43:32.000000'),
(3,'2026-04-06 09:43:32.000000','태양광소재','ITM003','8541.40','Solar Cell','태양전지셀','BOX','2024-02-01','Mono PERC/182mm/23.5%','active','EA',0.25,0.010,'2026-04-06 09:43:32.000000'),
(4,'2026-04-06 09:43:32.000000','태양광부품','ITM004','8536.90','Junction Box','정션박스','BOX','2024-02-15','IP68/3 Diodes/MC4','active','EA',3.20,0.250,'2026-04-06 09:43:32.000000'),
(5,'2026-04-06 09:43:32.000000','태양광부품','ITM005','7610.90','Aluminum Frame','알루미늄프레임','PALLET','2024-03-01','Anodized/Silver/35mm','active','SET',8.50,3.500,'2026-04-06 09:43:32.000000'),
(6,'2026-04-06 09:43:32.000000','태양광소재','ITM006','7007.19','Tempered Glass','강화유리','CRATE','2024-03-15','3.2mm/AR Coated/Low Iron','active','EA',12.00,8.000,'2026-04-06 09:43:32.000000'),
(7,'2026-04-06 09:43:32.000000','태양광소재','ITM007','7408.29','PV Ribbon','PV 리본','SPOOL','2024-04-01','Copper/0.23x1.5mm','active','KG',15.00,5.000,'2026-04-06 09:43:32.000000'),
(8,'2026-04-06 09:43:32.000000','접착제','ITM008','3214.10','Silicone Sealant','실리콘실란트','BOX','2024-04-15','RTV/Black/310ml','active','EA',4.50,0.350,'2026-04-06 09:43:32.000000'),
(9,'2026-04-06 09:43:32.000000','태양광부품','ITM009','8536.90','MC4 Connector','MC4 커넥터','BOX','2024-05-01','Male+Female/IP67/30A','active','SET',1.20,0.050,'2026-04-06 09:43:32.000000'),
(10,'2026-04-06 09:43:32.000000','태양광모듈','ITM010','8541.40','Solar Module 400W','태양광모듈 400W','PALLET','2024-05-15','Mono/400W/1722x1134mm','active','EA',120.00,21.000,'2026-04-06 09:43:32.000000'),
(11,'2026-04-06 09:43:32.000000','태양광모듈','ITM011','8541.40','Solar Module 500W','태양광모듈 500W','PALLET','2024-06-01','Mono/500W/2094x1134mm','active','EA',150.00,26.500,'2026-04-06 09:43:32.000000'),
(12,'2026-04-06 09:43:32.000000','태양광모듈','ITM012','8541.40','Solar Module 600W','태양광모듈 600W','PALLET','2024-06-15','Mono/600W/2384x1303mm','active','EA',180.00,32.000,'2026-04-06 09:43:32.000000'),
(13,'2026-04-06 09:43:32.000000','인버터','ITM013','8504.40','Micro Inverter','마이크로인버터','BOX','2024-07-01','400W/Single Phase/WiFi','active','EA',85.00,1.200,'2026-04-06 09:43:32.000000'),
(14,'2026-04-06 09:43:32.000000','인버터','ITM014','8504.40','String Inverter 10kW','스트링인버터 10kW','PALLET','2024-07-15','10kW/Three Phase/MPPT','active','EA',650.00,25.000,'2026-04-06 09:43:32.000000'),
(15,'2026-04-06 09:43:32.000000','구조물','ITM015','7308.90','Mounting Structure','구조물','BUNDLE','2024-08-01','Ground Mount/Galvanized','active','SET',45.00,15.000,'2026-04-06 09:43:32.000000'),
(16,'2026-04-06 09:43:32.000000','케이블','ITM016','8544.49','DC Cable','DC 케이블','DRUM','2024-08-15','PV1-F/4mm2/Black','active','M',0.80,0.055,'2026-04-06 09:43:32.000000'),
(17,'2026-04-06 09:43:32.000000','케이블','ITM017','8544.49','AC Cable','AC 케이블','DRUM','2024-09-01','H07RN-F/3x2.5mm2','active','M',1.50,0.095,'2026-04-06 09:43:32.000000'),
(18,'2026-04-06 09:43:32.000000','전기부품','ITM018','8537.10','Combiner Box','접속함','BOX','2024-09-15','6 String/DC Fuse/SPD','active','EA',55.00,5.000,'2026-04-06 09:43:32.000000'),
(19,'2026-04-06 09:43:32.000000','ESS','ITM019','8507.60','Energy Storage 5kWh','에너지저장장치 5kWh','PALLET','2024-10-01','LFP/5kWh/Wall Mount','inactive','EA',1200.00,45.000,'2026-04-06 09:43:32.000000'),
(20,'2026-04-06 09:43:32.000000','모니터링','ITM020','9031.80','Monitoring System','모니터링시스템','BOX','2024-10-15','WiFi/4G/Cloud Based','inactive','SET',200.00,2.000,'2026-04-06 09:43:32.000000');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `payment_terms` (`payment_term_id`, `payment_term_code`, `payment_term_description`, `payment_term_name`) VALUES (1,'TT','전신환 송금','Telegraphic Transfer'),
(2,'LC','신용장','Letter of Credit'),
(3,'DP','지급인도조건','Documents against Payment'),
(4,'DA','인수인도조건','Documents against Acceptance'),
(5,'CAD','서류상환불','Cash against Documents');
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
INSERT INTO `ports` (`port_id`, `port_city`, `port_code`, `port_name`, `country_id`) VALUES (1,'부산','KRPUS','Port of Busan',2),
(2,'인천','KRINC','Port of Incheon',2),
(3,'광양','KRKAN','Port of Gwangyang',2),
(4,'New York','USNYC','Port of New York',1),
(5,'Los Angeles','USLAX','Port of Los Angeles',1),
(6,'Houston','USHOU','Port of Houston',1),
(7,'Tokyo','JPTYO','Port of Tokyo',3),
(8,'Osaka','JPOSA','Port of Osaka',3),
(9,'Shanghai','CNSHA','Port of Shanghai',4),
(10,'Shenzhen','CNSZX','Port of Shenzhen',4),
(11,'Hamburg','DEHAM','Port of Hamburg',5),
(12,'London','GBLON','Port of London',6),
(13,'Singapore','SGSIN','Port of Singapore',10),
(14,'Ho Chi Minh','VNSGN','Port of Ho Chi Minh',8),
(15,'Dubai','AEJEA','Port of Jebel Ali',13);
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

