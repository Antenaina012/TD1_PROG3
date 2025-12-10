CREATE TABLE IF NOT EXISTS Product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS Product_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
        REFERENCES Product(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_product_name ON Product (name);
CREATE INDEX IF NOT EXISTS idx_category_name ON Product_category (name);