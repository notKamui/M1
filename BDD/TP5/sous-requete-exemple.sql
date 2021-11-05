
SELECT idmag, idpro, prixunit
FROM   stocke NATURAL JOIN produit
WHERE  libelle = 'bureau'; 

SELECT idmag, produit.idpro, prixunit
FROM   stocke, produit
WHERE  libelle = 'bureau'
       AND stocke.idpro = produit.idpro; 

SELECT idmag, idpro, prixunit
FROM   stocke
WHERE  EXISTS (SELECT *
               FROM   produit
               WHERE  libelle = 'bureau'
                      AND produit.idpro = stocke.idpro); 

SELECT idmag, idpro, prixunit
FROM   stocke
WHERE  idpro IN (SELECT idpro
                 FROM   produit
                 WHERE  libelle = 'bureau'); 
