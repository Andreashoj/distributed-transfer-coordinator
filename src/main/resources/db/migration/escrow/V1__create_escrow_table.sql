CREATE TABLE escrow
(
    id       UUID NOT NULL,
    amount   DECIMAL,
    buyer_id UUID,
    status   VARCHAR(255),
    CONSTRAINT pk_escrow PRIMARY KEY (id)
);

