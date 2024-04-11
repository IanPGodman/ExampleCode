-- H2
CREATE TABLE role
(
    id int auto_increment NOT NULL,
    role_name VARCHAR(12) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

INSERT INTO role (role_name) values ('ROLE_USER'), ('ROLE_ADMIN');

CREATE TABLE app_user_roles
(
    roles_id     BIGINT,
    user_user_id BIGINT
);

-- changeset iango:23-3-2024-1
INSERT INTO app_user_roles (user_user_id) SELECT user_id FROM app_user ;
UPDATE app_user_roles SET roles_id = (SELECT id FROM role WHERE role_name = 'ROLE_USER');
INSERT INTO app_user_roles (user_user_id, roles_id) VALUES ((SELECT user_id FROM app_user WHERE user_name = 'Admin'),
                                                            (SELECT id FROM role WHERE role_name = 'ROLE_ADMIN')) ;


