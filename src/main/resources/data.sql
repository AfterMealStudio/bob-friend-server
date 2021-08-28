
# INSERT INTO member (member_id,USERNAME, PASSWORD, email, ACTIVE) VALUES (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1);
#
# INSERT INTO authority (AUTHORITY_NAME) values ('ROLE_USER');
# INSERT INTO authority (AUTHORITY_NAME) values ('ROLE_ADMIN');
#
# INSERT INTO member_authority (member_ID, AUTHORITY_NAME) values (1, 'ROLE_USER');
# INSERT INTO member_authority (member_ID, AUTHORITY_NAME) values (1, 'ROLE_ADMIN')