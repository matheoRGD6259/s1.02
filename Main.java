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
            println("\nN° de profile\t Nom du soldat\t Total des scores");
            for(int i = 0; i < length(profiles, 1); i++) { /* Boucle permettant d'afficher tout les profils du fichier CSV */
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
            /* 
            
                Lorsque aucun profil est dans le fichier CSV,

            
            */
            animate(null, "Aucun profile n'a été sauvegardé actuellement.", true);
            animate(null, "Pour jouer, tapez \"1\" pour démarrer une nouvelle partie !", true);
            sleep(1000);
            algorithm();
        }
    }

    void nouvellePartie() {
        if(joueur == null) {
            String nomJoueur;
            do {
                animate(null, "Soldat, renseigne ton nom: ", false);
                nomJoueur = readString();
                int length = length(nomJoueur);
                if(length > 0) {
                    int i = 0;
                    boolean problem = false;
                    while(i < length && !problem) {
                        char c = charAt(nomJoueur, i);
                        if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                            i++;
                        } else {
                            problem = true;
                            nomJoueur = null;
                        }
                    }
                } else {
                    nomJoueur = null;
                }
                if(nomJoueur == null) {
                    animate(null, "Votre nom ne peut contenir uniquement des lettres et des chiffres !", true);
                    sleep(1000);
                }
            } while(nomJoueur == null);
        
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
                    voirNotes();
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

    void voirNotes() {
        animate(null, "\nVoici les différentes notes que vous avez obtenu:", true);
        sleep(500);
        for(int i = 0; i < length(joueur.scores); i++) {
            println(getThemeName(i) + ": " + joueur.scores[i] + "/10."); // Tout les scores autres que le test avec l'affichage suivant: "Nom du theme: note/10"
        }
        println("\nParcours du combattant: " + joueur.scoreTest + "/20.");
        println("\nAppuyez sur entrée pour retourner au menu principale.");
        readString();
        clean();
        nouvellePartie();
    }

    void chargerPartie() {
        String[][] profiles = getProfiles(false);
        if(profiles != null) {
            println("\nN° de profile\t Nom du soldat\t Total des scores");
            for(int i = 0; i < length(profiles, 1); i++) {
                String nom = profiles[i][0];
                int total = 0;
                for(int n = 1; n < length(profiles, 2); n++) {
                    total += stringToInt(profiles[i][n]);
                }
                print((i + 1) + ".   " + nom + "\t " + total + "\n");   
            }

            println("\n0.   Retour au menu principal.");
            int choix = choix();

            if(choix == 0) {
                algorithm();            // Retourner au menu principal du jeu
            } else if(choix >= 1 && choix <= length(profiles, 1)) { //Une ligne parmi la liste dans le fichier CSV des scores
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
        return checkChoix(r);
    }

    int checkChoix(String r) { //Vérifie si une lettres ou un caractère spécial est dans le String
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

    void test_checkChoix() {
        assertEquals(-1, checkChoix(""));
        assertEquals(-1, checkChoix("Bonjour"));
        assertEquals(10, checkChoix("10"));
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

    void score(int score) { //Permet d'afficher le score total
        if(qNombre == 10) {
            clean();
            afficherASCII(asciiMedaille(score), score);
        } else {
            println("\nVotre note est de " + score + "/" + qNombre + ".");
            if(score >= 15) {
                println("Votre note vous permet d'obtenir votre certificat !");
                sleep(2500);
                afficherASCII("./ressources/certificatparcours.txt");
            }
        }

        sleep(5000);
        println("Sauvegarde du score et de votre profile en cours...");
        if(scoreToChange != -1) {
            joueur.scores[scoreToChange] = score; //Change le score du thème actuelle
        } else {
            joueur.scoreTest = score; //Change le score du parcours du combattant (Test)
        }
        sauvegarderJoueur(joueur);
        println("Votre profile et votre score ont été sauvegardé !");
        sleep(2000);
        nouvellePartie();
    }

    String asciiMedaille(int score) { //Permet de récupéré le fichier texte avec l'ascii art de médaille en fonction du score
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
        int bonneReponse = theme.reponses[idQuestion]; //Enregistre la bonne réponse
        int choix;
        do {
            choix = choix();
            if(choix < 1 || choix > length(theme.choix, 2)) {
                animate("Lieutenant H.Reigner", syntaxe("Ne tente pas d'utiliser des joker ça n'existe pas ici ! Choisis une des propositions..."), true);
            }
        } while(choix < 1 || choix > length(theme.choix, 2)); //Tant que la proposition n'est pas un choix, la boucle s'exécute
        qNombre++;
        return choix == bonneReponse;
    }

    /*

                    DONNEE THEME (Création d'un thème etc...) 
    
    */

    int questionAleatoire(Theme theme) {
        int random = random(1, length(theme.questions)) - 1;
        while(dejaPose[getLignes(theme)][random]) {             //Choisir une question au hasard parmis la liste, 
            random = random(1, length(theme.questions)) - 1;    //et évite de remettre la même question si elle a déjà été posé
        }
        String question = theme.questions[random];
        println("Question n°" + qNombre + ": " + question + "\n");
        for(int j = 0; j < length(theme.choix, 2); j++) {
            println((j + 1) + ".  " + theme.choix[random][j] + "\n");
        }
        dejaPose[getLignes(theme)][random] = true; // Enregistré la questions dans les questions déjà posée
        return random;
    }

    Theme[] chargerTheme() { // Charge tout les thèmes qui existe dans le dossier questions
        Theme[] theme = new Theme[NB_THEME];
        for(int i = 0; i < NB_THEME; i++) {

            CSVFile themeFile = loadCSV("./ressources/data/questions/" + THEME[i], ';');
            Theme nTheme = newTheme(themeFile);
            for(int l = 1; l < rowCount(themeFile); l++) {
                for(int c = 1; c < columnCount(themeFile); c++) {
                    String cell = getCell(themeFile, l, c);
                    if(c == 1) { 
                        nTheme.questions[l - 1] = cell; // Range les questions dans un tableau
                    } else if(c == columnCount(themeFile) - 1) {
                        nTheme.reponses[l - 1] = stringToInt(cell); // Range les réponses à la même ligne que celle des questions
                    } else {
                        nTheme.choix[l - 1][c - 2] = cell; // Pareil pour les choix
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

    int getLignes(Theme theme) { // Retourne la ligne à laquelle le thème est associé
        int res;
        if(equals(theme.nom, "Première Guerre Mondiale")) {
            res = 0;
        } else if(equals(theme.nom, "Seconde Guerre Mondiale")) {
            res = 1;
        } else if(equals(theme.nom, "Guerre d'Algérie")) {
            res = 2;
        } else if(equals(theme.nom, "Guerre Froide")) {
            res = 3;
        } else if(equals(theme.nom, "Révolution Soviétique")){
            res = 4;
        } else {
            res = -1;
        }

        return res;
    }

    void test_getLignes() {
        assertEquals(0, getLignes(getTheme("Première Guerre Mondiale")));
        assertEquals(1, getLignes(getTheme("Seconde Guerre Mondiale")));
        assertEquals(2, getLignes(getTheme("Guerre d'Algérie")));
        assertEquals(3, getLignes(getTheme("Guerre Froide")));
        assertEquals(4, getLignes(getTheme("Révolution Soviétique")));
    }

    String getThemeName(int ligne) { // Retourne le nom du thème grâce à sa ligne
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

    Theme getTheme(String nom) { // Retourne le thème à partir de son nom
        Theme theme = null;
        for(int i = 0; i < NB_THEME; i++) {
            if(equals(themes[i].nom, nom)) {
                theme = themes[i];
            }
        }
        return theme;
    }

    void test_getTheme() {
        assertFalse(getTheme("Guerre du Vietnam") != null);
        assertTrue(getTheme("Première Guerre Mondiale") != null);
        assertTrue(getTheme("Seconde Guerre Mondiale") != null);
        assertTrue(getTheme("Guerre d'Algérie") != null);
        assertTrue(getTheme("Guerre Froide") != null);
        assertTrue(getTheme("Révolution Soviétique") != null);
    }

    /* 
    
                    DONNEE JOUEUR (Création d'un joueur, Chargement de données etc...) 
    
    */

    String[][] getProfiles(boolean csvMode) { // Récupéré les profiles sauvegardé
        int total = rowCount(SCORE) - 1;
        if(!csvMode) {
            if(total == 0) {
                return null; // Retourne null quand les lignes du CSV ne sont pas comptés et qu'il n'y a aucun profile
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

    void test_getProfiles() {
        SCORE = loadCSV("./ressources/data/testCSV.csv", ';');
        assertTrue(getProfiles(false) == null);

        SCORE = loadCSV("./ressources/data/testCSV2.csv", ';');
        assertTrue(getProfiles(false) != null);
    }

    Joueur loadPlayer(int ligne) { // Charge une sauvegarde
        joueur = null;
        if(ligne > 0 || ligne <= rowCount(SCORE)) {
            joueur = new Joueur();

            joueur.nom = getCell(SCORE, ligne, 0); // Charge les nom enregistré du joueur
            joueur.scores = new int[NB_THEME];
            for(int i = 0; i < NB_THEME; i++) {
                int tempInt = i + 1;
                joueur.scores[i] = stringToInt(getCell(SCORE, ligne, tempInt)); // Charge les score du joueur
            }
            joueur.scoreTest = stringToInt(getCell(SCORE, ligne, 6)); // Charge le score du tett du joueur
        }

        return joueur;
    }

    void test_loadPlayer() {
        SCORE = loadCSV("./ressources/data/testCSV2.csv", ';');
        assertTrue(loadPlayer(1) != null);
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

    void sauvegarderJoueur(Joueur joueur) { // Permet de sauvegarder un nouveau profile tout en gardant les anciens profile
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
        for(int i = 0; i < NB_THEME; i++) { // Ajoute le nouveau profile sur la dernière ligne ajouté au tableau
            int tempInt = i + 1;
            csvTab[rowCSV][tempInt] = "" + joueur.scores[i];
        }
        csvTab[rowCSV][6] = "" + joueur.scoreTest;
        qNombre = 1;
        saveCSV(csvTab, "./ressources/data/score.csv", ';'); // Écrase l'ancien fichier pour mettre la nouvelle version
        dejaPose = new boolean[length(themes)][15];
        SCORE = loadCSV("./ressources/data/score.csv", ';'); // Actualiser avec le dernier fichier des scores
    }

    String[][] ajouterLigneCSV(String[][] csv) {
        String[][] res = new String[length(csv, 1) + 1][length(csv, 2)]; // Créer un tableau de la même longueur que l'ancien + 1
        for(int l = 0; l < length(csv, 1); l++) {                        // Met les données du tableau csv
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

    void timer() { // Créer un compte à rebours de 3 secondes pour se sentir prêta avant une mission/test
        for(int i = 3; i > 0; i--) {
            animate(null, i + "... ", false);
            sleep(875);
        }
    }

    void clean() { // Permet de nettoyer la carte (Supprimé artificiellement les messages)
        for(int i = 0 ; i < 1000; i++) {
            println("");
            println("");
        }
    }

    void animate(String prefix, String text, boolean newLine) { // Permet de créer une animation sur les texte
        if(prefix != null) print(prefix + " : ");               // Le prefix permet d'afficher un texte avant que le texte animé se lance
        for(int i = 0 ; i < length(text); i++) {
            print(charAt(text, i));
            sleep(25);
        }
        if(newLine) print("\n"); // Permet de passer une ligne si le développeur le souhaite
    }

    String replace(String string, String pattern, String replacement) { // Permet de remplacer un patron par un autre
        String res = string;
        int index = indexOf(string, pattern); // Récupère l'emplacement du patron

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

    String syntaxe(String texte) { // Permet de créer les fichiers des dialogues par exemple
        return replace(texte, "{@}", joueur.nom); // Remplacer "{@}" par le nom du joueur
    }

    void test_syntaxe() {
        joueur = newJoueur("null");
        String syntaxeText1 = "{@}, je suis ravi de te voir ici !";
        String syntaxeText2 = "Oh ! Il est de retour notre cher {@} !!";
        assertEquals(syntaxe(syntaxeText1), "null, je suis ravi de te voir ici !");
        assertEquals(syntaxe(syntaxeText2), "Oh ! Il est de retour notre cher null !!");
    }

    void afficherDialogue(String fichier) { // Permet de lire un fichier de Dialogue
        File file = new File(fichier);
        String prefix = readLine(file); // La première ligne du fichier est le prefix, donc le nom d'un personnage

        while(ready(file)) { // Lecture du dialogue, avec l'animation
            String ligne = readLine(file);
            animate(prefix, syntaxe(ligne), true);
        }
    }

    void afficherASCII(String chemin) { // Permet d'afficher un art ASCII dans la console
        File file = new File(chemin);
        while(ready(file)) {
            String ligne = readLine(file);
            println(ligne);
        }
    }

    void afficherASCII(String chemin, int score) { // Permet d'afficher un art ASCII dans la console en remplaçant "{*}" par le score
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