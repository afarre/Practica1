import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();

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
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm + "&key=AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";
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
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0&maxResults=50";
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
                    //JsonObject test =
                    guardaCerca(intGuardar, json);
                    //JsonArray jsonArray = new JsonArray();
                    //jsonArray.add(test);
                    //guardaFitxer(jsonArray);
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

    private void guardaFitxer(JsonArray array) {
        try { FileWriter file = new FileWriter("favoritePlaces.json");
            file.write(array.toString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guardaCerca(int i, JsonObject json) {
        JsonObject jsonObject;
        FavouritesModel favouritesModel = new FavouritesModel();
        favouritesModel.setTipusResultat(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
        favouritesModel.setTitol(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
        favouritesModel.setDescripcio(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
        favouritesModel.setNomCanal(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString());

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(favouritesModel.toString());
        guardaFitxer(jsonArray);
        /*
        JsonElement element = jsonParser.parse(jsonArray.get(0).toString());
        jsonObject = element.getAsJsonObject();
        return jsonObject;
        */
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void mostraResultats(int index, JsonObject json) {
        for (int i = index; i < index + 10; i++){
            System.out.println("~~~~Resultat numero " + (i + 1) + "~~~~");
            System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
            System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
            System.out.println("    Descripcio: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
            System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
        }
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
