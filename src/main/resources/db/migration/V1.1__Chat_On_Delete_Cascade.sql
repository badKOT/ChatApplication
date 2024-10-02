BEGIN;

ALTER TABLE messages
DROP CONSTRAINT fk64w44ngcpqp99ptcb9werdfmb;

ALTER TABLE accounts_chats
DROP CONSTRAINT fkplqtek4lu992daogi87hrc7f7;

COMMIT;

BEGIN;

ALTER TABLE messages
ADD CONSTRAINT constraint_name_1
FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE;

ALTER TABLE accounts_chats
ADD CONSTRAINT constraint_name_2
FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE;

COMMIT;
