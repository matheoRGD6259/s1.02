import extensions.CSVFile;

class questionv2 extends Program {

    // Fonction qui choisit un nom de fichier au hasard
    String fichierhasard() {
        int fich = random(0,4);  

        String res = "";
        if (fich == 0) {
            res = "1GM.csv";
        } else if (fich == 1) {
            res = "2GM.csv";
        } else if (fich == 2) {
            res = "algerie.csv";
        } else if (fich == 3) {
            res = "revolution sovietique.csv";
        } else if (fich == 4){
            res = "guerre froide.csv";
        }
        return res;
    }


    String inttoString(int reponse){
       return "" + reponse;
    }


    // La fonction note prend maintenant le fichier CSV déjà chargé en paramètre
    int qts(String fichierhasard) {
        
        loadCSV questions = loadCSV(fichierhasard)
        
        int nbLignes = rowCount(questions);
        
        int questionhasard = random(1,nbLignes);

        String theme = getCell(questions, questionhasard, 0);
        String texteQuestion = getCell(questions, questionhasard, 1);
        String rep1 = getCell(questions, questionhasard, 2);
        String rep2 = getCell(questions, questionhasard, 3);
        String rep3 = getCell(questions, questionhasard, 4);
        String bonneReponse = getCell(questions, questionhasard, 5);

        // Affichage
        println("La question suivante sera sur le thème : " + theme);
        println(texteQuestion);
        println("1- " + rep1);
        println("2- " + rep2);
        println("3- " + rep3);

        // Saisie de la réponse et assure que c'est le bon format
        int reponse = readInt();
        while (reponse < 1 && reponse > 3) {
            println("Lieutenant H.Reigner : Idiot, choisis une des options proposées !");
            reponse = readInt();
        }

        // Verifie si la reponse est bonne
        if (equals(bonneReponse,inttoString(reponse)){
            return 1;
        } else {
            return 0;
        }
    }

 