DROP TABLE IF EXISTS product;

CREATE TABLE product (
     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     quantity INT NOT NULL,
     amount INT NOT NULL
);

INSERT INTO product (name, quantity, amount) VALUES ('test product-100', 10, 1500000);
INSERT INTO product (name, quantity, amount) VALUES ('test product-200', 20, 800000);
INSERT INTO product (name, quantity, amount) VALUES ('test product-300', 15, 600000);