package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.Menu;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by angel on 20/12/2017.
 */
public class Logica {

    private final static String API_KEY = "AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";
    private JsonReader jsonReader = new JsonReader();
    private JsonArray favourites = new JsonArray();
    private GeneraHTML generaHTML = new GeneraHTML();
    private GeneraJSON generaJSON = new GeneraJSON(this);

    /**
     * Carrega el fitxer de favorits ya existent o en genera un de nou si no n'existeix cap
     * @return True si existeix i l'ha carregat, False si no existeix i l'ha generat nou
     */
    public boolean carregaFavorits(){
        try {
            favourites = jsonReader.lectura();
            return true;
        } catch (FileNotFoundException e) {
            JsonObject jsonObject = new JsonObject();
            favourites.add(jsonObject);
            generaJSON.guardaFitxer(favourites);
            return false;
        }
    }

    /**
     * Permet introduir a l'usuari un text de cerca i mostra informacio dels 3 primers resultats
     */
    public void opcio1() {
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY;
        try {
            JsonObject json = jsonReader.getJsonFromURL(URL);
            System.out.println(json);
            if (json.get("items").getAsJsonArray().size() == 0){
                System.out.println("No s'ha trobat cap resultat!");
            }else {
                for (int i = 0 ; i < 3 && i < json.get("items").getAsJsonArray().size(); i++){
                    System.out.println("index: " + i);
                    System.out.println("~~~~Resultat numero " + (i + 1) + "~~~~");
                    System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
                    System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
                    System.out.println("    Descripcio: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
                    System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet al usuari cercar en base a un text introduit, la navegacio dels resultats de la cerca en blocs de 10
     * i el guardar el resultat desitjat en un fitxer json
     * @param menu Instancia de la clase menu per a poder printar el menu de nou quan sigui necesari
     */
    public void opcio2(Menu menu) {
        System.out.println("Introduiex el que vols cercar:");
        Scanner read = new Scanner(System.in);
        String queryTerm = read.nextLine();
        String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=";
        try {
            JsonObject json = jsonReader.getJsonFromURL(URL);

            int i = 0;
            mostraResultats(i, json);
            if (json.get("items").getAsJsonArray().size() != 0 ){
                System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                read = new Scanner(System.in);
                String guardar = read.nextLine();
                do {
                    if (guardar.compareToIgnoreCase("next") == 0){
                        if (json.get("nextPageToken") != null){
                            URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + json.get("nextPageToken").getAsString();
                            System.out.println(URL);
                            json = jsonReader.getJsonFromURL(URL);
                            mostraResultats(i, json);
                            System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                        }else {
                            System.out.println("No hi han mes resultats de busqueda.");
                            menu.mostraMenu();
                        }

                    }else if (guardar.compareToIgnoreCase("back") == 0){
                        if (json.get("prevPageToken") != null){
                            URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + queryTerm.replace(" ", "-") + "&key=" + API_KEY + "&maxResults=10&pageToken=" + json.get("prevPageToken").getAsString();
                            json = jsonReader.getJsonFromURL(URL);
                            mostraResultats(i, json);
                            System.out.println("Selecciona un per desar, si vols veure els seguents, els anteriors o parar:");
                        }else {
                            menu.mostraMenu();
                        }
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            System.out.println("Ja no hi han mes resultats de cerca!");
        }
    }

    /**
     * Mostra els videos guardats en el fitxer de favorits ordenats segons els percentatges de like de major a menor
     */
    public void opcio3() {
        ArrayList<JsonObject> videos = new ArrayList<>();

        for(int i = 1; i < favourites.size(); i++){
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

        if (videos.size() == 0){
            System.out.println("No hi ha cap video al fitxer de favorits.");
        }else {
            for (int i = 0; i < videos.size(); i++){
                System.out.println("~~~~Video numero " + (i + 1) + "~~~~");
                System.out.println("    Nom: " + videos.get(i).get("titol").getAsString());
                System.out.println("    Percentatge de likes: " + videos.get(i).get("percentatgeDeLikes").getAsFloat() + "%");
            }
        }
    }

    /**
     * Mostrar la mitjana de reproduccions del video, la mitjana de subscriptors dels canal i la playlist mes vella
     * i mes nova
     */
    public void opcio4() {
        int videosTotals = 0;
        int reproduccionsTotals = 0;
        int canalsTotal = 0;
        int subsTotals = 0;
        String playlistVella = "a";
        String playlistNova = "";

        for (int i = 1; i < favourites.size(); i++){
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
                    //NOTA: La data que proporciona YouTube esta en format ISO 8601 (The lexicographical order of the representation thus corresponds to chronological order)
                    URL = "https://www.googleapis.com/youtube/v3/playlists?id=" + favourites.get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY + "&part=snippet";
                    try {
                        JsonObject playlist = jsonReader.getJsonFromURL(URL);
                        String aux = playlist.get("items").getAsJsonArray().get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("publishedAt").getAsString();
                        if (aux.compareTo(playlistVella) < 0){
                            playlistVella = aux;
                        }
                        if (aux.compareTo(playlistNova) > 0){
                            playlistNova = aux;
                        }
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
        if (playlistVella.equals("a")){
            playlistVella = "";
        }
        if (canalsTotal == 0 && videosTotals == 0){
            System.out.println("No data for videos");
            System.out.println("No data for channels");
            System.out.println("La playlist mes vella es del: " + playlistVella);
            System.out.println("La playlist mes nova es del: " + playlistNova);
        }else if (canalsTotal == 0){
            System.out.println("Mitjana de reproduccions del videos: " + reproduccionsTotals/videosTotals);
            System.out.println("No data for channels");
            System.out.println("La playlist mes vella es del: " + playlistVella);
            System.out.println("La playlist mes nova es del: " + playlistNova);
        }else if (videosTotals == 0){
            System.out.println("No data for videos");
            System.out.println("Mitjana de subscriptors als canals: " + subsTotals/canalsTotal);
            System.out.println("La playlist mes vella es del: " + playlistVella);
            System.out.println("La playlist mes nova es del: " + playlistNova);
        }else {
            System.out.println("Mitjana de reproduccions del videos: " + reproduccionsTotals/videosTotals);
            System.out.println("Mitjana de subscriptors als canals: " + subsTotals/canalsTotal);
            System.out.println("La playlist mes vella es del: " + playlistVella);
            System.out.println("La playlist mes nova es del: " + playlistNova);
        }
    }

    /**
     * Genera un fitxer HTML que mostra una llista amb el nom i imatge del video amb el seu enllaç per cada
     * playlist guardada al fitxer de favorits
     */
    public void opcio5() {
        try {
            String body;
            for (int i = 1; i < favourites.size(); i++){
                if (favourites.getAsJsonArray().get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#playlist")){
                    String URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + favourites.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString() + "&key=" + API_KEY;
                    JsonObject json = jsonReader.getJsonFromURL(URL);
                    body = generaHTML.header(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString() + ": " + json.get("items").getAsJsonArray().size() + " videos.", 1);

                    for (int j = 0; j < json.get("items").getAsJsonArray().size(); j++){
                        body = body + generaHTML.header(json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString(), 4);
                        body = body + generaHTML.enllac("https://www.youtube.com/watch?v=" + json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString(), generaHTML.img(generaJSON.getMillorImatge(json, j),"Image not found!", 400, 600));
                    }
                    generaHTML.creaPlantilla(favourites.getAsJsonArray().get(i).getAsJsonObject().get("titol").getAsString(), body);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un fitxer en HTML que mostra en una graella els thumbnails de tots els resultats desats a favorits
     * amb un enllaç al seu video corresponent
     */
    public void opcio6() {
        JsonArray thumbnailArray =  new JsonArray();
        JsonArray URLArray = new JsonArray();
        ArrayList<Boolean> iframe = new ArrayList<>();
        for (int i = 1; i < favourites.size(); i++) {
            if (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#playlist")){
                for (int j = 1; j < favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().size(); j++){
                    if (favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(j).getAsString().equals("null")){
                        thumbnailArray.add("");
                    }else {
                        thumbnailArray.add(favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(j).getAsString());
                    }
                    URLArray.add(favourites.get(i).getAsJsonObject().get("URL").getAsJsonArray().get(j).getAsString());
                    iframe.add(true);
                }
            }else {
                if (favourites.get(i).getAsJsonObject().get("tipusResultat").getAsString().equals("youtube#video")){
                    iframe.add(true);
                }else {
                    iframe.add(false);
                }
                if (favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(0).getAsString().equals("null")){
                    thumbnailArray.add("");
                }else {
                    thumbnailArray.add(favourites.get(i).getAsJsonObject().get("thumbnails").getAsJsonArray().get(0).getAsString());
                }
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
                    graella = graella + generaHTML.enllac(URLArray.get((i * 4) + j).getAsString(), img);
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
                graella = graella + generaHTML.enllac(URLArray.get(i).getAsString(), img);
            }


        }
        fila = fila + generaHTML.generaFila(generaHTML.generaCasella(graella)) + "\n";

        try {
            generaHTML.creaPlantilla("Thumbnails", generaHTML.graella(fila));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra per pantalla els resultats del json introduit
     * @param index index de per quines caselles del array començar a printar els resultat
     * @param json Objecte JsonObject que conte la informacio la qual volem printar
     */
    private void mostraResultats(int index, JsonObject json) {
        int aux = 1;
        if (json.get("items").getAsJsonArray().size() == 0){
            System.out.println("No s'ha trobat cap resultat!");
        }else {
            for (int i = index; i < index + 10 && i < json.get("items").getAsJsonArray().size(); i++){
                System.out.println("~~~~Resultat numero " + (aux) + "~~~~");
                System.out.println("    Tipus de resultat: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
                System.out.println("    Titol: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
                System.out.println("    Descripció: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
                System.out.println("    Nom del canal al que correspon: " + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString() + "\n");
                aux++;
            }
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


    public static String getApiKey() {
        return API_KEY;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

}
