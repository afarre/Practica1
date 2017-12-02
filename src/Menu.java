import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonArray jsonArray = new JsonArray();
    private String API_KEY = "AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";

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
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm + "&key=" + API_KEY;
        try {
            JsonObject json = jsonReader.getJsonFromURL(URL);

            for (int i = 0 ; i < 3; i++){
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
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=50";
        try {
            JsonObject json = jsonReader.getJsonFromURL(URL);

            int i = 0;
            mostraResultats(i, json);

            System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
            read = new Scanner(System.in);
            String guardar = read.nextLine();
            do {
                if (guardar.toUpperCase().equals("NEXT")){
                    i = i + 10;
                    mostraResultats(i, json);
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.toUpperCase().equals("BACK")){
                    i = i - 10;
                    mostraResultats(i, json);
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.toUpperCase().equals("STOP")){
                    return;
                }else if (isNumeric(guardar)){
                    int intGuardar = Integer.parseInt(guardar);
                    GeneraJSON generaJSON = new GeneraJSON();
                    jsonArray.add(generaJSON.generaObjecte(intGuardar - 1, json));
                    generaJSON.guardaFitxer(jsonArray);
                    return;
                }else{
                    System.out.println("Aquesta no es una opcio valida! Introdueix una opcio de nou: ");
                }
                guardar = read.nextLine();
            }while (!guardar.toUpperCase().equals("STOP"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            System.err.println("Ja no hi han mes resultats de cerca!");
        }
    }

    /**
     * Mostra per pantalla els resultats del json introduit
     * @param index index de per quines caselles del array començar a printar els resultat
     * @param json Objecte JsonObject que conte la informacio la qual volem printar
     */
    private void mostraResultats(int index, JsonObject json) {
        for (int i = index; i < index + 10; i++){
            System.out.println("~~~~Resultat numero " + (i + 1) + "~~~~");
            System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
            System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
            System.out.println("    Descripcio: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
            System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
        }
    }

    /**
     * Comprova si la cadena introduida es un int
     * @param str Cadena a comprovar
     * @return cert si la cadena conte un int
     */
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void opcio3() {
        JsonArray favorits = jsonReader.lectura();
        System.out.println(favorits);
        for (int i = 0; i < favorits.size(); i++){
            if (favorits.getAsJsonArray().get(i).getAsJsonObject().get("tipusResultat").equals("youtube#channel")){

                System.out.println(favorits.get(0).getAsJsonObject().get("id"));
            }
        }
        //per a videos
        String URL1 = "https://www.googleapis.com/youtube/v3/videos?id=vJc7s9OJYiY&key=" + API_KEY + "&part=statistics";
        //per a canals
        String URL2 = "https://www.googleapis.com/youtube/v3/channels?id=UCEOWFJSDlfEgM_iPduEuHeg&key=" + API_KEY + "&part=statistics";
        //per a llistes
        String URL3 = "https://www.googleapis.com/youtube/v3/playlists?id=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj&key=" + API_KEY + "&part=snippet";
    }

    private void opcio4() {
        //per a videos
        String URL1 = "https://www.googleapis.com/youtube/v3/videos?id=vJc7s9OJYiY&key=" + API_KEY + "&part=statistics";
        //per a canals
        String URL2 = "https://www.googleapis.com/youtube/v3/channels?id=UCEOWFJSDlfEgM_iPduEuHeg&key=" + API_KEY + "&part=statistics";
        //per a llistes
        String URL3 = "https://www.googleapis.com/youtube/v3/playlists?id=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj&key=" + API_KEY + "&part=snippet";

    }

    private void opcio5() {

    }

    private void opcio6() {

    }

}
