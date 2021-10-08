CREATE SCHEMA itmo AUTHORIZATION postgres;

CREATE TYPE itmo.coordinates AS (x float4, y float4);

CREATE TABLE itmo.color
(
    id     serial4 NOT NULL,
    "name" varchar NOT NULL,
    CONSTRAINT color_pk PRIMARY KEY (id)
);

CREATE TABLE itmo.country
(
    id     serial4 NOT NULL,
    "name" varchar NOT NULL,
    CONSTRAINT country_pk PRIMARY KEY (id)
);

CREATE TABLE itmo.unit
(
    id     serial4 NOT NULL,
    "name" varchar NOT NULL,
    CONSTRAINT unit_pk PRIMARY KEY (id)
);

CREATE TABLE itmo.users
(
    "nickname"     varchar NOT NULL,
    "password" varchar NULL,
    CONSTRAINT users_pk PRIMARY KEY ("nickname")
);


CREATE TABLE itmo.person
(
    id          serial4 NOT NULL,
    "name"      varchar NULL,
    height      int4    NOT NULL,
    eye_color   int4    NOT NULL,
    hair_color  int4    NOT NULL,
    nationality int4    NOT NULL,
    CONSTRAINT person_pk PRIMARY KEY (id),
    CONSTRAINT person_fk_eye_color FOREIGN KEY (eye_color) REFERENCES itmo.color (id),
    CONSTRAINT person_fk_hair_color FOREIGN KEY (hair_color) REFERENCES itmo.color (id),
    CONSTRAINT person_fk_nationality FOREIGN KEY (nationality) REFERENCES itmo.country (id)
);

CREATE TABLE itmo.product
(
    id                serial4          NOT NULL,
    "name"            varchar          NOT NULL,
    coordinates       itmo.coordinates NOT NULL,
    creation_time     timestamptz      NOT NULL DEFAULT now(),
    price             int4 NULL,
    part_number       varchar          NOT NULL,
    manufacturer_cost float4 NULL,
    unit_of_measure   int4             NOT NULL,
    owner_id          int4 NULL,
    user_nickname	  varchar          NOT NULL, 
    CONSTRAINT product_check CHECK ((price > 0)),
    CONSTRAINT product_pk PRIMARY KEY (id),
    CONSTRAINT product_fk_person FOREIGN KEY (owner_id) REFERENCES itmo.person (id),
    CONSTRAINT product_fk_unit FOREIGN KEY (unit_of_measure) REFERENCES itmo.unit (id),
    CONSTRAINT product_fk_user FOREIGN KEY (user_nickname) references itmo.users ("nickname")
);

INSERT INTO itmo.color ("name")
VALUES ('BLUE'),
       ('GREEN'),
       ('BLACK'),
       ('ORANGE'),
       ('WHITE'),
       ('BROWN');

INSERT INTO itmo.country ("name")
VALUES ('GERMANY'),
       ('THAILAND'),
       ('JAPAN');

INSERT INTO itmo.person ("name", height, eye_color, hair_color, nationality)
VALUES ('Ivan Androsov', 183, 6, 6, 1);
INSERT INTO itmo.person ("name", height, eye_color, hair_color, nationality)
VALUES ('Ivan Androsov', 183, 6, 6, 1);

insert into itmo.users ("nickname", "password")
values ('309629', '123456789');

INSERT INTO itmo.unit ("name")
VALUES ('KILOGRAMS'),
       ('SQUARE_METERS'),
       ('GRAMS');

INSERT INTO itmo.product ("name", coordinates, creation_time, price, part_number, manufacturer_cost, unit_of_measure,
                          owner_id, user_nickname)
VALUES ('Razer Mouse', '(100,100)'::"itmo"."coordinates", '2021-09-20 17:10:05.094591+00', 10000, '1', 10.0, 3, 1, '309629'),
       ('Gamer Girl Bathwater', '(100,100)'::"itmo"."coordinates", '2021-09-20 17:10:33.442662+00', 99999, '2', 0.0, 3,
        2, '309629');