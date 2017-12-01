import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {
    JsonReader jsonReader = new JsonReader();

    void mostraMenu() {
        int i;
        do {
            System.out.println();
            System.out.println("1. Cerca de Resultats");
            System.out.println("2. Desar Preferits");
            System.out.println("3. Millors Videos");
            System.out.println("4. Estadístiques");
            System.out.println("5. Llistes de Reproducció");
            System.out.println("6. El Mosaic");
            System.out.println("7. Sortir\n");
            System.out.println("\nSel·lecciona una opcio:");

            Scanner read = new Scanner(System.in);
            i = read.nextInt();

            while (i < 1 || i > 7) {
                System.out.println("Opcio del menu incorrecta! Introdueix l'opcio de nou:");
                i = read.nextInt();
            }

            switch (i){
                case 1:
                    opcio1();
                    break;
                case 2:
                    opcio2();
                    break;
                case 3:
                    opcio3();
                    break;
                case 4:
                    opcio4();
                    break;
                case 5:
                    opcio5();
                    break;
                case 6:
                    opcio6();
                    break;
            }
        }while(i != 7);
    }

    private void opcio1() {
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm + "&key=AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";
        try {
            JsonObject json = jsonReader.getJsonFromURL(url);

            for (int i = 0 ; i < json.size() && i < 3; i++){
                System.out.println("~~~~Resultat numero " + (i + 1) + "~~~~");
                System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
                System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
                System.out.println("    Descripcio: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
                System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void opcio2() {

    }

    private void opcio3() {
    }

    private void opcio4() {

    }

    private void opcio5() {

    }

    private void opcio6() {

    }

}
