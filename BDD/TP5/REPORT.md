# BDD - TP1

## Partie 1

### 2

```
jimmy.teillard_db=> \d client
                                        Table « public.client »
 Colonne |         Type          | Collationnement | NULL-able |               Par défaut               
---------+-----------------------+-----------------+-----------+----------------------------------------
 numcli  | integer               |                 | not null  | nextval('client_numcli_seq'::regclass)
 nom     | character varying(25) |                 |           | 
 prenom  | character varying(25) |                 |           | 
 prenom2 | character varying(25) |                 |           | 
 prenom3 | character varying(25) |                 |           | 
 age     | integer               |                 |           | 
 ville   | character varying(25) |                 |           | 
 tel     | character varying(10) |                 |           | 
Index :
    "client_pkey" PRIMARY KEY, btree (numcli)
```

Il existe bien un index btree sur la clé primaire de la table. Postgres crée automatiquement un index sur les clés primaires.

### 3

```
jimmy.teillard_db=> explain select * from client limit 20;
                             QUERY PLAN                              
---------------------------------------------------------------------
 Limit  (cost=0.00..0.39 rows=20 width=54)
   ->  Seq Scan on client  (cost=0.00..4898.54 rows=248954 width=54)
(2 lignes)
```

```
jimmy.teillard_db=> explain select * from client where numcli > 30 AND numcli < 50;
                                 QUERY PLAN                                 
----------------------------------------------------------------------------
 Index Scan using client_pkey on client  (cost=0.42..8.80 rows=19 width=54)
   Index Cond: ((numcli > 30) AND (numcli < 50))
(2 lignes)
```

L'index sur le `numcli` va permettre de faire des recherches sur range plus efficacement.
- `cost` est une estimation du coût des opérations préalables (tri, par exemple).
- `row` est l'estimation du nombre d'enregistrements en sortie.
- `width` est l'estimation de la moyenne des taille en byte des enregistrements.


### 5

```
jimmy.teillard_db=> explain select * from client_btree where age between 20 and 30; 
                             QUERY PLAN                              
---------------------------------------------------------------------
 Seq Scan on client_btree  (cost=0.00..6143.31 rows=137265 width=54)
   Filter: ((age >= 20) AND (age <= 30))
(2 lignes)
```

```
jimmy.teillard_db=> explain select * from client_hash where age between 20 and 30; 
                             QUERY PLAN                             
--------------------------------------------------------------------
 Seq Scan on client_hash  (cost=0.00..6143.31 rows=138247 width=54)
   Filter: ((age >= 20) AND (age <= 30))
(2 lignes)
```

## Partie 2

### 7

```
jimmy.teillard_db=> explain select * from client_btree where age = 30;
                                       QUERY PLAN                                        
-----------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_btree  (cost=430.32..3087.54 rows=19858 width=54)
   Recheck Cond: (age = 30)
   ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..425.36 rows=19858 width=0)
         Index Cond: (age = 30)
(4 lignes)
```

```
jimmy.teillard_db=> explain select * from client_hash where age = 30;
                                       QUERY PLAN                                       
----------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_hash  (cost=694.46..3359.05 rows=20447 width=54)
   Recheck Cond: (age = 30)
   ->  Bitmap Index Scan on client_hash_age_idx  (cost=0.00..689.35 rows=20447 width=0)
         Index Cond: (age = 30)
(4 lignes)
```

Double check bitmap heap scan into bitmap index scan

### 8

```sql
select tel, count(tel) as c from client_btree group by tel having count(tel) >= 2;
```

```
jimmy.teillard_db=> explain select * from client_btree where tel = '0004955053';
                                        QUERY PLAN                                        
------------------------------------------------------------------------------------------
 Index Scan using client_btree_tel_idx on client_btree  (cost=0.42..8.44 rows=1 width=54)
   Index Cond: ((tel)::text = '0004955053'::text)
(2 lignes)
```
-> chaque index sera vérifié et retiré séquentiellement

