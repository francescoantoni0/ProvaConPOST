DROP DATABASE IF EXISTS tutor;
CREATE DATABASE tutor;

USE tutor;

CREATE TABLE reg(
    plate VARCHAR(20) PRIMARY KEY,
    timestamp TIMESTAMP
);