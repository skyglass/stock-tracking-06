CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS customer_payment;

CREATE TABLE customer (
   id uuid NOT NULL DEFAULT uuid_generate_v4(),
   name VARCHAR(50) NOT NULL,
   balance int,
   CONSTRAINT customer_pk PRIMARY KEY (id)
);

CREATE TABLE customer_payment (
   payment_id uuid NOT NULL DEFAULT uuid_generate_v4(),
   customer_id uuid NOT NULL,
   order_id uuid,
   status VARCHAR(50),
   amount int,
   CONSTRAINT customer_payment_pk PRIMARY KEY (payment_id),
   CONSTRAINT customer_payment_fk FOREIGN KEY (customer_id) REFERENCES customer(id)
);

insert into customer(name, balance)
    values
        ('sam', 100),
        ('mike', 100),
        ('john', 100);