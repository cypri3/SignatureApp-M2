# BLS-M2

### Ressources intéressantes :
 - Implémentation python BLS : https://github.com/asonnino/bls
 - Pseudo code : https://datatracker.ietf.org/doc/html/draft-boneh-bls-signature-00#section-2.1 (En dessous la section 2.1)

 - Implémentation de MD5 en python : https://github.com/timvandermeij/md5.py/blob/master/md5.py
 - Psueod code de MD5 : https://en.wikipedia.org/wiki/MD5#Pseudocode

 Pour compiler : 
 javac -cp "lib/*" -d bin src/Main.java
 Pour executer :
 java -cp "lib/*:bin" Main
 Pour trouver l'emplacement d'une classe :
 jar tf lib/jpbc-plaf-2.0.0.jar | grep PairingFactory
 Pour les a.properties :
 https://github.com/chrizchow/JPBC-ABS2/blob/master/a.properties