```
jimmy.teillard_db=> explain select * from client_btree where tel is null;
                                      QUERY PLAN                                       
---------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_btree  (cost=77.13..2623.56 rows=2672 width=54)
   Recheck Cond: (tel IS NULL)
   ->  Bitmap Index Scan on client_btree_tel_idx  (cost=0.00..76.46 rows=2672 width=0)
         Index Cond: (tel IS NULL)
(4 lignes)
```
-> chaque pointeur sur un tuple va être enregistré pour finallement retirer tout un bloc d'un seul coup

```
jimmy.teillard_db=> explain select * from client_hash where tel is null;
                            QUERY PLAN                            
------------------------------------------------------------------
 Seq Scan on client_hash  (cost=0.00..4898.54 rows=2365 width=54)
   Filter: (tel IS NULL)
(2 lignes)

```
-> quasi toute la table est lue donc, puisque un hash ne peux pas être "trié", elle est lue séquentiellement

Quand un grand nombre de données individuelles doivent être analysées, les bitmap sont préférées.

### 9

```
jimmy.teillard_db=> explain select * from client_btree where age between 20 and 50;
                             QUERY PLAN                              
---------------------------------------------------------------------
 Seq Scan on client_btree  (cost=0.00..6143.31 rows=241308 width=54)
   Filter: ((age >= 20) AND (age <= 50))
(2 lignes)
```
```
jimmy.teillard_db=> explain select * from client_btree where age between 20 and 22;
                                       QUERY PLAN                                        
-----------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_btree  (cost=339.18..2957.10 rows=13928 width=54)
   Recheck Cond: ((age >= 20) AND (age <= 22))
   ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..335.70 rows=13928 width=0)
         Index Cond: ((age >= 20) AND (age <= 22))
(4 lignes)
```
```
jimmy.teillard_db=> explain select * from client_hash where age between 20 and 22;
                            QUERY PLAN                             
-------------------------------------------------------------------
 Seq Scan on client_hash  (cost=0.00..6143.31 rows=14117 width=54)
   Filter: ((age >= 20) AND (age <= 22))
(2 lignes)
```

btree sur range courte -> bitmap ; puisque les blocs sont alignés dans la mémoire (grace au tri)

### 10

```
jimmy.teillard_db=> explain select * from client_hash where age is null;
                            QUERY PLAN                            
------------------------------------------------------------------
 Seq Scan on client_hash  (cost=0.00..4898.54 rows=2415 width=54)
   Filter: (age IS NULL)
(2 lignes)
```
```
jimmy.teillard_db=> explain select * from client_btree where age is null;
                                      QUERY PLAN                                       
---------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_btree  (cost=55.39..2575.67 rows=2448 width=54)
   Recheck Cond: (age IS NULL)
   ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..54.78 rows=2448 width=0)
         Index Cond: (age IS NULL)
(4 lignes)
```
hash ne gère pas les null

### 11

```
jimmy.teillard_db=> explain select * from client_btree where age is not null;
                             QUERY PLAN                              
---------------------------------------------------------------------
 Seq Scan on client_btree  (cost=0.00..4898.54 rows=246506 width=54)
   Filter: (age IS NOT NULL)
(2 lignes)
```
trop de not null pour qu'il soit intéressant de faire autre chose que récupérer les lignes séquentiellements.

### 12

```
jimmy.teillard_db=> explain select * from client_btree where age between 20 and 22 and tel = '0004955053';
                                        QUERY PLAN                                        
------------------------------------------------------------------------------------------
 Index Scan using client_btree_tel_idx on client_btree  (cost=0.42..8.44 rows=1 width=54)
   Index Cond: ((tel)::text = '0004955053'::text)
   Filter: ((age >= 20) AND (age <= 22))
(3 lignes)
```
```
jimmy.teillard_db=> explain select * from client_hash where age between 20 and 22 and tel = '0004955053';
                                       QUERY PLAN                                       
----------------------------------------------------------------------------------------
 Index Scan using client_hash_tel_idx on client_hash  (cost=0.00..8.02 rows=1 width=54)
   Index Cond: ((tel)::text = '0004955053'::text)
   Filter: ((age >= 20) AND (age <= 22))
(3 lignes)
```
Cette fois ci, le hash est capable de comprendre qu'il doit indexer un petit nombre de données.

