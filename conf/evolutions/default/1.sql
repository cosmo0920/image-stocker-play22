# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `images` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`filename` VARCHAR(254) NOT NULL,`description` VARCHAR(254) NOT NULL);
create table `users` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`email` VARCHAR(254) NOT NULL,`password` VARCHAR(254) NOT NULL);

# --- !Downs

drop table `images`;
drop table `users`;

