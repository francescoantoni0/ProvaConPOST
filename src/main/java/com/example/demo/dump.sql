DROP DATABASE IF EXISTS tutor;
CREATE DATABASE tutor;

USE tutor;

CREATE TABLE reg(
    plate VARCHAR(20),
    timestamp TIMESTAMP,
    PRIMARY KEY (plate, timestamp)
);