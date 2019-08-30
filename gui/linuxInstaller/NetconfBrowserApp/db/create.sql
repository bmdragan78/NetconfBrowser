/*CREATE DATABASE uidata IF NOT EXISTS;*/

/*DROP TABLE IF EXISTS DEVICES;*/

CREATE TABLE IF NOT EXISTS DEVICES (
    id IDENTITY PRIMARY KEY,
    
    name varchar(255) NOT NULL,
    UNIQUE KEY name (name),
    
    host varchar(255),
    port varchar(255),
    username varchar(255),
    type boolean,
    password varchar(255),
    privkey varchar(255)  
);

/*DROP DATABASE uidata;*/ 