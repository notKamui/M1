DROP TABLE IF EXISTS client_hash CASCADE;

CREATE TABLE client_hash(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);

CREATE INDEX ON client_hash USING hash (age);
CREATE INDEX ON client_hash USING hash (tel);

---Filling table client :
\copy client_hash FROM 'clients_data.csv' csv;
