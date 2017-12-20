package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    private final String API_KEY = "AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";

    /**
     * Mostra el menu permetent seleccionar opcions de la 1 a la 7 incloses amb control d'errors
     */
    public void mostraMenu() {
        int i;
        GeneraHTML generaHTML = new GeneraHTML();
        GeneraJSON generaJSON = new GeneraJSON(API_KEY);
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

            switch (i){
                case 1:
                    opcio1();
                    break;
                case 2:
                    opcio2(generaJSON);
                    break;
                case 3:
                    opcio3();
                    break;
                case 4:
                    opcio4();
                    break;
                case 5:
                    opcio5(generaHTML, generaJSON);
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

    /**
     * Permet introduir a l'usuari un text de cerca i mostra informacio dels 3 primers resultats
     */
    private void opcio1() {
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY;
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

    /**
     * Permet al usuari cercar en base a un text introduit, la navegacio dels resultats de la cerca en blocs de 10
     * i el guardar el resultat desitjat en un fitxer json
     * @param generaJSON Clase amb funcionalitats que ens permeten generar un fitxer json
     */
    private void opcio2(GeneraJSON generaJSON) {
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
                if (guardar.compareToIgnoreCase("next") == 0){
                    //TODO: ARREGLAR EL INDEX DEL BACK
                    URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + json.get("nextPageToken").getAsString();
                    previousTokens.add(json.get("nextPageToken").getAsString());
                    json = jsonReader.getJsonFromURL(URL);
                    mostraResultats(i, json);
                    acum++;
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.compareToIgnoreCase("back") == 0){
                    URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + previousTokens.get(--acum);
                    json = jsonReader.getJsonFromURL(URL);
                    mostraResultats(i, json);
                    System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                }else if (guardar.compareToIgnoreCase("stop") == 0){
                    return;
                }else if (isNumeric(guardar) && Integer.parseInt(guardar) < 11 && Integer.parseInt(guardar) > 0){
                    int intGuardar = Integer.parseInt(guardar);
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
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Mostra els videos guardats en el fitxer de favorits ordenats segons els percentatges de like de major a menor
     */
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

    /**
     * Mostrar la mitjana de reproduccions del video, La mitjana de subscriptors dels canal i la playlist mes vella
     * i mes nova
     */
    private void opcio4() {
        int videosTotals = 0;
        int reproduccionsTotals = 0;
        int canalsTotal = 0;
        int subsTotals = 0;
        String playlistVella;
        String playlistNova;
        for (int i = 0; i < favourites.size(); i++){
            switch (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString()) {
                case "youtube#video":
                    videosTotals++;
                    String URL = "https://www.googleapis.com/youtube/v3/videos?id=" + favourites.get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY + "&part=statistics";
                    try {
                        JsonObject video = jsonReader.getJsonFromURL(URL);
                        reproduccionsTotals += video.get("items").getAsJsonArray().get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("viewCount").getAsInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "youtube#playlist":
                    URL = "https://www.googleapis.com/youtube/v3/playlists?id=" + favourites.get(i).getAsJsonObject().get("id").getAsString() + "-5rmhvj7fax8fdxoj&key=" + API_KEY + "&part=snippet";
                    System.out.println(URL);
                    try {
                        JsonObject playlist = jsonReader.getJsonFromURL(URL);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "youtube#channel":
                    canalsTotal++;
                    URL = "https://www.googleapis.com/youtube/v3/channels?id=" + favourites.get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY + "&part=statistics";
                    try {
                        JsonObject canal = jsonReader.getJsonFromURL(URL);
                        subsTotals += canal.get("items").getAsJsonArray().get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("subscriberCount").getAsInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        if (videosTotals == 0){
            System.out.println("No data for videos");
            System.out.println("Mitjana de subscriptors als canals: " + subsTotals/canalsTotal);
        }else if (canalsTotal == 0){
            System.out.println("Mitjana de reproduccions del videos: " + reproduccionsTotals/videosTotals);
            System.out.println("No data for channels");
        }else {
            System.out.println("Mitjana de reproduccions del videos: " + reproduccionsTotals/videosTotals);
            System.out.println("Mitjana de subscriptors als canals: " + subsTotals/canalsTotal);
        }
        //TODO: ACABAR LES PLAYLIST. SUPOSAR QUE LA LLISTA ES VA CREAR AL INSERIR EL PRIMER VIDEO?
    }

    /**
     * Genera un fitxer html que mostra una llista amb el nom i imatge del video amb el seu enllaç per cada
     * playlist guardada al fitxer de favorits
     * @param generaHTML Clase amb funcionalitats que ens permeten generar un fitxer html
     * @param generaJSON Clase amb funcionalitats que ens permeten generar un fitxer json
     */
    private void opcio5(GeneraHTML generaHTML, GeneraJSON generaJSON) {
        try {
            String body;
            for (int i = 0; i < favourites.size(); i++){
                if (favourites.getAsJsonArray().get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#playlist")){
                    String URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + favourites.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY;
                    JsonObject json = jsonReader.getJsonFromURL(URL);
                    //TODO: CONTROLAR QUE EL FITXER ES CREI DE FORMA CORRECTA
                    body = generaHTML.header(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString() + ": " + json.get("items").getAsJsonArray().size() + " videos.", 1);

                    for (int j = 0; j < json.get("items").getAsJsonArray().size(); j++){
                        body = body + generaHTML.header(json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString(), 4);
                        body = body + generaHTML.enllaç("https://www.youtube.com/watch?v=" + json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString(), generaHTML.img(generaJSON.getMillorImatge(json, j),"Image not found!", 400, 600));
                    }
                    generaHTML.creaPlantilla(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString(), body);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un fitxer en html que mostra en una graella els thumbnails de tots els resultats desats a favorits
     * amb un enllaç al seu video corresponent
     * @param generaHTML Clase amb funcionalitats que ens permeten generar un fitxer html
     */
    //TODO: AMB ZOOM GRAN MOSTRE FILES DE 4, HAM ZOOM MES GRAN FILES DEL TAMANY DE LA PANTALLA. TOT EN UNA SOLA FILA?
    private void opcio6(GeneraHTML generaHTML) {
        JsonArray thumbnailArray =  new JsonArray();
        JsonArray URLArray = new JsonArray();
        ArrayList<Boolean> iframe = new ArrayList<>();
        for (int i = 0; i < favourites.size(); i++) {
            if (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#playlist")){
                for (int j = 0; j < favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().size(); j++){
                    thumbnailArray.add(favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(j).getAsString());
                    URLArray.add(favourites.get(i).getAsJsonObject().get("URL").getAsJsonArray().get(j).getAsString());
                    iframe.add(true);
                }
            }else {
                if (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#video")){
                    iframe.add(true);
                }else {
                    iframe.add(false);
                }
                thumbnailArray.add(favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(0).getAsString());
                URLArray.add(favourites.get(i).getAsJsonObject().get("URL").getAsJsonArray().get(0).getAsString());
            }
        }

        String fila = "";
        for (int i = 0; i < thumbnailArray.size() / 4; i++){
            String graella = "";
            for (int j = 0; j < 4; j++){
                if (iframe.get((i * 4) + j)){
                    graella = graella + generaHTML.generaIframe(URLArray.get((i * 4) + j).getAsString(), 400, 600);
                }else{
                    String img = generaHTML.img(thumbnailArray.get((i * 4) + j).getAsString(), "Image not found!", 400, 600);
                    graella = graella + generaHTML.enllaç(URLArray.get((i * 4) + j).getAsString(), img);
                }
            }
            fila = fila + generaHTML.generaFila(generaHTML.generaCasella(graella)) + "\n";
        }

        String graella = "";
        for (int i = (thumbnailArray.size() - (thumbnailArray.size() - 4*(thumbnailArray.size() / 4))); i < thumbnailArray.size(); i++){
            if (iframe.get(i)){
                graella = graella + generaHTML.generaIframe(URLArray.get(i).getAsString(), 400, 600);
            }else{
                String img = generaHTML.img(thumbnailArray.get(i).getAsString(), "Image not found!", 400, 600);
                graella = graella + generaHTML.enllaç(URLArray.get(i).getAsString(), img);
            }


        }
        fila = fila + generaHTML.generaFila(generaHTML.generaCasella(graella)) + "\n";

        try {
            generaHTML.creaPlantilla("Thumbnails", generaHTML.graella(fila));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