### 13

```
jimmy.teillard_db=> explain select * from client_btree where age <  12 and tel > '0900000000';
                                          QUERY PLAN                                           
-----------------------------------------------------------------------------------------------
 Bitmap Heap Scan on client_btree  (cost=639.94..736.22 rows=26 width=54)
   Recheck Cond: ((age < 12) AND ((tel)::text > '0900000000'::text))
   ->  BitmapAnd  (cost=639.94..639.94 rows=26 width=0)
         ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..6.52 rows=280 width=0)
               Index Cond: (age < 12)
         ->  Bitmap Index Scan on client_btree_tel_idx  (cost=0.00..633.15 rows=23031 width=0)
               Index Cond: ((tel)::text > '0900000000'::text)
(7 lignes)
```

Pas beaucoup de gens en dessous de 12 ans, ni de numéro en 09; les deux index sont visités séparéments puis un bitwise AND est appliqué sur les pointeurs pour tout récupérer en bloc.

# TP2

## Exercice 1

```
jimmy.teillard_db=> SELECT DISTINCT(ville) FROM client WHERE prenom = 'Alice' AND age = 38 INTERSECT (SELECT DISTINCT(ville) FROM client WHERE prenom = 'Robert' AND age = 38);
    ville    
-------------
 lille
 montpellier
 grenoble
 nice
(4 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE SELECT ville FROM client WHERE prenom = 'Alice' AND age = 38 INTERSECT (SELECT ville FROM client WHERE prenom = 'Robert' AND age = 38);
                                                           QUERY PLAN                                                            
---------------------------------------------------------------------------------------------------------------------------------
 HashSetOp Intersect  (cost=0.00..6191.16 rows=1 width=68) (actual time=88.098..88.099 rows=4 loops=1)
   ->  Append  (cost=0.00..6191.15 rows=2 width=68) (actual time=1.744..88.039 rows=31 loops=1)
         ->  Subquery Scan on "*SELECT* 1"  (cost=0.00..3095.58 rows=1 width=68) (actual time=1.744..49.954 rows=18 loops=1)
               ->  Seq Scan on client  (cost=0.00..3095.57 rows=1 width=68) (actual time=1.743..49.942 rows=18 loops=1)
                     Filter: (((prenom)::text = 'Alice'::text) AND (age = 38))
                     Rows Removed by Filter: 248936
         ->  Subquery Scan on "*SELECT* 2"  (cost=0.00..3095.58 rows=1 width=68) (actual time=2.930..38.073 rows=13 loops=1)
               ->  Seq Scan on client client_1  (cost=0.00..3095.57 rows=1 width=68) (actual time=2.929..38.065 rows=13 loops=1)
                     Filter: (((prenom)::text = 'Robert'::text) AND (age = 38))
                     Rows Removed by Filter: 248941
 Planning time: 0.234 ms
 Execution time: 88.167 ms
(12 lignes)
```

