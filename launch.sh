#!/bin/bash

echo "Compilation de Quizz'toire de Guerre..."

IJAVA=$(find / -name "ijava.jar" -readable -print -quit 2>/dev/null)

COMPILE=$(java -jar $IJAVA compile Main.java 2>&1)
if echo "$COMPILE" | grep -q "Compilation successful"; then
    echo "Compilation réussite !";
    echo "Démarrage de Quizz'toire de Guerre..."
    java -jar $IJAVA execute Main
else
    echo "Une erreur s'est produite lors de la compilation de Quizz'toir de Guerre:"
    echo "$COMPILE"
    exit 1
fi