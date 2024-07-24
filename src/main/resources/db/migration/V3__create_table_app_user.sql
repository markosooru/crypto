create table app_user (
    id integer not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(20) not null default 'ROLE_USER',
    primary key (id)
);