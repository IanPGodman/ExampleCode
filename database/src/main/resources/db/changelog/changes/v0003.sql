-- Postgres
CREATE TABLE role
(
    id SERIAL NOT NULL,
    role_name VARCHAR(12) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

INSERT INTO role (role_name) values ('ROLE_USER'), ('ROLE_ADMIN');

CREATE TABLE app_user_roles
(
    roles_id     BIGINT,
    user_user_id BIGINT
);

INSERT INTO app_user_roles (user_user_id) SELECT user_id FROM app_user ;
UPDATE app_user_roles SET roles_id = (SELECT id FROM role WHERE role_name = 'ROLE_USER');
INSERT INTO app_user_roles (user_user_id, roles_id) VALUES ((SELECT user_id FROM app_user WHERE user_name = 'Admin'),
                                                            (SELECT id FROM role WHERE role_name = 'ROLE_ADMIN'));

-- changeset iango:23-3-2024-2
ALTER TABLE app_user_roles ALTER COLUMN roles_id SET NOT NULL;
ALTER TABLE app_user_roles ALTER COLUMN user_user_id SET NOT NULL;

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuserol_on_role FOREIGN KEY (roles_id) REFERENCES role (id);


ALTER TABLE app_user_roles
    ADD CONSTRAINT pk_app_user_roles PRIMARY KEY (roles_id, user_user_id);

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuserol_on_user FOREIGN KEY (user_user_id) REFERENCES app_user (user_id);


