# divisonexists

EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND NOT EXISTS
			(
				SELECT *       
				FROM stocke NATURAL JOIN produit as p
				WHERE stocke.idmag = magasin.idmag
				AND p.couleur = produit.couleur
			) 
	);

# divisionin

EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND couleur NOT IN
			(
				SELECT couleur 
				FROM stocke NATURAL JOIN produit  
				WHERE stocke.idmag = magasin.idmag
				AND couleur IS NOT NULL
			) 
	);


# divisionincorr

EXPLAIN ANALYSE
SELECT idmag FROM magasin
WHERE NOT EXISTS
	(
		SELECT * FROM produit
		WHERE couleur IS NOT NULL
		AND idmag NOT IN
			(
				SELECT idmag
				FROM stocke NATURAL JOIN produit p
				WHERE p.couleur = produit.couleur
			)
	);

