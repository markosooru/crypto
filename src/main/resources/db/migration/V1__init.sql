create sequence app_user_seq start with 1 increment by 50;
create sequence portfolio_seq start with 1 increment by 50;

create table app_user (
    id integer not null,
    email varchar(255) not null unique,
    username varchar(255) not null,
    primary key (id)
);

create table portfolio (
    id integer not null,
    amount numeric(38,2) not null,
    app_user_id integer,
    date_of_purchase timestamp(6) not null,
    currency varchar(255) not null,
    primary key (id)
);

alter table if exists portfolio
    add constraint app_user_id_fk
    foreign key (app_user_id)
    references app_user;
