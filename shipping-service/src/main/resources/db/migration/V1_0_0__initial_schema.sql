CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS shipment;

CREATE TABLE shipment (
                          id uuid NOT NULL DEFAULT uuid_generate_v4(),
                          order_id uuid,
                          product_id int,
                          customer_id int,
                          quantity int,
                          status VARCHAR(50),
                          delivery_date TIMESTAMP,
                          CONSTRAINT shipment_pk PRIMARY KEY (id)
);
