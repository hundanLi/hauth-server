use auth_server;

create table auth_user
(
    id       int          not null primary key auto_increment,
    user_id  varchar(50)  not null default '',
    username varchar(50)  not null default '',
    password varchar(100) not null default '',
    mobile   varchar(50)  not null default ''
);