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
