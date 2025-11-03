--
-- Database schema for the FDS Ex 4, the Java Bank Application
--
-- Usually you will not need to execute this file manually. All is done in AbstractOracleXaBank.java.
--

DROP TABLE account;
DROP TABLE customer;

CREATE TABLE customer (
  CustomerNo  INTEGER PRIMARY KEY,
  Surname     VARCHAR2(50),
  FirstName   VARCHAR2(50),
  Nation      VARCHAR2(50),
  DateOfBirth DATE,
  Street      VARCHAR2(50),
  ZIP         VARCHAR2(5),
  City        VARCHAR2(50)
);

CREATE TABLE account (
  IBAN         VARCHAR2(50),
  CustomerNo   INTEGER,
  Balance      NUMBER,
  InterestRate NUMBER,
  CONSTRAINT pk_account PRIMARY KEY (IBAN),
  CONSTRAINT fk_customer FOREIGN KEY (CustomerNo) REFERENCES Customer (CustomerNo),
  CONSTRAINT ck_balance CHECK (Balance >= 0),
  CONSTRAINT ck_full_account CHECK (Balance <= 15000)
);
-- CAUTION: Weird bank - accounts have a maximum capacity!

INSERT INTO customer
VALUES (1, 'Estermann', 'Xaver', 'CH', to_date('1943/05/03', 'yyyy/mm/dd'), 'Bahnhofstrasse 10a', '8000', 'Zurich');
INSERT INTO customer
VALUES (2, 'Martelli', 'Katrin', 'CH', to_date('1983/12/20', 'yyyy/mm/dd'), 'Dolder 6', '8010', 'Zurich');
INSERT INTO customer
VALUES (3, 'Metzler', 'Ruth', 'CH', to_date('1966/07/30', 'yyyy/mm/dd'), 'Bergstrasse 43', '7234', 'Appenzell');
INSERT INTO customer
VALUES (4, 'Deiss', 'Joseph', 'CH', to_date('1975/01/02', 'yyyy/mm/dd'), 'Rue Victoire 34', '1234', 'Fribourg');
INSERT INTO customer
VALUES (5, 'Cotti', 'Flavio', 'CH', to_date('1967/10/13', 'yyyy/mm/dd'), 'Via Grande 55', '3224', 'Lugano');

INSERT INTO account VALUES ('CH5367B1', 1, 8000, 0.01);
INSERT INTO account VALUES ('CH5367B2', 2, 15000, 0.02);
INSERT INTO account VALUES ('CH5367B3', 3, 5000, 0.01);
INSERT INTO account VALUES ('CH5367B4', 4, 1700, 0.02);
INSERT INTO account VALUES ('CH5367B5', 5, 2345, 0.0075);
