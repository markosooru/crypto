create table app_user (
    id integer not null,
    email varchar(255) not null unique,
    username varchar(255) not null,
    primary key (id)
);