```
jimmy.teillard_db=>  EXPLAIN ANALYZE SELECT ville FROM client_btree WHERE prenom = 'Alice' AND age = 38 INTERSECT (SELECT ville FROM client_btree WHERE prenom = 'Robert' AND age = 38);
                                                                      QUERY PLAN                                                                       
-------------------------------------------------------------------------------------------------------------------------------------------------------
 HashSetOp Intersect  (cost=104.58..5172.12 rows=1 width37.538..2586.03 rows=13 width=7) (actual time=2.080..32.481 rows=18 loops=1)
               ->  Bitmap Heap Scan on client_btree  (cost=104.58..2585.90 rows=13 width=7) (actual time=2.079..32.465 rows=18 loops=1)
                     Recheck Cond: (age = 38)
                     Filter: ((prenom)::text = 'Alice'::text)
                     Rows Removed by Filter: 4545
                     Heap Blocks: exact=2046
                     ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..104.58 rows=4821 width=0) (actual time=1.014..1.014 rows=4563 loops=1)
                           Index Cond: (age = 38)
         ->  Subquery Scan on "*SELECT* 2"  (cost=104.58..2586.03 rows=13 width=7) (actual time=1.161..4.891 rows=13 loops=1)
               ->  Bitmap Heap Scan on client_btree client_btree_1  (cost=104.58..2585.90 rows=13 width=7) (actual time=1.160..4.885 rows=13 loops=1)
                     Recheck Cond: (age = 38)
                     Filter: ((prenom)::text = 'Robert'::text)
                     Rows Removed by Filter: 4550
                     Heap Blocks: exact=2046
                     ->  Bitmap Index Scan on client_btree_age_idx  (cost=0.00..104.58 rows=4821 width=0) (actual time=0.609..0.609 rows=4563 loops=1)
                           Index Cond: (age = 38)
 Planning time: 0.207 ms
 Execution time: 37.538 ms
(20 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE select distinct(ville) from client where ville in (select ville from client c1 where prenom = 'Robert' and age = 38 and exists
(select ville from client c2 where prenom = 'Alice' and age = 38 and c1.ville = c2.ville));
                                                                 QUERY PLAN                                                                  
---------------------------------------------------------------------------------------------------------------------------------------------
 HashAggregate  (cost=9289.03..9291.03 rows=200 width=68) (actual time=162.092..162.093 rows=4 loops=1)
   Group Key: client.ville
   ->  Hash Join  (cost=6191.17..9231.82 rows=22886 width=68) (actual time=81.189..154.385 rows=41307 loops=1)
         Hash Cond: ((client.ville)::text = (c1.ville)::text)
         ->  Seq Scan on client  (cost=0.00..2866.71 rows=45771 width=68) (actual time=0.052..31.012 rows=248954 loops=1)
         ->  Hash  (cost=6191.16..6191.16 rows=1 width=136) (actual time=81.101..81.101 rows=4 loops=1)
               Buckets: 1024  Batches: 1  Memory Usage: 9kB
               ->  HashAggregate  (cost=6191.15..6191.16 rows=1 width=136) (actual time=81.092..81.093 rows=4 loops=1)
                     Group Key: (c1.ville)::text
                     ->  Nested Loop Semi Join  (cost=0.00..6191.15 rows=1 width=136) (actual time=54.807..81.072 rows=7 loops=1)
                           Join Filter: ((c1.ville)::text = (c2.ville)::text)
                           Rows Removed by Join Filter: 138
                           ->  Seq Scan on client c1  (cost=0.00..3095.57 rows=1 width=68) (actual time=4.470..38.515 rows=13 loops=1)
                                 Filter: (((prenom)::text = 'Robert'::text) AND (age = 38))
                                 Rows Removed by Filter: 248941
                           ->  Materialize  (cost=0.00..3095.57 rows=1 width=68) (actual time=0.070..3.270 rows=11 loops=13)
                                 ->  Seq Scan on client c2  (cost=0.00..3095.57 rows=1 width=68) (actual time=0.904..42.472 rows=18 loops=1)
                                       Filter: ((age = 38) AND ((prenom)::text = 'Alice'::text))
                                       Rows Removed by Filter: 248936
 Planning time: 0.275 ms
 Execution time: 162.181 ms
(21 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE select DISTINCT(ville) from client c1 where prenom = 'Robert' and age = 38 and exists
(select ville from client c2 where prenom = 'Alice' and age = 38 and c1.ville = c2.ville);
                                                        QUERY PLAN                                                         
---------------------------------------------------------------------------------------------------------------------------
 HashAggregate  (cost=6191.15..6191.16 rows=1 width=68) (actual time=91.141..91.142 rows=4 loops=1)
   Group Key: c1.ville
   ->  Nested Loop Semi Join  (cost=0.00..6191.15 rows=1 width=68) (actual time=59.305..91.113 rows=7 loops=1)
         Join Filter: ((c1.ville)::text = (c2.ville)::text)
         Rows Removed by Join Filter: 138
         ->  Seq Scan on client c1  (cost=0.00..3095.57 rows=1 width=68) (actual time=4.054..44.078 rows=13 loops=1)
               Filter: (((prenom)::text = 'Robert'::text) AND (age = 38))
               Rows Removed by Filter: 248941
         ->  Materialize  (cost=0.00..3095.57 rows=1 width=68) (actual time=0.064..3.613 rows=11 loops=13)
               ->  Seq Scan on client c2  (cost=0.00..3095.57 rows=1 width=68) (actual time=0.825..46.926 rows=18 loops=1)
                     Filter: ((age = 38) AND ((prenom)::text = 'Alice'::text))
                     Rows Removed by Filter: 248936
 Planning time: 0.181 ms
 Execution time: 91.215 ms
(14 lignes)
```

