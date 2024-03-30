# create database cas_server;

use cas_server;

drop table if exists cas_account;

-- 账号
create table cas_account
(
    id           int          not null primary key auto_increment,
    account_name varchar(20)  not null default '' comment '用户名',
    display_name varchar(20)  not null default '' comment '显示名',
    account_id   varchar(20)  not null default '' comment '账号ID',
    password     varchar(255) not null default '' comment '密码',
    mobile       varchar(100) not null default '' comment '手机号码',
    mail         varchar(100) not null default '' comment '邮箱',
    avatar       varchar(255) not null default '' comment '用户头像',
    expired      int          not null default 0 comment '是否过期',
    disabled     int          not null default 0 comment '是否禁用',
    create_time  int          not null default 0 comment '创建时间',
    update_time  int          not null default 0 comment '更新时间'
);

drop table if exists cas_client;
-- 客户端
create table cas_client
(
    id            int          not null primary key auto_increment,
    client_id     varchar(100) not null default '' comment '客户端clientId',
    client_secret varchar(100) not null default '' comment '客户端clientSecret',
    client_name   varchar(50)  not null default '' comment '客户端名称',
    client_type   varchar(10)  not null default '' comment '客户端类型',
    redirect_uri  varchar(255) not null default '' comment '客户端回调地址',
    grant_types   varchar(255) not null default '' comment '授权类型',
    scope         varchar(255) not null default '' comment '授权范围',
    logo_uri      varchar(255) not null default '' comment '客户端logo',
    logout_uri    varchar(255) not null default '' comment '登出uri',
    disabled      int          not null default 0 comment '是否启用',
    create_time   int          not null default 0 comment '创建时间',
    update_time   int          not null default 0 comment '更新时间'
);

drop table if exists cas_consent;
-- oauth2用户同意
create table cas_consent
(
    id          int          not null primary key auto_increment,
    client_id   varchar(100) not null default '' comment '客户端id',
    account_id  varchar(20)  not null default '' comment '账号id',
    create_time int          not null default 0 comment '创建时间',
    expire_time int          not null default 0 comment '到期时间'
);



