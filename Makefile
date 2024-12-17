# Variables
SRC = src/*.java
BIN = bin
LIB = lib/*
MAIN = Projet

# Règle par défaut : compile
all: compile

# Compilation des fichiers source
compile:
	javac -d $(BIN) -cp "$(LIB)" $(SRC)

# Exécution du programme principal
run: compile
	java -cp "$(BIN):$(LIB)" $(MAIN)

# Nettoyage des fichiers compilés
clean:
	rm -rf $(BIN)/*

# Aide pour les commandes disponibles
help:
	@echo "Commandes disponibles :"
	@echo "  make        - Compile le projet"
	@echo "  make run    - Compile et exécute le programme"
	@echo "  make clean  - Supprime les fichiers compilés"
