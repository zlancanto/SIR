# Utiliser une image de base avec Java 17
FROM maven:3.8.4-openjdk-17

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le reste du code source
COPY src ./src

# Compiler l'application
RUN mvn package

# Exposer le port si nécessaire (si votre application est un serveur)
EXPOSE 8080

# Commande pour lancer l'application
CMD ["java", "-jar", "target/testjpa-0.0.1-SNAPSHOT.jar"]
