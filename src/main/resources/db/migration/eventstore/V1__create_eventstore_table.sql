CREATE TABLE saga_event
(
    id                UUID NOT NULL,
    event_type        SMALLINT,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    payload_escrow_id UUID,
    payload_seller_id UUID,
    payload_amount    DECIMAL,
    CONSTRAINT pk_sagaevent PRIMARY KEY (id)
);