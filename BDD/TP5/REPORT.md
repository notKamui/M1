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