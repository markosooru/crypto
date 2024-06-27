create table portfolio (
    id integer not null,
    amount numeric(38,2) not null,
    app_user_id integer,
    date_of_purchase timestamp(6) not null,
    currency varchar(255) not null,
    deleted boolean not null,
    primary key (id)
);