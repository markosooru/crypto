alter table if exists portfolio
    add constraint app_user_id_fk
    foreign key (app_user_id)
    references app_user;