CREATE USER 'vinodh'@'localhost' IDENTIFIED BY 'vinodh';

grant all privileges on *.* to 'vinodh'@'localhost' with grant option;

CREATE DATABASE tradefast CHARACTER SET UTF8;

/*
Sample useful scripts
insert into users values('vinodhsamurai','secret','vinodhsamurai@gmail.com','555 E Washington Ave','Sunnyvale','CA','USA',94086,'USD');
insert into items(id,title,price,end_time,seller_id,city,state,country,zipcode) values(3,'item3',12,current_timestamp,'xdfd','sa','cs','sdk',9008);
*/

CREATE TABLE `users` (
  `user_name` varchar(20) NOT NULL,
  `password` varchar(20) DEFAULT NULL,
  `email_id` varchar(254) NOT NULL,
  `picture` varchar(300) DEFAULT NULL,
  `currency` char(3) DEFAULT NULL,
  `language` varchar(20) DEFAULT NULL,
  `address_line` varchar(100) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `zipcode` varchar(10) NOT NULL,
  PRIMARY KEY (`user_name`),
  UNIQUE KEY `email_id` (`email_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8


CREATE TABLE `items` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(140) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `sale_duration` int(11) NOT NULL DEFAULT '24',
  `created_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `seller_id` varchar(254) NOT NULL,
  `buyer_id` varchar(254) DEFAULT NULL,
  `address_line` varchar(100) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `zipcode` varchar(10) NOT NULL,
  `is_free` tinyint(1) NOT NULL DEFAULT '0',
  `sold` tinyint(1) NOT NULL DEFAULT '0',
  `expired` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `seller_id` (`seller_id`),
  KEY `buyer_id` (`buyer_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_name`),
  CONSTRAINT `items_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8

CREATE TABLE `item_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(10) unsigned NOT NULL,
  `tag` varchar(20) NOT NULL,
  `normalized_tag` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_tags_fk` (`item_id`),
  CONSTRAINT `item_tags_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

CREATE EVENT mark_expired_posts ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP
COMMENT 'Mark the posts which have expired'
DO UPDATE tradefast.items SET expired=true WHERE end_time < now() AND expired=false;
