create schema auth;
create sequence auth.sq_dat_user;

alter sequence auth.sq_dat_user owner to testapp;

create sequence auth.sq_dat_privilege;

alter sequence auth.sq_dat_privilege owner to testapp;

create sequence auth.sq_dat_role;

alter sequence auth.sq_dat_role owner to testapp;

create table auth.dat_user
(
    user_id    bigint not null
        primary key,
    user_login varchar(255) not null,
    user_name  varchar(255) not null,
    user_password varchar(255)
);

alter table auth.dat_user
    owner to testapp;

create table auth.dat_privilege
(
    privilege_id          bigint not null
        primary key,
    privilege_description varchar(255),
    privilege_name        varchar(255)
);

alter table auth.dat_privilege
    owner to testapp;

create table auth.dat_role
(
    role_id          bigint not null
        primary key,
    role_description varchar(255),
    role_name        varchar(255)
);

comment on column auth.dat_role.role_id is 'Идентификатор роли';

comment on column auth.dat_role.role_description is 'Описание роли';

comment on column auth.dat_role.role_name is 'Название роли';


alter table auth.dat_role
    owner to testapp;

create table auth.dat_roles_privileges
(
    role_id      bigint not null
        constraint dat_roles_privileges_dat_role_role_id_fk
            references auth.dat_role,
    privilege_id bigint not null
        constraint dat_roles_privileges_dat_privilege_privilege_id_fk
            references auth.dat_privilege,
    constraint dat_roles_privileges_pk
        primary key (role_id, privilege_id)
);

alter table auth.dat_roles_privileges
    owner to testapp;

create index dat_roles_privileges_role_id_index
    on auth.dat_roles_privileges (role_id);

create index dat_roles_privileges_privilege_id_index
    on auth.dat_roles_privileges (privilege_id);

create table auth.dat_users_roles
(
    user_id bigint not null
        constraint dat_users_roles_dat_user_user_id_fk
            references auth.dat_user,
    role_id bigint not null
        constraint dat_users_roles_dat_role_role_id_fk
            references auth.dat_role
);

alter table auth.dat_users_roles
    owner to testapp;

create index dat_users_roles_role_id_user_id_index
    on auth.dat_users_roles (role_id, user_id);

insert into auth.dat_user (
    user_id, user_login, user_name)
values (nextval('auth.sq_dat_user'), 'test', 'Тестовый пользователь');

insert into auth.dat_role
(role_id, role_name, role_description)
values (nextval('auth.sq_dat_role'), 'ADMIN', 'Администратор системы');

insert into auth.dat_users_roles
(user_id, role_id)
select (select user_id from auth.dat_user where user_login = 'test'), (select role_id from auth.dat_role where role_name = 'ADMIN');