DROP TABLE PURCHASE;
DROP TABLE PURCHASE_STATUS;
DROP TABLE PURCHASE_TYPE;
DROP TABLE JOB;
DROP TABLE CATEGORY;
DROP TABLE USER_ACCOUNT;
DROP TABLE USER_ROLE;

CREATE TABLE USER_ROLE(
ID BIGSERIAL PRIMARY KEY,
ROLE_TYPE VARCHAR(255) NOT NULL
);

CREATE TABLE USER_ACCOUNT(
ID BIGSERIAL PRIMARY KEY,
USERNAME VARCHAR(100) NOT NULL,
EMAIL VARCHAR(100) NOT NULL,
PASSWORD VARCHAR(255) NOT NULL,
RATING DOUBLE PRECISION NOT NULL DEFAULT 0.00,
BALANCE DOUBLE PRECISION NOT NULL DEFAULT 0.00,
ROLE_ID BIGINT REFERENCES USER_ROLE(ID)
);

CREATE TABLE CATEGORY(
ID BIGSERIAL PRIMARY KEY,
NAME VARCHAR(150) NOT NULL
);

CREATE TABLE JOB(
ID BIGSERIAL PRIMARY KEY,
NAME VARCHAR(255) NOT NULL,
PRICE DOUBLE PRECISION NOT NULL,
DAYS_UNTIL_DELIVERY INTEGER NOT NULL,
RATING DOUBLE PRECISION NOT NULL DEFAULT 0.00,
DESCRIPTION VARCHAR(255) NOT NULL,
CATEGORY_ID BIGINT REFERENCES CATEGORY(ID),
SELLER_ID BIGINT REFERENCES USER_ACCOUNT(ID)
);

CREATE TABLE PURCHASE_TYPE(
ID BIGSERIAL PRIMARY KEY,
CUSTOMER_TYPE VARCHAR(255) NOT NULL
);

CREATE TABLE PURCHASE_STATUS(
ID BIGSERIAL PRIMARY KEY,
STATUS VARCHAR(255) NOT NULL
);

CREATE TABLE PURCHASE(
ID BIGSERIAL PRIMARY KEY,
NAME VARCHAR(255) NOT NULL,
PRICE DOUBLE PRECISION NOT NULL,
DAYS_UNTIL_DELIVERY INTEGER NOT NULL,
CREATION_DATE DATE NOT NULL,
DESCRIPTION VARCHAR(255),
RATING INTEGER NOT NULL DEFAULT 0,
TYPE_ID BIGINT REFERENCES PURCHASE_TYPE(ID),
STATUS_ID BIGINT REFERENCES PURCHASE_STATUS(ID),
JOB_ID BIGINT REFERENCES JOB(ID),
CUSTOMER_ID BIGINT REFERENCES USER_ACCOUNT(ID)
);

INSERT INTO USER_ROLE(ROLE_TYPE) VALUES ('ROLE_SELLER');
INSERT INTO USER_ROLE(ROLE_TYPE) VALUES ('ROLE_BUYER');

INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('IN_PROGRESS');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('LATE');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('FINISHED');
INSERT INTO PURCHASE_STATUS(STATUS) VALUES ('CANCELED');

INSERT INTO CATEGORY (NAME) VALUES ('Business');
INSERT INTO CATEGORY (NAME) VALUES ('Design & Graphics');
INSERT INTO CATEGORY (NAME) VALUES ('Digital Marketing');
INSERT INTO CATEGORY (NAME) VALUES ('Lifestyle');
INSERT INTO CATEGORY (NAME) VALUES ('Music & Audio');
INSERT INTO CATEGORY (NAME) VALUES ('Programming');
INSERT INTO CATEGORY (NAME) VALUES ('Video & Animation');
INSERT INTO CATEGORY (NAME) VALUES ('Writing & Translation');
