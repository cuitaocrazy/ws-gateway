create table if not exists org
(
    id varchar(30) primary key,
    name varchar(30)
);

create table if not exists user
(
    id varchar primary key,
    pwd varchar
);