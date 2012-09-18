# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table items (
  id                        bigint not null,
  title                     varchar(255),
  description               varchar(255),
  price                     float,
  sale_duration             integer,
  created_time              timestamp,
  end_time                  timestamp,
  seller_id                 varchar(255),
  buyer_id                  varchar(255),
  address_line              varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  zipcode                   integer,
  country                   varchar(255),
  contact_email             varchar(255),
  contact_phone             varchar(255),
  sold                      boolean,
  deleted                   boolean,
  constraint pk_items primary key (id))
;

create table item_tags (
  id                        bigint not null,
  item_id                   bigint,
  tag                       varchar(255),
  normalized_tag            varchar(255),
  constraint pk_item_tags primary key (id))
;

create table users (
  email_id                  varchar(255) not null,
  password                  varchar(255),
  screen_name               varchar(255),
  constraint pk_users primary key (email_id))
;

create sequence items_seq;

create sequence item_tags_seq;

create sequence users_seq;

alter table item_tags add constraint fk_item_tags_item_1 foreign key (item_id) references items (id) on delete restrict on update restrict;
create index ix_item_tags_item_1 on item_tags (item_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists items;

drop table if exists item_tags;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists items_seq;

drop sequence if exists item_tags_seq;

drop sequence if exists users_seq;

