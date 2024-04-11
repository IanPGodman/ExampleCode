-- H2
CREATE TABLE vegetable
(
    veg_id int auto_increment NOT NULL PRIMARY KEY,
    display_name VARCHAR(32) NOT NULL,
    image_name VARCHAR(32) NOT NULL
);

CREATE UNIQUE INDEX vegetable_by_name ON vegetable (display_name);

INSERT INTO vegetable (display_name, image_name) VALUES ('Baking potato', 'baking_potato.jpg'),
                        ('Broccoli', 'broccoli.jpg'),
                        ('Carrot', 'carrors_lse.jpg'),
                        ('Cauliflower', 'cauliflower.jpg'),
                        ('Cavlo', 'cavlo_nero.jpg'),
                        ('Coriander', 'coriander.jpg'),
                        ('Courgette', 'courgettes.jpg'),
                        ('Garlic', 'garlic.jpg'),
                        ('Green pepper', 'green_pepper.jpg'),
                        ('Leek', 'leeks_loose.jpg'),
                        ('Onion', 'onion_lse.jpg'),
                        ('Parsnip', 'parsnip_lse.jpg'),
                        ('Red onion', 'red_onion_lse.jpg'),
                        ('Red pepper', 'red_pepper.jpg'),
                        ('Savoy cabbage', 'savoy_cabbage.jpg'),
                        ('Spring onion', 'spring_onions.jpg'),
                        ('Sweet potato', 'sweet_potato.jpg'),
                        ('tomato', 'tomato.jpg'),
                        ('Yellow pepper', 'yellow_pepper.jpg');

