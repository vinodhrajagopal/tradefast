CREATE USER 'vinodh'@'localhost' IDENTIFIED BY 'vinodh';

grant all privileges on *.* to 'vinodh'@'localhost' with grant option;

CREATE DATABASE tradefast CHARACTER SET UTF8;

/*
Sample useful scripts
insert into users(email_id,screen_name,password) values('vinodhsamurai@gmail.com','vinodh','secret');
insert into items(id,title,price,end_time,seller_id,city,state,country,zipcode) values(3,'item3',12,current_timestamp,'xdfd','sa','cs','sdk',9008);
*/

create table users(
email_id varchar(254) not null primary key,
screen_name varchar(25) not null unique key,
password varchar(20) not null,
address_line varchar(100) not null,
city varchar(100) not null,
state varchar(100) not null,
country varchar(100) not null,
zipcode decimal(5,0) not null,
currency char(3) not null
) ENGINE=INNODB;


CREATE TABLE `items` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(140) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0',
  `sale_duration` int(11) NOT NULL DEFAULT '24',
  `created_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `seller_id` varchar(254) NOT NULL,
  `buyer_id` varchar(254) DEFAULT NULL,
  `address_line` varchar(100) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `zipcode` decimal(5,0) unsigned NOT NULL,
  `is_free` tinyint(1) NOT NULL DEFAULT '0',
  `sold` tinyint(1) NOT NULL DEFAULT '0',
  `expired` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `seller_id` (`seller_id`),
  KEY `buyer_id` (`buyer_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`email_id`),
  CONSTRAINT `items_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`email_id`)
) ENGINE=InnoDB;

CREATE TABLE `item_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(10) unsigned NOT NULL,
  `tag` varchar(20) NOT NULL,
  `normalized_tag` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_tags_fk` (`item_id`),
  CONSTRAINT `item_tags_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE EVENT mark_expired_posts ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP
COMMENT 'Mark the posts which have expired'
DO UPDATE tradefast.items SET expired=true WHERE end_time < now() AND expired=false;
