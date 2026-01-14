CREATE TABLE seller
(
    id      UUID NOT NULL,
    balance DECIMAL,
    CONSTRAINT pk_seller PRIMARY KEY (id)
);