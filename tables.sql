CREATE TABLE restaurants (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(255) NOT NULL,
    adresse VARCHAR2(255) NOT NULL,
    latitude NUMBER(10, 8) NOT NULL,
    longitude NUMBER(11, 8) NOT NULL
);

CREATE SEQUENCE seq_restaurants START WITH 1 INCREMENT BY 1;

CREATE TABLE reservations (
    id NUMBER PRIMARY KEY,
    restaurant_id NUMBER NOT NULL,
    nom VARCHAR2(100) NOT NULL,
    prenom VARCHAR2(100) NOT NULL,
    nb_convives NUMBER NOT NULL,
    telephone VARCHAR2(20) NOT NULL,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE SEQUENCE seq_reservations START WITH 1 INCREMENT BY 1;