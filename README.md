# Partage de pièces jointe chiffrée via AES/IBE

Projet de Cryptographie Avancée réalisé en 2025 en tant que 4A ICY à l'INSA HdF.
Ce projet Java permet d'envoyer des pièces jointes par mail sécurisées par une combinaison de AES et IBE, avec une autorité de certification délivrant les clés privées.

## Authors

- DUCRAUX Tristan [@C0co-maker](https://github.com/TristanDcrxdu77Maxdu69)
- LEMAITRE Maxime [@maxlem24](https://github.com/maxlem24)
- JAMIN Antoin
- MENU Thomas [@Saitilink](https://github.com/Saitilink)
- SALTEL Baptiste [@SHA_foin](https://github.com/SHAfoin)

## Deployment

Nécessite d'avoir installé JavaFX manuellement ou via Maven.

1. Démarrer le serveur `autorite/HttpServer.java`
2. Avec Maven, démarrer le client avec :

```
mvn javafx:run
```

## Utilisation

- Le serveur affichera dans la console le code OTP à des fins de debbug
- Les mot de passe saisi doit être le mot de passe d'application du compte mail
- Ne fonctionne qu'avec des adresses Gmail
