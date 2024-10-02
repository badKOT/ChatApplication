insert into chats (title) values ('Клуб мазохистов');
insert into chats (title) values ('Клуб трактористов');
insert into chats (title) values ('Клуб мотоциклистов');

insert into accounts (password, phone_number, username, role) values ('$2a$10$rFIuet3JMtAr0W2jAY.Qw.DDSbYYFg7N7h09Ced7Ac77gVdJS4hvS', '12345678910', 'first', 'user');
insert into accounts (password, phone_number, username, role) values ('$2a$10$EJRGT7b0bJJhXaioZFYDvecpdxrmFKamVJU3AkqFxRCSZDVJJp4TS', '12345678910', 'second', 'user');
insert into accounts (password, phone_number, username, role) values ('$2a$10$stSvYzdbpBaSMTLGqb6u/.TyVP8JYIhzp2Yu8msu/eWThNeG6dFkq', '12345678910', 'third', 'user');

insert into accounts_chats (chat_id, account_id) values (1, 1);
insert into accounts_chats (chat_id, account_id) values (2, 1);
insert into accounts_chats (chat_id, account_id) values (3, 1);
