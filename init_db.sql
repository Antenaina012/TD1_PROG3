CREATE USER product_manager_user WITH PASSWORD '123456';

CREATE DATABASE product_management_db OWNER product_manager_user;

GRANT ALL PRIVILEGES ON DATABASE product_management_db TO product_manager_user;

ALTER DATABASE product_management_db OWNER TO product_manager_user;