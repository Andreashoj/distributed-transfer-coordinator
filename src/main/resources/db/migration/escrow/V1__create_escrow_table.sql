CREATE TABLE escrow
(
    id      UUID NOT NULL,
    balance DECIMAL,
    CONSTRAINT pk_escrow PRIMARY KEY (id)
);