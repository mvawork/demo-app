drop DATABASE IF EXISTS testappdb;
drop ROLE IF EXISTS testapp;

CREATE user testapp WITH PASSWORD 'xxxxxx';

CREATE DATABASE testappdb WITH OWNER = testapp;

