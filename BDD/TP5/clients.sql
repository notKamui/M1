DROP TABLE IF EXISTS client CASCADE;

CREATE TABLE client(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);

---Filling table client :
\copy client FROM 'clients_data.csv' csv;
