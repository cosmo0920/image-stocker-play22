# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `images` (`id` INTEGER AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`filename` VARCHAR(1024) NOT NULL, `description` TEXT NOT NULL);

# --- !Downs

drop table `images`;
