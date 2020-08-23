CREATE TABLE PAYMENT (
    id SERIAL CONSTRAINT PK_PAYMENT PRIMARY KEY,
    sender VARCHAR(1024) NOT NULL,
    receiver VARCHAR(1024) NOT NULL,
    amount NUMERIC NOT NULL
);