CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS order_workflow_action;
DROP TABLE IF EXISTS purchase_order;

CREATE TABLE purchase_order (
                                order_id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                customer_id int,
                                product_id int,
                                quantity int,
                                unit_price int,
                                amount int,
                                status VARCHAR(100),
                                delivery_date TIMESTAMP,
                                CONSTRAINT order_pk PRIMARY KEY (order_id)
);

CREATE TABLE order_workflow_action (
                                       id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                       order_id uuid,
                                       action VARCHAR(100),
                                       created_at TIMESTAMP,
                                       CONSTRAINT workflow_action_pk PRIMARY KEY (id),
                                       CONSTRAINT workflow_action_fk FOREIGN KEY (order_id) REFERENCES purchase_order(order_id)
);