## Exercice 2

### 1

```
jimmy.teillard_db=> EXPLAIN ANALYZE SELECT idmag, idpro, prixunit
jimmy.teillard_db-> FROM   stocke NATURAL JOIN produit
jimmy.teillard_db-> WHERE  libelle = 'bureau'; 
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=16.02..680.73 rows=137 width=20) (actual time=0.152..8.888 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.70 rows=32970 width=20) (actual time=0.009..4.364 rows=32928 loops=1)
   ->  Hash  (cost=16.00..16.00 rows=2 width=4) (actual time=0.127..0.127 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..16.00 rows=2 width=4) (actual time=0.014..0.120 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.138 ms
 Execution time: 8.999 ms
(10 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE SELECT idmag, produit.idpro, prixunit
jimmy.teillard_db-> FROM   stocke, produit
jimmy.teillard_db-> WHERE  libelle = 'bureau'
jimmy.teillard_db->        AND stocke.idpro = produit.idpro;
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=16.02..680.73 rows=137 width=20) (actual time=0.147..6.863 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.70 rows=32970 width=20) (actual time=0.009..3.496 rows=32928 loops=1)
   ->  Hash  (cost=16.00..16.00 rows=2 width=4) (actual time=0.129..0.129 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..16.00 rows=2 width=4) (actual time=0.014..0.121 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.135 ms
 Execution time: 6.961 ms
(10 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE SELECT idmag, idpro, prixunit
jimmy.teillard_db-> FROM   stocke
jimmy.teillard_db-> WHERE  EXISTS (SELECT *
jimmy.teillard_db(>                FROM   produit
jimmy.teillard_db(>                WHERE  libelle = 'bureau'
jimmy.teillard_db(>                       AND produit.idpro = stocke.idpro); 
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=16.02..680.73 rows=16485 width=20) (actual time=0.202..6.954 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.70 rows=32970 width=20) (actual time=0.019..3.662 rows=32928 loops=1)
   ->  Hash  (cost=16.00..16.00 rows=2 width=4) (actual time=0.169..0.169 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..16.00 rows=2 width=4) (actual time=0.026..0.162 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.193 ms
 Execution time: 7.037 ms
(10 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYZE SELECT idmag, idpro, prixunit
jimmy.teillard_db-> FROM   stocke
jimmy.teillard_db-> WHERE  idpro IN (SELECT idpro
jimmy.teillard_db(>                  FROM   produit
jimmy.teillard_db(>                  WHERE  libelle = 'bureau');
                                                   QUERY PLAN                                                    
-----------------------------------------------------------------------------------------------------------------
 Hash Join  (cost=16.02..680.73 rows=16485 width=20) (actual time=0.388..12.844 rows=1031 loops=1)
   Hash Cond: (stocke.idpro = produit.idpro)
   ->  Seq Scan on stocke  (cost=0.00..539.70 rows=32970 width=20) (actual time=0.014..6.046 rows=32928 loops=1)
   ->  Hash  (cost=16.00..16.00 rows=2 width=4) (actual time=0.335..0.335 rows=33 loops=1)
         Buckets: 1024  Batches: 1  Memory Usage: 10kB
         ->  Seq Scan on produit  (cost=0.00..16.00 rows=2 width=4) (actual time=0.028..0.316 rows=33 loops=1)
               Filter: ((libelle)::text = 'bureau'::text)
               Rows Removed by Filter: 967
 Planning time: 0.325 ms
 Execution time: 13.007 ms
(10 lignes)
```

