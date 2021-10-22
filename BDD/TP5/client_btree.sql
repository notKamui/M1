DROP TABLE IF EXISTS client_btree CASCADE;

CREATE TABLE client_btree(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);

CREATE INDEX ON client_btree USING btree (age);
CREATE INDEX ON client_btree USING btree (tel);

---Filling table client :
\copy client_btree FROM 'clients_data.csv' csv;
