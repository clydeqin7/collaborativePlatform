create table user
(
    id                 int auto_increment
        primary key,
    username           varchar(20)  null,
    encrypted_password varchar(100) null,
    avatar             varchar(100) null,
    created_at         datetime     null,
    updated_at         datetime     null,
    constraint user_pk
        unique (username)
);