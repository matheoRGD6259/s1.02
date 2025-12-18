import extensions.File;
import extensions.CSVFile;

class Main extends Program {

    final CSVFile SCORE = loadCSV("./ressources/data/score.csv", ';');
    final String[] THEME = getAllFilesFromDirectory("./ressources/data/questions/");

    Theme[] themes = chargerTheme();

    Joueur joueur;
    Theme themeActuelle;


    /* MANAGER DU JEU (Nouvelle Partie, Tableau des scores etc...) */

    void nouvellePartie() {
        if(joueur == null) {
            animate(null, "Soldat, renseigne ton nom: ", false);
            String nomJoueur = readString();
            joueur = newJoueur(nomJoueur);
            afficherDialogue("./ressources/dialogue/nouvellePartie.txt");
            println("");
        }
        afficherASCII("./ressources/menu/missions.txt");

        println("");
        choixMenu();

    }

    void continuerPartie() {
        clean();
        afficherDialogue("./ressources/dialogue/retour.txt");
        println("");
        afficherASCII("./ressources/menu/missions.txt");

        println("");
        choixMenu();
    }

    void choixMenu() {
        int choix = choix();
        if(choix >= 1 && choix <= 4) {
            if(choix >= 3) {
                animate("Lieutenant H.Reigner", syntaxe("Malheureusement... Ce campement est en cours d'installation soldat {@}..."), true);
                sleep(500);
                clean();
                nouvellePartie();
            } else {
                int num;
                if(choix == 2) {
                    num = themeAuChoix();
                } else {
                    num = random(1, length(themes));
                }
                jouer(num - 1);
            }
        } else {
            animate("Lieutenant H.Reigner", syntaxe("Ces missions ne sont pas ouverte pour toi ! Choisis une mission que tu peux faire soldat {@}..."), true);
            sleep(500);
            clean();
            nouvellePartie();
        }
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
        int choix = choix();

        if(choix == 0) {
            clean();
            sys(true);
        } else if(choix >= 1 && choix <= length(profiles, 1)) {
            joueur = loadPlayer(choix);
            continuerPartie();
        }
    }

    int choix() {
        print("\nVotre choix : ");
        return readInt();
    }
    
    int themeAuChoix() {
        int themeAuChoix;
        do {
            animate("Lieutenant H.Reigner", syntaxe("Très bien soldat ! Tourner la page et choisissez votre position sur le front et plus vite que ça !"), true);
            afficherASCII("./ressources/menu/theme.txt");
            println("");
            themeAuChoix = choix();
        } while(themeAuChoix < 1 || themeAuChoix > 5);
        return themeAuChoix;
    }

    void jouer(int theme) {
        themeActuelle = themes[theme];
        animate("Lieutenant H.Reigner", "Le thème sélectionner est la " + themeActuelle.nom, true);
        animate(null, "Le jeu commence dans ", false);
        for(int i = 3; i > 0; i--) {
            animate(null, i + "... ", false);
            sleep(875);
        }
        println("\n");
        int score = 0;
        for(int i = 0; i < 10; i++) {
            int random = random(1, length(themeActuelle.questions)) - 1;
            String question = themeActuelle.questions[random];
            println("Question n°" + (i+1) + ": " + question + "\n");
            for(int j = 0; j < length(themeActuelle.choix, 2); j++) {
                println((j + 1) + ".  " + themeActuelle.choix[random][j] + "\n");
            }
            if(reponseCorrect(themeActuelle, random)) {
                score++;
                animate("Lieutenant H.Reigner", syntaxe("Réponse correct, mais ne te relache pas le bleu."), true);
            } else {
                animate("Lieutenant H.Reigner", syntaxe("C'est faux, concentre-toi soldat {@} !"), true);
            }
        }
        println("\nVotre note est de " + score + "/10.");
    }

    boolean reponseCorrect(Theme theme, int idQuestion) {
        int bonneReponse = theme.reponses[idQuestion];
        int choix;
        do {
            choix = choix();
            if(choix < 1 || choix > length(theme.choix, 2)) {
                animate("Lieutenant H.Reigner", syntaxe("Ne tente pas d'utiliser des joker ça n'existe pas ici ! Choisis une des propositions..."), true);
            }
        } while(choix < 1 || choix > length(theme.choix, 2));
        return choix == bonneReponse;
    }


    /* DONNEE THEME (Création d'un thème etc...) */

    Theme[] chargerTheme() {
        Theme[] theme = new Theme[length(THEME)];
        for(int i = 0; i < length(THEME); i++) {

            CSVFile themeFile = loadCSV("./ressources/data/questions/" + THEME[i], ';');
            Theme nTheme = newTheme(themeFile);
            for(int l = 1; l < rowCount(themeFile); l++) {
                for(int c = 1; c < columnCount(themeFile); c++) {
                    String cell = getCell(themeFile, l, c);
                    if(c == 1) { 
                        nTheme.questions[l - 1] = cell; 
                    } else if(c == columnCount(themeFile) - 1) {
                        nTheme.reponses[l - 1] = stringToInt(cell);
                    } else {
                        nTheme.choix[l - 1][c - 2] = cell;
                    }
                }
            }
            theme[i] = nTheme;
        }

        return theme;
    }

    Theme newTheme(CSVFile file) {
        Theme theme = new Theme();
        int row = rowCount(file);
        int collumn = columnCount(file);

        theme.nom = getCell(file, 1, 0);
        theme.questions = new String[row - 1];
        theme.choix = new String[row - 1][collumn - 3];
        theme.reponses = new int[row - 1];

        return theme;
    }

    /* DONNEE JOUEUR (Création d'un joueur, Chargement de données etc...) */

    String[][] getProfiles() {
        String[][] profiles = new String[rowCount(SCORE) - 1][7];

        for(int l = 1; l < rowCount(SCORE); l++) {
            for(int c = 0; c < columnCount(SCORE); c++) {
                String cellValue = getCell(SCORE, l, c);
                profiles[l - 1][c] = cellValue;
            }
        }

        return profiles;
    }

    Joueur loadPlayer(int ligne) {
        Joueur joueur = new Joueur();

        joueur.nom = getCell(SCORE, ligne, 0);
        joueur.scorePGM = stringToInt(getCell(SCORE, ligne, 1));
        joueur.scoreSGM = stringToInt(getCell(SCORE, ligne, 2));
        joueur.scoreGAL = stringToInt(getCell(SCORE, ligne, 3));
        joueur.scoreGF = stringToInt(getCell(SCORE, ligne, 4));
        joueur.scoreRSV = stringToInt(getCell(SCORE, ligne, 5));
        joueur.scoreTest = stringToInt(getCell(SCORE, ligne, 6));

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
        for(int i = 0 ; i < 1000; i++) {
            println("");
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
        return choix();
    }

    void sys(boolean start) {
        if(start) afficherASCII("./ressources/menu/menu.txt");
        int choix = choix();

        if(choix == 1) {
            clean();
            nouvellePartie();
        } else if(choix == 2) {
            chargerPartie();
        } else if(choix == 3) {

        } else if(choix == 4) {
            println("Merci d'avoir lancé Quiz'toir de Guerre...");
        } else {
            animate(null, "Le nombre que vous avez entré n'est pas un choix !", true);
            sys(false);
        }
    }

    void algorithm() {
        clean();
        sys(true);
    }
}