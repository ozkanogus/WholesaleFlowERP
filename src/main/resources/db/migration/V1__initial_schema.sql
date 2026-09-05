CREATE SEQUENCE sequence_generator START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE grocery (
    id bigint NOT NULL, created_date timestamp without time zone,
    last_modified_date timestamp without time zone, name character varying(255) NOT NULL,
    CONSTRAINT grocery_pkey PRIMARY KEY (id)
);

CREATE TABLE product (
    id bigint NOT NULL, name character varying(255) NOT NULL, unit character varying(255) NOT NULL,
    CONSTRAINT product_pkey PRIMARY KEY (id),
    CONSTRAINT uk_jmivyxk9rmgysrmsqw15lqr5b UNIQUE (name)
);

CREATE TABLE purchase (
    id bigint NOT NULL, created_date timestamp without time zone,
    last_modified_date timestamp without time zone, grocery_id bigint NOT NULL,
    CONSTRAINT purchase_pkey PRIMARY KEY (id),
    CONSTRAINT fkge22gwyikp4whxo2rihkw2kwd FOREIGN KEY (grocery_id) REFERENCES grocery(id)
);

CREATE TABLE sale (
    id bigint NOT NULL, created_date timestamp without time zone,
    last_modified_date timestamp without time zone, grocery_id bigint NOT NULL,
    CONSTRAINT sale_pkey PRIMARY KEY (id),
    CONSTRAINT fka0ro43sfrcnfuochdurtr4j80 FOREIGN KEY (grocery_id) REFERENCES grocery(id)
);

CREATE TABLE purchase_product (
    count numeric(21,2) NOT NULL, price numeric(21,2) NOT NULL,
    purchase_id bigint NOT NULL, product_id bigint NOT NULL,
    CONSTRAINT purchase_product_pkey PRIMARY KEY (product_id, purchase_id),
    CONSTRAINT fk1te3j5efipmc5c19wve8c90qd FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    CONSTRAINT fkl1da8u1v57wry7sunkkgmjr8o FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE sale_product (
    count numeric(21,2) NOT NULL, price numeric(21,2) NOT NULL,
    sale_id bigint NOT NULL, product_id bigint NOT NULL,
    CONSTRAINT sale_product_pkey PRIMARY KEY (product_id, sale_id),
    CONSTRAINT fk4dtibi1vwxkx8gjs59nhp0cnq FOREIGN KEY (sale_id) REFERENCES sale(id),
    CONSTRAINT fkrtwiisrmdqeslt86pacdwwn1o FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE stock_movement (
    id bigint NOT NULL, created_date timestamp without time zone,
    last_modified_date timestamp without time zone, count numeric(21,2) NOT NULL,
    operation_id bigint NOT NULL, operation_type character varying(255) NOT NULL,
    grocery_id bigint NOT NULL, product_id bigint NOT NULL,
    CONSTRAINT stock_movement_pkey PRIMARY KEY (id),
    CONSTRAINT fk71my9icrb0nmmfekp3qp7p74r FOREIGN KEY (grocery_id) REFERENCES grocery(id),
    CONSTRAINT fkq63e7y5l2pnh2tt2lvxlquvbf FOREIGN KEY (product_id) REFERENCES product(id)
);