Même query plan a chaque fois ; postgres est capable d'optimiser de lui même.

### 2

```
jimmy.teillard_db=> EXPLAIN ANALYSE
jimmy.teillard_db-> SELECT idmag FROM magasin
jimmy.teillard_db-> WHERE NOT EXISTS
jimmy.teillard_db->  (
jimmy.teillard_db(>   SELECT * FROM produit
jimmy.teillard_db(>   WHERE couleur IS NOT NULL
jimmy.teillard_db(>   AND NOT EXISTS
jimmy.teillard_db(>    (
jimmy.teillard_db(>     SELECT *       
jimmy.teillard_db(>     FROM stocke NATURAL JOIN produit as p
jimmy.teillard_db(>     WHERE stocke.idmag = magasin.idmag
jimmy.teillard_db(>     AND p.couleur = produit.couleur
jimmy.teillard_db(>    ) 
jimmy.teillard_db(>  );
                                                           QUERY PLAN                                                            
---------------------------------------------------------------------------------------------------------------------------------
 Nested Loop Anti Join  (cost=0.00..3056278.50 rows=195 width=4) (actual time=70.268..471.835 rows=437 loops=1)
   Join Filter: (NOT (alternatives: SubPlan 1 or hashed SubPlan 2))
   Rows Removed by Join Filter: 404255
   ->  Seq Scan on magasin  (cost=0.00..13.90 rows=390 width=4) (actual time=0.026..0.307 rows=500 loops=1)
   ->  Materialize  (cost=0.00..17.19 rows=478 width=68) (actual time=0.000..0.170 rows=809 loops=500)
         ->  Seq Scan on produit  (cost=0.00..14.80 rows=478 width=68) (actual time=0.025..0.163 rows=923 loops=1)
               Filter: (couleur IS NOT NULL)
               Rows Removed by Filter: 77
   SubPlan 1
     ->  Nested Loop  (cost=0.29..32.64 rows=1 width=0) (never executed)
           ->  Seq Scan on produit p  (cost=0.00..16.00 rows=2 width=4) (never executed)
                 Filter: ((couleur)::text = (produit.couleur)::text)
           ->  Index Only Scan using stocke_pkey on stocke  (cost=0.29..8.31 rows=1 width=4) (never executed)
                 Index Cond: ((idmag = magasin.idmag) AND (idpro = p.idpro))
                 Heap Fetches: 0
   SubPlan 2
     ->  Hash Join  (cost=20.80..1013.84 rows=32970 width=72) (actual time=0.449..32.020 rows=32928 loops=1)
           Hash Cond: (stocke_1.idpro = p_1.idpro)
           ->  Seq Scan on stocke stocke_1  (cost=0.00..539.70 rows=32970 width=8) (actual time=0.023..7.450 rows=32928 loops=1)
           ->  Hash  (cost=14.80..14.80 rows=480 width=72) (actual time=0.399..0.399 rows=1000 loops=1)
                 Buckets: 1024  Batches: 1  Memory Usage: 51kB
                 ->  Seq Scan on produit p_1  (cost=0.00..14.80 rows=480 width=72) (actual time=0.005..0.231 rows=1000 loops=1)
 Planning time: 0.457 ms
 Execution time: 472.014 ms
(24 lignes)
```

