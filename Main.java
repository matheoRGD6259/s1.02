import extensions.File;

class Main extends Program {

    String nomJoueur;
    Theme themeActuelle;

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
        String firstPatternChar = charAt(pattern, 0) + "";
        String lastPatternChar = charAt(pattern, length(pattern) - 1) + "";
        
        int firstIndex = indexOf(string, firstPatternChar);
        int lastIndex = indexOf(string, lastPatternChar);

        if(firstIndex != -1 && lastIndex != -1) {
            String firstPart = substring(string, 0, firstIndex);
            String lastPart = substring(string, lastIndex + 1, length(string));
            res = firstPart + replacement + lastPart;
        }

        return res;
    }

    String syntaxe(String texte) {
        return replace(texte, "{@}", nomJoueur);
    }

    void afficherDialogue(String fichier) {
        File file = new File(fichier);
        String prefix = readLine(file);

        while(ready(file)) {
            String ligne = readLine(file);
            animate(prefix, syntaxe(ligne), true);
        }
    }

    void nouvellePartie() {
        animate(null, "Soldat, renseigne ton nom: ", false);
        nomJoueur = readString();
        println("");
        afficherDialogue("./dialogue/nouvellePartie.txt");
        println("");

    }

    void afficherASCII(String chemin) {
        File file = new File(chemin);
        while(ready(file)) {
            String ligne = readLine(file);
            println(ligne);
        }
    }

    int lancement() {
        afficherASCII("./art/menu.txt");

        print("\nVotre choix: ");
        int choix = readInt();
        return choix;
    }

    void sys(boolean start) {
        int choix;
        if(start) {
            choix = lancement();
        } else {
            print("Votre choix: ");
            choix = readInt();
        }
        if(choix == 1) {
            nouvellePartie();
        } else if(choix == 2) {

        } else if(choix == 3) {

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