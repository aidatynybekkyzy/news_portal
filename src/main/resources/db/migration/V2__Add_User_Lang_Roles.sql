insert into roles(ID,role_name)
values
    (nextval('roles_sequence'), 'ADMIN'),
    (nextval('roles_sequence'), 'USER');

insert  into users (id,email,firstname,lastname,password)
values (nextval('users_sequence'),'admin@gmail.com','Limbo','Limbovich','limbo');

insert into user_roles (user_id, role_id)
values (1, 1);

insert into languages (id,code)
values (1,'en'),
       (2,'ru');
