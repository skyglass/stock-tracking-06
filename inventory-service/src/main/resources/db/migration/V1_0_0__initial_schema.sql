CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS order_inventory;

CREATE TABLE product (
                         id SERIAL NOT NULL,
                         description VARCHAR(50),
                         available_quantity int,
                         CONSTRAINT product_pk PRIMARY KEY (id)
);

CREATE TABLE order_inventory (
                                 inventory_id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                 order_id uuid,
                                 product_id int,
                                 status VARCHAR(50),
                                 quantity int,
                                 CONSTRAINT inventory_pk PRIMARY KEY (inventory_id),
                                 CONSTRAINT inventory_fk FOREIGN KEY (product_id) REFERENCES product(id)
);

insert into product(description, available_quantity)
values
    ('book', 10),
    ('pen', 10),
    ('rug', 10);