import extensions.File;
import extensions.CSVFile;

class Main extends Program {

    CSVFile SCORE = loadCSV("./ressources/data/score.csv", ';');
    final String[] THEME = getAllFilesFromDirectory("./ressources/data/questions/");
    int NB_THEME = length(THEME);

    Theme[] themes = chargerTheme();
    boolean[][] dejaPose = new boolean[length(themes)][15];

    Joueur joueur;
    Theme themeActuelle;

    int rowCSV = -1;
    int scoreToChange;

    int qNombre;

    /* 
    
                    MANAGER DU JEU (Nouvelle Partie, Tableau des scores etc...) 
                    
    */

    void tableauScore() {

        String[][] profiles = getProfiles(false);
        if(profiles != null) {
            println("\nNom du soldat\t Total des scores");
            for(int i = 0; i < length(profiles, 1); i++) {
                String nom = profiles[i][0];
                int total = 0;
                for(int n = 1; n < length(profiles, 2); n++) {
                    total += stringToInt(profiles[i][n]);
                }
                print((i + 1) + ".   " + nom + "\t " + total + "\n");   
            }

            println("\nAppuyez sur entrée pour retourner au menu principale.");
            readString();
            algorithm();
        } else {
            animate(null, "Aucun profile n'a été sauvegardé actuellement.", true);
            animate(null, "Pour jouer, tapez \"1\" pour démarrer une nouvelle partie !", true);
            sleep(1000);
            algorithm();
        }
    }

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
        if(choix >= 1 && choix <= 5) {
            if(choix < 3) {
                int num;
                if(choix == 2) {
                    num = themeAuChoix();
                } else {
                    num = random(1, length(themes));
                }
                if(num == 6) {
                    nouvellePartie();
                } else {
                    qNombre = 1;
                    jouer(num - 1);
                }
            } else {
                if(choix == 3) {
                    parcours();
                } else if(choix == 4) {

                } else {
                    joueur = null;
                    algorithm();
                }
            }
        } else {
            animate("Lieutenant H.Reigner", syntaxe("Ces missions ne sont pas ouverte pour toi ! Choisis une mission que tu peux faire soldat {@}..."), true);
            sleep(500);
            clean();
            nouvellePartie();
        }
    }

    void chargerPartie() {
        String[][] profiles = getProfiles(false);
        if(profiles != null) {
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
                algorithm();
            } else if(choix >= 1 && choix <= length(profiles, 1)) {
                joueur = loadPlayer(choix);
                rowCSV = choix;
                continuerPartie();
            }
        } else {
            animate(null, "Aucun profile n'a été sauvegardé actuellement.", true);
            animate(null, "Pour jouer, tapez \"1\" pour démarrer une nouvelle partie !", true);
            sleep(1000);
            algorithm();
        }
    }

    int choix() {
        print("\nVotre choix : ");
        String r = readString();
        if(length(r) < 1) {
            r = "-1";
        } else {
            int idx = 0;
            boolean breaker = false;
            do {
                char c = charAt(r, idx);
                if(!(c >= '0' && c <= '9')) {
                    r = "-1";
                    breaker = true;
                }
                idx++;
            } while(idx < length(r) && !breaker);
        }

        return stringToInt(r);
    }
    
    int themeAuChoix() {
        int themeAuChoix;
        do {
            animate("Lieutenant H.Reigner", syntaxe("Très bien soldat ! Tourner la page et choisissez votre position sur le front et plus vite que ça !"), true);
            afficherASCII("./ressources/menu/theme.txt");
            println("");
            themeAuChoix = choix();
        } while(themeAuChoix < 1 || themeAuChoix > 6);
        return themeAuChoix;
    }

    void jouer(int theme) {
        themeActuelle = getTheme(getThemeName(theme));
        scoreToChange = theme;
        animate("Lieutenant H.Reigner", "Le thème sélectionner est la " + themeActuelle.nom, true);
        animate(null, "Le jeu commence dans ", false);
        timer();
        println("\n");
        int score = 0;
        for(int i = 0; i < 10; i++) {
            int question = questionAleatoire(themeActuelle);
            if(reponseCorrect(themeActuelle, question)) {
                score++;
                animate("Lieutenant H.Reigner", syntaxe("Réponse correct, mais ne te relache pas le bleu."), true);
            } else {
                animate("Lieutenant H.Reigner", syntaxe("C'est faux, concentre-toi soldat {@} !"), true);
            }
        }
        score(score);
    }

    void score(int score) {
        if((qNombre - 1) == 10) {
            clean();
            afficherASCII(asciiMedaille(score), score);
        } else {
            println("\nVotre note est de " + score + "/" + (qNombre - 1) + ".\n");
        }

        sleep(5000);
        println("Sauvegarde du score et de votre profile en cours...");
        if(scoreToChange != -1) {
            joueur.scores[scoreToChange] = score;
        } else {
            joueur.scoreTest = score;
        }
        sauvegarderJoueur(joueur);
        println("Votre profile et votre score ont été sauvegardé !");
        sleep(2000);
        nouvellePartie();
    }

    String asciiMedaille(int score) {
        String file;
        if(score < 5) {
            file = "./ressources/medailles/medaille0.txt";
        } else if(score < 8) {
            file = "./ressources/medailles/medaille1.txt";
        } else if(score < 10) {
            file = "./ressources/medailles/medaille2.txt";
        } else {
            file = "./ressources/medailles/medaille3.txt";
        }
        return file;
    }

    void test_asciiMedaille() {
        assertEquals(asciiMedaille(2), "./ressources/medailles/medaille0.txt");
        assertEquals(asciiMedaille(6), "./ressources/medailles/medaille1.txt");
        assertEquals(asciiMedaille(9), "./ressources/medailles/medaille2.txt");
        assertEquals(asciiMedaille(10), "./ressources/medailles/medaille3.txt");
    }

    void parcours() {
        scoreToChange = -1;
        afficherDialogue("./ressources/dialogue/parcours.txt");
        animate(null, "Le parcours du combattant commence dans ", false);
        timer();
        println("\n");
        int score = 0;
        for(int i = 0; i < 20; i++) {
            int epreuve = (i / 2) + 1;
            afficherASCII("./ressources/epreuves/epreuve" + epreuve + ".txt");

            int random = random(1, length(themes)) - 1;
            themeActuelle = getTheme(getThemeName(random));
            int question = questionAleatoire(themeActuelle);
            if(reponseCorrect(themeActuelle, question)) {
                score++;
            }
        }
        score(score);   
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
        qNombre++;
        return choix == bonneReponse;
    }

    /*

                    DONNEE THEME (Création d'un thème etc...) 
    
    */

    int questionAleatoire(Theme theme) {
        int random = random(1, length(theme.questions)) - 1;
        while(dejaPose[getLignes(theme)][random]) {
            random = random(1, length(theme.questions)) - 1;
        }
        String question = theme.questions[random];
        println("Question n°" + qNombre + ": " + question + "\n");
        for(int j = 0; j < length(theme.choix, 2); j++) {
            println((j + 1) + ".  " + theme.choix[random][j] + "\n");
        }
        dejaPose[getLignes(theme)][random] = true;
        return random;
    }

    Theme[] chargerTheme() {
        Theme[] theme = new Theme[NB_THEME];
        for(int i = 0; i < NB_THEME; i++) {

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

    int getLignes(Theme theme) {
        int res;
        if(equals(theme.nom, "Première Guerre Mondiale")) {
            res = 0;
        } else if(equals(theme.nom, "Seconde Guerre Mondiale")) {
            res = 1;
        } else if(equals(theme.nom, "Guerre d'Algérie")) {
            res = 2;
        } else if(equals(theme.nom, "Guerre Froide")) {
            res = 3;
        } else {
            res = 4;
        }

        return res;
    }

    String getThemeName(int ligne) {
        String nom;

        if(ligne == 0) {
            nom = "Première Guerre Mondiale";
        } else if(ligne == 1) {
            nom = "Seconde Guerre Mondiale";
        } else if(ligne == 2) {
            nom = "Guerre d'Algérie";
        } else if(ligne == 3) {
            nom = "Guerre Froide";
        } else {
            nom = "Révolution Soviétique";
        }

        return nom;
    }

    void test_getThemeName() {
        assertEquals("Première Guerre Mondiale", getThemeName(0));
        assertEquals("Seconde Guerre Mondiale", getThemeName(1));
        assertEquals("Guerre d'Algérie", getThemeName(2));
        assertEquals("Guerre Froide", getThemeName(3));
        assertEquals("Révolution Soviétique", getThemeName(4));
    }

    Theme getTheme(String nom) {
        Theme theme = null;
        for(int i = 0; i < NB_THEME; i++) {
            if(equals(themes[i].nom, nom)) {
                theme = themes[i];
            }
        }
        return theme;
    }

    /* 
    
                    DONNEE JOUEUR (Création d'un joueur, Chargement de données etc...) 
    
    */

    String[][] getProfiles(boolean csvMode) {
        int total = rowCount(SCORE) - 1;
        if(!csvMode) {
            if(total == 0) {
                return null;
            }
        }
        int idx = 1;
        if(csvMode) {
            total = rowCount(SCORE);
            idx = 0;
        }
        String[][] profiles = new String[total][7];

        for(int l = idx; l < rowCount(SCORE); l++) {
            int pos = l - 1;
            if(csvMode) {
                pos = l;
            }
            for(int c = 0; c < columnCount(SCORE); c++) {
                String cellValue = getCell(SCORE, l, c);
                profiles[pos][c] = cellValue;
            }
        }

        return profiles;
    }

    Joueur loadPlayer(int ligne) {
        Joueur joueur = new Joueur();

        joueur.nom = getCell(SCORE, ligne, 0);
        joueur.scores = new int[NB_THEME];
        for(int i = 0; i < NB_THEME; i++) {
            int tempInt = i + 1;
            joueur.scores[i] = stringToInt(getCell(SCORE, ligne, tempInt));
        }
        joueur.scoreTest = stringToInt(getCell(SCORE, ligne, 6));

        return joueur;
    }

    Joueur newJoueur(String nom) {
        Joueur joueur = new Joueur();
        joueur.nom = nom;

        joueur.scores = new int[NB_THEME];
        for(int i = 0; i < NB_THEME; i++) {
            joueur.scores[i] = 0;
        }
        return joueur;
    }

    void sauvegarderJoueur(Joueur joueur) {
        String[][] csvTab;
        int lineIdx;
        if(rowCSV != -1) {
            csvTab = getProfiles(true); 
            lineIdx = rowCSV;
        } else {
            csvTab = ajouterLigneCSV(getProfiles(true));
            lineIdx = length(csvTab, 1) - 1;
            rowCSV = lineIdx;
        }
        csvTab[rowCSV][0] = joueur.nom;
        for(int i = 0; i < NB_THEME; i++) {
            int tempInt = i + 1;
            csvTab[rowCSV][tempInt] = "" + joueur.scores[i];
        }
        csvTab[rowCSV][6] = "" + joueur.scoreTest;
        saveCSV(csvTab, "./ressources/data/score.csv", ';');
        dejaPose = new boolean[length(themes)][15];
        SCORE = loadCSV("./ressources/data/score.csv", ';');
    }

    String[][] ajouterLigneCSV(String[][] csv) {
        String[][] res = new String[length(csv, 1) + 1][length(csv, 2)];
        for(int l = 0; l < length(csv, 1); l++) {
            for(int c = 0; c < length(csv, 2); c++) {
                res[l][c] = csv[l][c];
            }
        }

        return res;
    }

    void test_ajouterLigneCSV() {
        String[][] test = new String[7][5];
        assertEquals(8, length(ajouterLigneCSV(test), 1));
        assertFalse(length(ajouterLigneCSV(test), 1) == 10);
    }

    /* Utilitaires */

    void timer() {
        for(int i = 3; i > 0; i--) {
            animate(null, i + "... ", false);
            sleep(875);
        }
    }

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

    void test_syntaxe() {
        joueur = newJoueur("null");
        String syntaxeText1 = "{@}, je suis ravi de te voir ici !";
        String syntaxeText2 = "Oh ! Il est de retour notre cher {@} !!";
        assertEquals(syntaxe(syntaxeText1), "null, je suis ravi de te voir ici !");
        assertEquals(syntaxe(syntaxeText2), "Oh ! Il est de retour notre cher null !!");
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

    void afficherASCII(String chemin, int score) {
        File file = new File(chemin);
        while(ready(file)) {
            String ligne = readLine(file);
            println(replace(ligne, "{*}", "" + score));
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
            tableauScore();
        } else if(choix == 4) {
            println("Merci d'avoir lancé Quiz'toir de Guerre...");
            println("Développé par LEROI Adrien et RINGARD Mathéo !");
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