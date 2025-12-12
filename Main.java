import extensions.File;
import extensions.CSVFile;

class Main extends Program {

    Joueur joueur;
    Theme themeActuelle;

    /* MANAGER DU JEU (Nouvelle Partie, Tableau des scores etc...) */

    void nouvellePartie() {
        animate(null, "Soldat, renseigne ton nom: ", false);
        String nomJoueur = readString();
        joueur = newJoueur(nomJoueur);

        println("");
        afficherDialogue("./ressources/dialogue/nouvellePartie.txt");
        println("");

    }

    void continuerPartie() {
        println("");
        afficherDialogue("./ressources/dialogue/retour.txt");
        println("");

    }

    void chargerPartie() {
        String[][] profiles = getProfiles();
        for(int i = 0; i < length(profiles, 1); i++) {
            String nom = profiles[i][0];
            print((i + 1) + ".   " + nom + " | ");
            for(int n = 1; n < length(profiles, 2); n++) {
                print(profiles[i][n] + " ");
            }
            println("");
        }

        println("\n0.   Retour au menu principal.");
        print("\nVotre choix : ");
        int choix = readInt();

        if(choix == 0) {
            clean();
            sys(true);
        } else if(choix >= 1 && choix <= length(profiles, 1)) {
            joueur = loadPlayer(choix - 1);
        }
    }

    /* DONNEE JOUEUR (Création d'un joueur, Chargement de données etc...) */

    String[][] getProfiles() {
        CSVFile score = loadCSV("./ressources/score/score.csv", ';');
        String[][] profiles = new String[rowCount(score) - 1][7];

        for(int l = 1; l < rowCount(score); l++) {
            for(int c = 0; c < columnCount(score); c++) {
                String cellValue = getCell(score, l, c);
                profiles[l - 1][c] = cellValue;
            }
        }

        return profiles;
    }

    Joueur loadPlayer(int ligne) {
        Joueur joueur = new Joueur();
        CSVFile score = loadCSV("./ressources/score/score.csv");

        joueur.nom = getCell(score, ligne, 0);
        joueur.scorePGM = stringToInt(getCell(score, ligne, 1));
        joueur.scoreSGM = stringToInt(getCell(score, ligne, 2));
        joueur.scoreGAL = stringToInt(getCell(score, ligne, 3));
        joueur.scoreGF = stringToInt(getCell(score, ligne, 4));
        joueur.scoreRSV = stringToInt(getCell(score, ligne, 5));
        joueur.scoreTest = stringToInt(getCell(score, ligne, 6));

        return joueur;
    }

    Joueur newJoueur(String nom) {
        Joueur joueur = new Joueur();
        joueur.nom = nom;
        joueur.scorePGM = 0;
        joueur.scoreSGM = 0;
        joueur.scoreGAL = 0;
        joueur.scoreGF = 0;
        joueur.scoreRSV = 0;
        joueur.scoreTest = 0;
        return joueur;
    }

    /* Utilitaires */

    void clean() {
        for(int i = 0 ; i < 2000; i++) {
            println("");
        }
    }

    void animate(String prefix, String text, boolean newLine) {
        if(prefix != null) print(prefix + " : "); 
        for(int i = 0 ; i < length(text); i++) {
            print(charAt(text, i));
            sleep(25);
        }
        if(newLine) print("\n");
    }

    String replace(String string, String pattern, String replacement) {
        String res = string;
        int index = indexOf(string, pattern);

        if(index != -1) {
            String firstPart = substring(string, 0, index);
            String lastPart = substring(string, index + length(pattern), length(string));
            res = firstPart + replacement + lastPart;
        }

        return res;
    }

    void test_replace() {
        String textTest = "Bonjour je suis un texte pour tester le remplacement.";

        assertTrue(equals(replace(textTest, "je suis", "je ne suis pas"), "Bonjour je ne suis pas un texte pour tester le remplacement."));
        assertFalse(equals(replace(textTest, "Bonvour", "Hello"), "Hello je suis un texte pour tester le remplacement."));
    }

    String syntaxe(String texte) {
        return replace(texte, "{@}", joueur.nom);
    }

    void afficherDialogue(String fichier) {
        File file = new File(fichier);
        String prefix = readLine(file);

        while(ready(file)) {
            String ligne = readLine(file);
            animate(prefix, syntaxe(ligne), true);
        }
    }

    void afficherASCII(String chemin) {
        File file = new File(chemin);
        while(ready(file)) {
            String ligne = readLine(file);
            println(ligne);
        }
    }

    int lancement() {
        afficherASCII("./ressources/menu/menu.txt");

        print("\nVotre choix : ");
        int choix = readInt();
        return choix;
    }

    void sys(boolean start) {
        int choix;
        if(start) {
            choix = lancement();
        } else {
            print("Votre choix : ");
            choix = readInt();
        }
        if(choix == 1) {
            nouvellePartie();
        } else if(choix == 2) {
            chargerPartie();
        } else if(choix == 3) {

        }  else if(choix == 4) {
            println("Merci d'avoir lancé Quiz'toir de Guerre...");
        } else {
            animate(null, "Le nombre entrer n'est pas un choix !", true);
            sys(false);
        }
    }

    void algorithm() {
        clean();
        sys(true);
    }
}