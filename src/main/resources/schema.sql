CREATE TABLE product (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     price DOUBLE NOT NULL,
     stock INT NOT NULL,
     version INT NOT NULL
);
