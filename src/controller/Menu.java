package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import utils.GeneraHTML;
import utils.GeneraJSON;
import utils.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonArray favourites = new JsonArray();
    private String API_KEY = "AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";

    public void mostraMenu() {
        int i;
        do {
            System.out.println("\n1. Cerca de Resultats");
            System.out.println("2. Desar Preferits");
            System.out.println("3. Millors Videos");
            System.out.println("4. Estadístiques");
            System.out.println("5. Llistes de Reproducció");
            System.out.println("6. El Mosaic");
            System.out.println("7. Sortir\n");
            System.out.println("\nSel·lecciona una opcio:");

            i = readInt();

            while (i < 1 || i > 7) {
                System.out.println("Opcio del menu incorrecta! Introdueix l'opcio de nou:");
                i = readInt();
            }

            GeneraHTML generaHTML = new GeneraHTML(API_KEY);

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
                    opcio5(generaHTML);
                    break;
                case 6:
                    opcio6(generaHTML);
                    break;
            }
        }while(i != 7);
    }

    /**
     * Comprova que l'usuari introduiex un enter
     * @return El numero introduit per l'usuari o -1 en cas de que no hagi introduit un numero
     */
    private int readInt(){
        try {
            Scanner read = new Scanner(System.in);
            return read.nextInt();
        }catch (InputMismatchException ignored){
        }
        return -1;
    }

    private void opcio1() {
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY;
        System.out.println(URL);
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
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=";
        ArrayList<String> previousTokens = new ArrayList<>();
        try {
            JsonObject json = jsonReader.getJsonFromURL(URL);

            int i = 0;
            int acum = 0;
            mostraResultats(i, json);

            System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
            read = new Scanner(System.in);
            String guardar = read.nextLine();
            do {
                if (guardar.toUpperCase().equals("NEXT")){
                    //guardar.compareTo("test");
                    URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + json.get("nextPageToken").getAsString();
                    previousTokens.add(json.get("nextPageToken").getAsString());
                    json = jsonReader.getJsonFromURL(URL);
                    mostraResultats(i, json);
                    acum++;
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.toUpperCase().equals("BACK")){
                    URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + previousTokens.get(--acum);
                    json = jsonReader.getJsonFromURL(URL);
                    mostraResultats(i, json);
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.toUpperCase().equals("STOP")){
                    return;
                }else if (isNumeric(guardar) && Integer.parseInt(guardar) < 11 && Integer.parseInt(guardar) > 0){
                    int intGuardar = Integer.parseInt(guardar);
                    GeneraJSON generaJSON = new GeneraJSON();
                    favourites.add(generaJSON.generaObjecte(intGuardar - 1, json));
                    generaJSON.guardaFitxer(favourites);
                    return;
                }else{
                    System.out.println("Aquesta no es una opcio valida! Introdueix una opcio de nou: ");
                }
                guardar = read.nextLine();
            }while (!guardar.toUpperCase().equals("STOP"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            System.out.println("Ja no hi han mes resultats de cerca!");
        }
    }

    /**
     * Mostra per pantalla els resultats del json introduit
     * @param index index de per quines caselles del array començar a printar els resultat
     * @param json Objecte JsonObject que conte la informacio la qual volem printar
     */
    private void mostraResultats(int index, JsonObject json) {
        int aux = 1;
        for (int i = index; i < index + 10; i++){
            System.out.println("~~~~Resultat numero " + (aux) + "~~~~");
            System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
            System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
            System.out.println("    Descripció: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
            System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
            aux++;
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
        ArrayList<JsonObject> videos = new ArrayList<>();

        for(int i = 0; i < favourites.size(); i++){
            if (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#video")) {
                videos.add(favourites.get(i).getAsJsonObject());
            }
        }

        JSONArray aux = new JSONArray();
        for (int i = 0; i < favourites.size(); i++){
            aux.put(favourites.get(i));
        }

        for (int i = 0; i < videos.size(); i++){
            for (int j = 0; j < videos.size(); j++){
                  if (videos.get(i).getAsJsonObject().get("percentatgeDeLikes").getAsFloat() > videos.get(j).getAsJsonObject().get("percentatgeDeLikes").getAsFloat()){
                    Collections.swap(videos, i, j);
                }
            }
        }

        for (int i = 0; i < videos.size(); i++){
            System.out.println("~~~~Video numero " + (i + 1) + "~~~~");
            System.out.println("    Nom: " + videos.get(i).get("titol").getAsString());
            System.out.println("    Percentatge de likes: " + videos.get(i).get("percentatgeDeLikes").getAsFloat() + "%");
        }
    }

    private void opcio4() {

        //per a videos
        String URL1 = "https://www.googleapis.com/youtube/v3/videos?id=vJc7s9OJYiY&key=" + API_KEY + "&part=statistics";
        //per a canals
        String URL2 = "https://www.googleapis.com/youtube/v3/channels?id=UCEOWFJSDlfEgM_iPduEuHeg&key=" + API_KEY + "&part=statistics";
        //per a llistes
        String URL3 = "https://www.googleapis.com/youtube/v3/playlists?id=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj&key=" + API_KEY + "&part=snippet";

    }

    private void opcio5(GeneraHTML generaHTML) {
        try {
            String body;
            for (int i = 0; i < favourites.size(); i++){
                if (favourites.getAsJsonArray().get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#playlist")){
                    String URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + favourites.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY;
                    JsonObject json = jsonReader.getJsonFromURL(URL);
                    body = generaHTML.header(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString() + ": " + json.get("items").getAsJsonArray().size() + " videos.", 1);

                    for (int j = 0; j < json.get("items").getAsJsonArray().size(); j++){
                        body = body + generaHTML.header(json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString(), 4) + "\n";
                        body = body + generaHTML.enllaç("https://www.youtube.com/watch?v=" + json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString(), generaHTML.img(getMillorImatge(json, j),"Image not found!", 200) + "\n") + "\n";
                    }
                    generaHTML.creaPlantilla(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString(), body);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obte la URL de la imatge amb millor qualitat amb controls d'errors
     * @param json Json d'on podem llegir les diferents imatges amb diferents qualitats a escollir
     * @param j Index que indica quin element del json s'ha de llegir
     * @return La URL de la imatge amb millor qualitat
     */
    private String getMillorImatge(JsonObject json, int j){
        try {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("maxres").getAsJsonObject().get("url").getAsString();
        }catch (NullPointerException a){
            try {
                return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("standard").getAsJsonObject().get("url").getAsString();
            }catch (NullPointerException b){
                try {
                    return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("high").getAsJsonObject().get("url").getAsString();
                }catch (NullPointerException c){
                    try {
                        return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("medium").getAsJsonObject().get("url").getAsString();
                    }catch (NullPointerException d){
                        try {
                            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("default").getAsJsonObject().get("url").getAsString();
                        }catch (NullPointerException e){
                            return "No image found";
                        }
                    }
                }
            }
        }
    }

    private void opcio6(GeneraHTML generaHTML) {

    }

}
