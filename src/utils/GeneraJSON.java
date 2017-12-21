package utils;

import com.google.gson.*;
import model.FavouritesModel;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by angel on 02/12/2017.
 */
class GeneraJSON {
    private String API_KEY;
    private JsonReader jsonReader;

    GeneraJSON(String API_KEY, JsonReader jsonReader) {
        this.API_KEY = API_KEY;
        this.jsonReader = jsonReader;
    }

    /**
     * Crea un fitxer .json
     * @param array Array de dades a partir del qual generar el fitxer .json
     */
    void guardaFitxer(JsonArray array) {
        try {
            FileWriter file = new FileWriter("favoritePlaces.json");
            //TODO: NO SOBREESCRIURE EL FITXER VELL, AFEGIR LES DADES VELLES AL NOU
            file.write(array.toString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converteix i retorna un JsonObject a partir d'un model de dades
     * @param i Index del array del json del qual poblarem el model de dades
     * @param json Json d'on volem extreure les dades
     * @return Un objecte Json extret del model de dades
     */
    JsonObject generaObjecte(int i, JsonObject json) {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();

        FavouritesModel favouritesModel = new FavouritesModel();
        favouritesModel.setTipusResultat(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
        favouritesModel.setTitol(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
        favouritesModel.setDescripcio(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
        favouritesModel.setNomCanal(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString());

        switch (favouritesModel.getTipusResultat()) {
            case "youtube#video":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString());
                String URL = "https://www.googleapis.com/youtube/v3/videos?id=" + favouritesModel.getId() + "&key=" + API_KEY + "&part=statistics";
                try {
                    JsonObject jsonobj = jsonReader.getJsonFromURL(URL);
                    int likes = jsonobj.getAsJsonArray("items").get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("likeCount").getAsInt();
                    int dislikes = jsonobj.getAsJsonArray("items").get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("dislikeCount").getAsInt();
                    int percentatgeLikes = 100 * likes / (likes + dislikes);

                    favouritesModel.setPercentatgeDeLikes(percentatgeLikes);
                    favouritesModel.setThumbnails(jsonParser.parse("[\n\"" + getMillorImatge(json, i) + "\"\n]").getAsJsonArray());
                    favouritesModel.setURL(jsonParser.parse("[\n\"" + "https://www.youtube.com/embed/" + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString() + "\"\n]").getAsJsonArray());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "youtube#playlist":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("playlistId").getAsString());
                URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + favouritesModel.getId() + "&key=" + API_KEY;
                try {
                    JsonObject jsonobj = jsonReader.getJsonFromURL(URL);
                    JsonArray imgArray = new JsonArray();
                    JsonArray URLArray = new JsonArray();
                    for (int j = 0; j < jsonobj.get("items").getAsJsonArray().size(); j++) {
                        imgArray.add(getMillorImatge(jsonobj, j));
                        URLArray.add("https://www.youtube.com/embed/" + jsonobj.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString() /*+ "&list=" + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("playlistId").getAsString()*/);
                    }

                    favouritesModel.setThumbnails(imgArray);
                    favouritesModel.setURL(URLArray);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "youtube#channel":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("channelId").getAsString());
                favouritesModel.setThumbnails(jsonParser.parse("[\n\"" + getMillorImatge(json, i) + "\"\n]").getAsJsonArray());
                favouritesModel.setURL(jsonParser.parse("[\n\"" + "https://www.youtube.com/channel/" + json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("channelId").getAsString() + "\"\n]").getAsJsonArray());
                break;
        }

        JsonElement element = jsonParser.parse(gson.toJson(favouritesModel));
        return element.getAsJsonObject();
    }

    /**
     * Obte la URL de la imatge amb millor qualitat amb controls d'errors
     * @param json Json d'on podem llegir les diferents imatges amb diferents qualitats a escollir
     * @param j Index que indica quin element del json s'ha de llegir
     * @return La URL de la imatge amb millor qualitat
     */
    String getMillorImatge(JsonObject json, int j) {
        if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails") == null){
            return "noimage.jpg";
        }else if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("maxres") != null) {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("maxres").getAsJsonObject().get("url").getAsString();
        } else if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("standard") != null) {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("standard").getAsJsonObject().get("url").getAsString();
        } else if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("high") != null) {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("high").getAsJsonObject().get("url").getAsString();
        } else if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("medium") != null) {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("medium").getAsJsonObject().get("url").getAsString();
        } else if (json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("default") != null) {
            return json.get("items").getAsJsonArray().get(j).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("default").getAsJsonObject().get("url").getAsString();
        } else {
            //TODO: RETORNAR UNA IMATGE GUARDADA EN LOCAL
            return "No image found";
        }
    }
}
