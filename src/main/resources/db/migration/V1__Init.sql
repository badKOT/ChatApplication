create table if not exists accounts
(
    id           bigserial
        primary key,
    password     varchar(255)                                  not null,
    phone_number varchar(11)                                   not null,
    username     varchar(255)                                  not null
        constraint uk_k8h1bgqoplx0rkngj01pm1rgp
            unique,
    role         varchar(50) default 'user'::character varying not null
);

alter table accounts
    owner to postgres;

create table if not exists chats
(
    id    bigserial
        primary key,
    title varchar(255) not null
);

alter table chats
    owner to postgres;

create table if not exists accounts_chats
(
    chat_id    bigint not null
        constraint fkplqtek4lu992daogi87hrc7f7
            references chats,
    account_id bigint not null
        constraint fkhj9whghif2p4ifrw9ygy5y0cv
            references accounts,
    constraint uk91frcjv9lqtgl0wdivcmly92g
        unique (chat_id, account_id)
);

alter table accounts_chats
    owner to postgres;

create table if not exists messages
(
    id         bigserial
        primary key,
    sent       timestamp(6)                                   not null,
    chat_id    bigint
        constraint fk64w44ngcpqp99ptcb9werdfmb
            references chats,
    account_id bigint
        constraint fk57yqwf1pagxwrq7l2na31ie9
            references accounts,
    content    varchar(255),
    type       varchar(255) default 'CHAT'::character varying not null
        constraint messages_type_check
            check ((type)::text = ANY
                   ((ARRAY ['CHAT'::character varying, 'JOIN'::character varying, 'LEAVE'::character varying])::text[]))
);

alter table messages
    owner to postgres;
