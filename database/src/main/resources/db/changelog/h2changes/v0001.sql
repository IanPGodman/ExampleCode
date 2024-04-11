-- H2
-- changeset iango:create-user-table
CREATE TABLE app_user
(
    user_id long auto_increment NOT NULL PRIMARY KEY,
    user_name VARCHAR(16) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    enabled CHAR(1) NOT NULL DEFAULT 'Y',
    created_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO app_user (user_name, password, email) VALUES ('Admin', '$2a$10$WM83GXfb71KfOMT3TVxitu5vanaNvYauTFFMO7g3ASBq1mlRiXtvi', 'admin@user.net');
INSERT INTO app_user (user_name, password, email) VALUES ('Test', '$2a$10$WM83GXfb71KfOMT3TVxitu5vanaNvYauTFFMO7g3ASBq1mlRiXtvi', 'test@user.net');
INSERT INTO app_user (user_name, password, email) VALUES ('Another User', '$2a$10$WM83GXfb71KfOMT3TVxitu5vanaNvYauTFFMO7g3ASBq1mlRiXtvi', 'another@user.net');
