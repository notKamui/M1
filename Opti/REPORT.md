# Optimisation

## Partie 1

### Exercice 1

```
max: 2 x1 + x2;
x1 + 2 x2 <= 20;
3.2 x1 + x2 <= 15;
int x1, x2;
```

avec restrictions on obtient x1=2 et x2=8

### Exercie 2

```
max: -2 x1 + x2;
x1 + 2 x2 <= 20;
3.2 x1 + x2 <= 15;
free x1, x2;
```

"This problem is unbounded"

### Exercice 3

```
max: 150x + 100y;
10x + 10y <= 350;
30x + 10y <= 750;
10x + 20y <= 600;
```

Value of objective function: 4500

Actual values of the variables:
x                              20
y                              15

---------------------------------

```
max: 150x + 200y;
10x + 10y <= 350;
30x + 10y <= 750;
10x + 20y <= 600;
```

Value of objective function: 6500

Actual values of the variables:
x                              10
y                              25

### Exercice 6

```
$ time python3 generic.py data/data.txt data.lp
opt = 21654.79332331
P10 = 10.1211
P13 = 4.82887
P22 = 22.7733
P23 = 0.824531
P27 = 14.6569
P29 = 12.2746
python3 generic.py data/data.txt data.lp  0,04s user 0,02s system 100% cpu 0,059 total
```

1. 21654.79332331
2. 6
3. 0.05

### Exercice 7

```
$ time python3 generic.py data/data.txt data.lp -int
opt = 21638.00000000
P1 = 1
P6 = 1
P10 = 7
P13 = 4
P16 = 1
P18 = 2
P19 = 3
P20 = 1
P22 = 15
P25 = 1
P27 = 21
P29 = 7
python3 generic.py data/data.txt data.lp -int  31,75s user 69,07s system 99% cpu 1:40,85 total
```

1. 21638
2. 12
3. 1:40.85

--> En contrainte entière, l'algorithme essaye différentes valeurs à tatons, et donc par bruteforce, pour trouver la solution
    optimale. Cela prend donc énormément de temps.


### Exercice 8

```
$ time python3 generic.py data/bigdata.txt data.lp                                                                                                                                               ─╯
opt = 5050533.10609334
M24 = 0.400901
M214 = 0.328428
M251 = 0.115088
M266 = 0.130587
M287 = 0.642495
M312 = 0.252647
M370 = 0.553936
M392 = 1.23306
M417 = 0.573397
M420 = 0.0924625
M449 = 0.363708
M481 = 0.304358
M573 = 0.316772
M603 = 0.303747
M610 = 0.643703
M642 = 0.344195
M648 = 0.118262
M666 = 0.514571
M690 = 0.205917
M708 = 1.46363
M729 = 1.42969
M753 = 0.317496
M759 = 0.33008
M768 = 0.544069
M815 = 1.25797
M834 = 0.931606
M846 = 0.699307
M901 = 0.152618
M910 = 0.143603
M938 = 0.157488
M988 = 0.543307
M1069 = 0.203577
M1105 = 0.95974
M1115 = 0.636004
M1141 = 1.63815
M1184 = 0.431527
M1225 = 1.37447
M1290 = 0.0371404
M1310 = 0.150461
M1311 = 0.183485
M1339 = 0.871317
M1382 = 0.47961
M1384 = 0.145271
M1391 = 0.534445
M1398 = 1.59784
M1423 = 0.338706
M1465 = 0.0064159
M1558 = 0.867426
M1572 = 0.991143
M1584 = 0.0991437
M1628 = 0.709245
M1705 = 0.0559628
M1710 = 0.701275
M1714 = 0.245129
M1739 = 0.389557
M1749 = 1.08424
M1771 = 0.370778
M1781 = 0.217275
M1798 = 0.39745
M1861 = 0.94626
M1877 = 0.374019
M1893 = 0.771376
M1909 = 0.221485
M1968 = 0.522703
M1983 = 0.0954971
M2000 = 1.6967
M2024 = 0.212347
M2029 = 0.0529964
M2035 = 0.424844
M2061 = 1.42574
M2080 = 0.0392016
M2086 = 0.00759731
M2104 = 0.590123
M2201 = 0.587175
M2222 = 0.616288
M2243 = 0.560826
M2316 = 0.814764
M2373 = 0.879572
M2380 = 0.249043
M2395 = 0.37383
M2399 = 0.374907
M2468 = 1.22385
M2475 = 0.0768296
M2514 = 0.527249
M2518 = 0.656914
M2522 = 2.10557
M2531 = 0.116476
M2598 = 0.834176
M2605 = 1.04779
M2646 = 0.14747
M2679 = 0.108057
M2731 = 0.0673165
M2761 = 0.42582
M2767 = 0.085283
M2784 = 0.372912
M2785 = 0.169396
M2917 = 0.688974
M2922 = 0.504641
M2941 = 1.05783
M2946 = 0.130887
python3 generic.py data/bigdata.txt data.lp  1,34s user 0,06s system 99% cpu 1,417 total
```

1. 5050533.10609334
2. 100
3. 1.417

Avec contrainte : np-difficile