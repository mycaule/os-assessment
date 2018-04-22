
<p align="center">
  April 2018 OS Interview Assessment
</p>

<p align="center">
  <a href="http://travis-ci.org/mycaule/os-assessment"><img src="https://api.travis-ci.org/mycaule/os-assessment.svg?branch=master" alt="Build Status"></a>
  <br>
  <br>
</p>


### Utilisation
```
# Lancer les tests unitaires
sbt test
# Lancer le programme
sbt run
```

### Solution
Le problème est une variante au problème du voyageur de commerce auquel on ne sait pas trouver de solution exacte rapidement (problème NP-complet).

Soit *n* le nombre d'articles en entrée et le coût le nombre de cartons utilisés. Minimiser le coût nécessite d'examiner au pire *n!* cas parmi les permutations possibles de la liste de départ. Outre l'algorithme naïf fourni en énoncé (traitement en temps réel), nous l'améliorons légèrement en ne fermant pas le carton immédiatement avant d'avoir trouvé un autre article pouvant le compléter. Une autre observation est la possibilité de traiter les éléments les plus grands en priorité.

D'autres approches existent pour calculer une approximation de la solution. Souvent elles nécessiteront un parcours complet de liste *a posteriori*.

Nous utilisons une approche de type Monte Carlo en mettant à profit l'algorithme initial. Nous évaluons ainsi le coût associé à un grand nombre de tirages aléatoires de la liste d'articles en entrée et emballés dans l'ordre que fourni le tirage.

Lorsque le nombre de tirages est suffisamment important, cela permet d'aboutir à un résultat satisfaisant.
Cette approche est ainsi généralisable à des volumes de données plus important pour des structures de type *Stream* ou *RDD* où la parallélisation pourra être envisagée.

De manière qualitative, un mélange aléatoire des données tend à homogénéiser la taille des articles au cours de la progression dans la liste.

### Livrable

Nous implémentons un objet [*Parcel*](src/main/scala/mycaule/Parcel.scala) capable de :
- valider la conformité des données par rapport aux règles métier,
- écrire les données au format de chaîne de caractères,
- grouper les articles dans des cartons par trois algorithmes différents.

Le programme principal permet également d'évaluer et de comparer les deux algorithmes implémentés :

```
[info] Running mycaule.Parcel
Chaîne d'articles en entrée :
1132984984938277239
19 articles à emballer
[info] Monte Carlo pour n=19, tirages=10000, permutations=1.211E+17
Chaîne d'articles emballés :
temps réel - 1132/9/8/4/9/8/4/9/3/82/7/72/3/9 (14 cartons, K=0.71)
meilleur choix - 9/9/9/9/81/81/8/73/73/44/3222 (11 cartons, K=0.90)
montecarlo - 9/82/19/37/34/8/712/43/9/28/9 (11 cartons, K=0.90)
[success] Total time: 15 s, completed Apr 22, 2018 2:59:42 PM
```

Un mini système d'intégration continue est également mis en place dans [Travis CI](http://travis-ci.org/mycaule/os-assessment).

### Pour aller plus loin

Des aléas pouvant arriver dans la progression dans la chaîne, rien ne prouve l'optimalité de ces méthodes.

Des approches mixtes pourront toutefois être adoptées (algorithmes génétiques). En raison de la complexité algorithmique du problème ces solutions resteront approximatives sans garanties d'optimalité.

Ces évolutions peuvent également être étudiées :
- formalisation sous un programme d'optimisation linéaire (librairie *Breeze*),
- paralléliser l'implémentation (librairie *Spark*),
- fournir une implémentation *online* sur des *Streams*.

#### Références

- [Wikipedia - Bin packing problem](https://en.wikipedia.org/wiki/Bin_packing_problem)
- [Youtube - Google Interview - Find pair matching a sum](https://www.youtube.com/watch?v=XKu_SEDAykw)
- [YouTube - Big Rocks](https://www.youtube.com/watch?v=8FbWb3f-jLQ)