```
jimmy.teillard_db=> EXPLAIN ANALYSE
jimmy.teillard_db-> SELECT idmag FROM magasin
jimmy.teillard_db-> WHERE NOT EXISTS
jimmy.teillard_db->  (
jimmy.teillard_db(>   SELECT * FROM produit
jimmy.teillard_db(>   WHERE couleur IS NOT NULL
jimmy.teillard_db(>   AND couleur NOT IN
jimmy.teillard_db(>    (
jimmy.teillard_db(>     SELECT couleur 
jimmy.teillard_db(>     FROM stocke NATURAL JOIN produit  
jimmy.teillard_db(>     WHERE stocke.idmag = magasin.idmag
jimmy.teillard_db(>     AND couleur IS NOT NULL
jimmy.teillard_db(>    ) 
jimmy.teillard_db(>  );
                                                                QUERY PLAN                                                                
------------------------------------------------------------------------------------------------------------------------------------------
 Nested Loop Anti Join  (cost=0.00..12388300.45 rows=195 width=4) (actual time=52.626..28436.849 rows=437 loops=1)
   Join Filter: (NOT (SubPlan 1))
   Rows Removed by Join Filter: 404255
   ->  Seq Scan on magasin  (cost=0.00..13.90 rows=390 width=4) (actual time=0.032..0.513 rows=500 loops=1)
   ->  Materialize  (cost=0.00..17.19 rows=478 width=68) (actual time=0.000..0.551 rows=809 loops=500)
         ->  Seq Scan on produit  (cost=0.00..14.80 rows=478 width=68) (actual time=0.020..0.286 rows=923 loops=1)
               Filter: (couleur IS NOT NULL)
               Rows Removed by Filter: 77
   SubPlan 1
     ->  Hash Join  (cost=26.34..237.93 rows=164 width=68) (actual time=0.045..0.062 rows=12 loops=404318)
           Hash Cond: (stocke.idpro = produit_1.idpro)
           ->  Bitmap Heap Scan on stocke  (cost=5.57..214.89 rows=165 width=4) (actual time=0.034..0.039 rows=12 loops=404318)
                 Recheck Cond: (idmag = magasin.idmag)
                 Heap Blocks: exact=429612
                 ->  Bitmap Index Scan on stocke_pkey  (cost=0.00..5.53 rows=165 width=0) (actual time=0.020..0.020 rows=69 loops=404318)
                       Index Cond: (idmag = magasin.idmag)
           ->  Hash  (cost=14.80..14.80 rows=478 width=72) (actual time=0.453..0.453 rows=923 loops=1)
                 Buckets: 1024  Batches: 1  Memory Usage: 48kB
                 ->  Seq Scan on produit produit_1  (cost=0.00..14.80 rows=478 width=72) (actual time=0.004..0.285 rows=923 loops=1)
                       Filter: (couleur IS NOT NULL)
                       Rows Removed by Filter: 77
 Planning time: 0.268 ms
 Execution time: 28437.166 ms
(23 lignes)
```

Il refait les mêmes requetes pleins de fois (loops > 1)

## Exercice 3

### 1

```
jimmy.teillard_db=>  select * from pg_catalog.pg_stats where tablename='client_btree' and attname='age';
-[ RECORD 1 ]-
schemaname             | public
tablename              | client_btree
attname                | age
inherited              | f
null_frac              | 0.00983333
avg_width              | 4
n_distinct             | 40
most_common_vals       | {30,29,31,28,27,32,26,33,34,25,35,24,23,36,37,22,38,21,39,20}
most_common_freqs      | {0.0793667,0.0767,0.0764333,0.0737667,0.0711667,0.0693333,0.063,0.0623333,0.0536,0.0527,0.0434667,0.0419333,0.0351667,0.0341333,0.0265667,0.0257,0.0193667,0.0181333,0.0127333,0.0119}
histogram_bounds       | {10,16,17,17,18,18,19,19,19,19,40,40,40,40,41,41,42,42,44,49}
correlation            | 0.0518037
most_common_elems      | 
most_common_elem_freqs | 
elem_count_histogram   |
```

