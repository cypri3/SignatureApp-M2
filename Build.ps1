# Définition des variables
$SRC_DIR = "src"
$BIN_DIR = "bin"
$LIB_DIR = "lib"
$MAIN_CLASS = "SignatureApp"

# Création du dossier bin s'il n'existe pas
if (-Not (Test-Path $BIN_DIR)) {
    New-Item -ItemType Directory -Path $BIN_DIR | Out-Null
}

# Fonction pour compiler le projet
function Compile {
    Write-Host "Compilation en cours..." -ForegroundColor Cyan
    javac -d $BIN_DIR -cp "$LIB_DIR/*" "$SRC_DIR/*.java"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation terminee avec succes." -ForegroundColor Green
    } else {
        Write-Host "Erreur lors de la compilation." -ForegroundColor Red
        exit 1
    }
}

# Fonction pour exécuter le programme principal
function Run {
    Compile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Exécution du programme..." -ForegroundColor Cyan
        java -cp "$BIN_DIR;$LIB_DIR/*" $MAIN_CLASS
    } else {
        Write-Host "Impossible d'exécuter en raison d'erreurs de compilation." -ForegroundColor Red
    }
}

# Fonction pour nettoyer les fichiers compilés
function Clean {
    Write-Host "Suppression des fichiers compilés..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force "$BIN_DIR\*.class" -ErrorAction SilentlyContinue
    Write-Host "Nettoyage terminé." -ForegroundColor Green
}

# Menu pour sélectionner l'action
if ($args.Count -eq 0) {
    Write-Host "Usage: .\build.ps1 [compile|run|clean|help]" -ForegroundColor White
    exit
}

switch ($args[0]) {
    "compile" { Compile }
    "run"     { Run }
    "clean"   { Clean }
    "help" {
        Write-Host "Commandes disponibles :" -ForegroundColor White
        Write-Host "  .\build.ps1 compile   - Compile le projet" 
        Write-Host "  .\build.ps1 run       - Compile et exécute le programme"
        Write-Host "  .\build.ps1 clean     - Supprime les fichiers compilés"
        Write-Host "  .\build.ps1 help      - Affiche cette aide"
    }
    Default {
        Write-Host "Commande inconnue : $($args[0]). Utilisez 'help' pour voir les options." -ForegroundColor Red
    }
}
