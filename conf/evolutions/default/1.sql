# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        varchar(255) not null,
  firstname                 varchar(255),
  lastname                  varchar(255),
  request_token_secret      varchar(255),
  request_token_string      varchar(255),
  verification_key          varchar(255),
  access_token_secret       varchar(255),
  access_token_string       varchar(255),
  constraint pk_user primary key (id))
;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