```
jimmy.teillard_db=>  select * from pg_catalog.pg_stats where tablename='client_btree' and attname='tel';
-[ RECORD 1 ]-
schemaname             | public
tablename              | client_btree
attname                | tel
inherited              | f
null_frac              | 0.0107333
avg_width              | 11
n_distinct             | -0.989267
most_common_vals       | 
most_common_freqs      | 
histogram_bounds       | {0000019061,0009384956,0019626121,0029465837,0038758608,0047594326,0057715580,0067914478,0078160572,0087788067,0098055654,0108030648,0117726401,0128280352,0137809457,0147146132,0157589574,0167729556,0178106032,0188183012,0198254632,0208184501,0217499391,0227669695,0236920897,0246430469,0255935368,0265352760,0276293918,0285690148,0294949544,0304811169,0315001812,0325109994,0336022358,0346058392,0356975921,0366762881,0376273497,0386575676,0396954513,0406740899,0416978723,0426557413,0435950428,0445228379,0454674693,0465717552,0475683258,0486118792,0496389969,0506064698,0515547330,0524954396,0535907529,0546906378,0557012101,0567690393,0577851555,0587756532,0597468003,0607573975,0616566721,0626906361,0636440626,0647256833,0657296107,0668019796,0678509904,0689207364,0698924866,0708509831,0718405969,0728380314,0738095487,0748107703,0758056664,0767647807,0777856860,0788133764,0797594002,0808525308,0819109643,0828882941,0840079771,0849125535,0858779679,0868521514,0878326339,0889058386,0897920420,0907750557,0918687407,0928587178,0939002779,0948519420,0958546976,0968732285,0979572923,0990326582,0999966727}
correlation            | -0.0113086
most_common_elems      | 
most_common_elem_freqs | 
elem_count_histogram   |
```

```
jimmy.teillard_db=>  select * from pg_catalog.pg_stats where tablename='client_btree' and attname='prenom3';
-[ RECORD 1 ]-
schemaname             | public
tablename              | client_btree
attname                | prenom3
inherited              | f
null_frac              | 0.775333
avg_width              | 7
n_distinct             | 367
most_common_vals       | 
most_common_freqs      | 
histogram_bounds       | {Adèle,Agnès,Alexandra,Alphonse,André,Anthony,Arnaud,Aurélia,Axel,Bastien,Benedict,Bérénice,Blanche,Brittany,Bryan,Cassandre,Céline,Charles,Christelle,Christopher,Clémentine,Corentin,Cyndie,David,Denise,Diego,Dorothée,Elena,Elliot,Emeric,Emma,Eric,Etienne,Fanny,Fleur,Florent,Françoise,Gaëlle,Gehanne,Germain,Grace,Guillaume,Hélène,Hermione,Ibrahim,Iolanda,Isabelle,Jacques,Jean,Jeanne,Jessica,Jodie,Judith,Karim,Lamri,Laurie,Léon,Lilya,Louise,Lucien,Mamadou,Marianne,Mario,Mathilde,Maxime,Mélanie,Michaël,Mylène,Natacha,Nicolas,Nina,Océane,Olivia,Ophélie,Pascale,Pauline,Pierre,Prune,Raphaelle,Rémi,Rhada,Rodolphe,Romane,Sami,Sandy,Simon,Sophie,Summer,Talia,Tarja,Théodore,Timothé,Valentin,Victoire,Vincent,Vitaly,Wallace,Yann,Yasser,Yves,Zora}
correlation            | 3.33017e-05
most_common_elems      | 
most_common_elem_freqs | 
elem_count_histogram   |
```

### 2

Les histogrammes sont triés par ordre naturel ascendant (croissant, alphabétique, etc)

### 3

```
jimmy.teillard_db=> show default_statistics_target;
-[ RECORD 1 ]-------------+----
default_statistics_target | 100
```

### 4

```
jimmy.teillard_db=> ALTER TABLE client_btree ALTER COLUMN age SET STATISTICS 1000;
```