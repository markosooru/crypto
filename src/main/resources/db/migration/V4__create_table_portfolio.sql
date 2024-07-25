create table portfolio (
    id integer not null,
    app_user_id integer,
    amount numeric(38,8) not null,
    date_of_purchase timestamp(6) not null,
    currency varchar(255) not null,
    is_deleted boolean DEFAULT false not null,
    primary key (id)
);