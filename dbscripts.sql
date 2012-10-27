/* script for mysql*/
CREATE USER 'vinodh'@'localhost' IDENTIFIED BY 'vinodh';

grant all privileges on *.* to 'vinodh'@'localhost' with grant option;

CREATE DATABASE tradefast CHARACTER SET UTF8;

/*
Sample useful scripts
insert into users values('vinodhsamurai','secret','vinodh@gmail.com','555 E Washington Ave','Sunnyvale','CA','USA',94086,'USD');
insert into items(id,title,price,end_time,created_by,city,state,country,zipcode) values(3,'item3',12,current_timestamp,'xdfd','sa','cs','sdk',9008);
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
  `created_by` varchar(254) NOT NULL,
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
  KEY `created_by` (`created_by`),
  KEY `buyer_id` (`buyer_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_name`),
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


/* postgres installation 
 * 
 * default user/pwd - postgres/vinodh 
 * 
 */

CREATE USER vinodh WITH createdb PASSWORD 'vinodh';

CREATE TABLE users (
  user_name varchar(20) NOT NULL PRIMARY KEY,
  password varchar(20) DEFAULT NULL,
  email_id varchar(254) NOT NULL UNIQUE,
  picture varchar(300) DEFAULT NULL,
  currency char(3) DEFAULT NULL,
  locale varchar(20) DEFAULT NULL,
  address_line varchar(100) DEFAULT NULL,
  city varchar(100) NOT NULL,
  state varchar(100) NOT NULL,
  country varchar(100) NOT NULL,
  zipcode varchar(10) NOT NULL
);


CREATE TABLE posts (
  id bigserial NOT NULL PRIMARY KEY,
  title varchar(140) NOT NULL,
  description varchar(1000) DEFAULT NULL,
  price decimal(15,2) NOT NULL DEFAULT 0.00 CHECK(price >= 0),
  post_duration smallint NOT NULL DEFAULT 24 CHECK(post_duration > 0),
  created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
  end_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP + interval '1 day',
  created_by varchar(254) NOT NULL REFERENCES users(user_name) ON DELETE CASCADE ON UPDATE CASCADE,
  buyer_id varchar(254) DEFAULT NULL REFERENCES users(user_name) ON DELETE CASCADE ON UPDATE CASCADE,
  address_line varchar(100) DEFAULT NULL,
  city varchar(100) NOT NULL,
  state varchar(100) NOT NULL,
  country varchar(100) NOT NULL,
  zipcode varchar(10) NOT NULL,
  is_free BOOLEAN NOT NULL DEFAULT FALSE,
  sold BOOLEAN NOT NULL DEFAULT FALSE,
  expired BOOLEAN NOT NULL DEFAULT FALSE,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE post_tags (
  id bigserial NOT NULL PRIMARY KEY,
  post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE,
  tag varchar(20) NOT NULL,
  normalized_tag varchar(20) NOT NULL CHECK(normalized_tag=lower(tag)),
  UNIQUE (post_id, normalized_tag)
);

CREATE TABLE message_threads (
	id bigserial NOT NULL PRIMARY KEY,
	post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE,
	created_by varchar(254) NOT NULL REFERENCES users(user_name) ON DELETE CASCADE ON UPDATE CASCADE,
	UNIQUE (post_id, created_by)
);


CREATE TABLE messages (
	id bigserial NOT NULL PRIMARY KEY,
	thread_id bigint NOT NULL REFERENCES message_threads(id) ON DELETE CASCADE ON UPDATE CASCADE,
	message_from varchar(254) NOT NULL REFERENCES users(user_name) ON DELETE CASCADE ON UPDATE CASCADE,
	body varchar(250) NOT NULL,
	created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);



/*
insert into users values('vinodh','secret','vinodh@gmail.com','/pic','USD','English','555 E Washington Ave','Sunnyvale','CA','USA','94086');
insert into users values('surya','secret','surya@gmail.com',null,'USD','English','555 E Washington Ave','Sunnyvale','CA','USA','94086');

insert into message_threads(post_id,created_by) values(1,'surya');

insert into messages(thread_id,message_from,body) values(1,'vinodh','message2');
/*