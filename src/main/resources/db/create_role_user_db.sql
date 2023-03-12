/*create sequence users_sequence start 1 increment 1;
create sequence roles_sequence start 1 increment 1;
create sequence hibernate_sequence start 1 increment 1;

create table users
(
    id bigserial not null,
    firstname varchar(50) not null,
    lastname varchar(50) not null,
    email varchar(100) not null,
    password varchar(500) not null,
    primary key (id)
);

create table roles
(
    id          bigserial not null,
    roleName    varchar(100),
    primary key (id)
);

create table user_roles
(
    id bigserial not null,
    user_id bigint not null references users(id),
    role_id bigint not null references roles(id),
    primary key (id)
);*/
