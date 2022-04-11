BEGIN TRANSACTION;

DROP TABLE IF EXISTS pothole;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

CREATE TABLE pothole (
	pothole_id serial,
	date_reported date NOT NULL,
	latitude decimal(10,6) NOT NULL,
	longitude decimal(10,6) NOT NULL,
	image_location varchar(256),
	cross_street_1 varchar(256) NOT NULL,
	cross_street_2 varchar(256) NOT NULL,
	contact_name varchar(128) NOT NULL,
	contact_email varchar(128) NOT NULL,
	contact_phone varchar(15) NOT NULL,
	CONSTRAINT pk_pothole PRIMARY KEY (pothole_id)
);

CREATE TABLE users (
	user_id int DEFAULT nextval('seq_user_id'::regclass) NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

INSERT INTO users (username,password_hash,role) VALUES ('user','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_USER');
INSERT INTO users (username,password_hash,role) VALUES ('admin','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_ADMIN');

INSERT INTO pothole (date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone) VALUES ('2022-04-09', 39.938010, -82.992751, 'https://ik.imagekit.io/q6zdnk39roo/ian-taylor-cZokzzlC3KM-unsplash_UPdf2JpA8.jpg?ik-sdk-version=javascript-1.4.3&updatedAt=1649534868646', '4th St', 'E Gates St', 'John Doe', 'john_doe@aol.com', '555-555-5555');

INSERT INTO pothole (date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone) VALUES ('2022-04-09', 40.061212, -83.100036, 'https://ik.imagekit.io/q6zdnk39roo/matt-hoffman--OOiAy2lBZc-unsplash__1__VPw8VC-c7.jpg?ik-sdk-version=javascript-1.4.3&updatedAt=1649534868036', 'Henderson Rd', 'Horizons Dr', 'Jane Doe', 'jane_doe@gmail.com', '555-555-5555');

INSERT INTO pothole (date_reported, latitude, longitude, image_location, cross_street_1, cross_street_2, contact_name, contact_email, contact_phone) VALUES ('2022-04-09', 39.987221, -83.016627, 'https://ik.imagekit.io/q6zdnk39roo/liam-riby-j8h2_9UDqrM-unsplash_bAGSkwa3F.jpg?ik-sdk-version=javascript-1.4.3&updatedAt=1649534861081', 'Michigan Ave', '5th Ave', 'Smithers Smith', 'smither.smith@burnspower.com', '555-555-5555');

COMMIT TRANSACTION;
