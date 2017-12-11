import com.google.gson.*;
import model.FavouritesModel;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by angel on 02/12/2017.
 */
public class GeneraJSON {
    private String API_KEY = "AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0";


    /**
     * Crea un fitxer .json
     * @param array Array de dades a partir del qual generar el fitxer .json
     */
    void guardaFitxer(JsonArray array) {
        try { FileWriter file = new FileWriter("favoritePlaces.json");
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
        JsonReader jsonReader = new JsonReader();

        FavouritesModel favouritesModel = new FavouritesModel();
        favouritesModel.setTipusResultat(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("kind").getAsString());
        favouritesModel.setTitol(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
        favouritesModel.setDescripcio(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
        favouritesModel.setNomCanal(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("channelTitle").getAsString());

        switch (favouritesModel.getTipusResultat()) {
            case "youtube#channel":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("channelId").getAsString());
                break;
            case "youtube#playlist":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("playlistId").getAsString());
                break;
            case "youtube#video":
                favouritesModel.setId(json.get("items").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString());
                break;
        }

        if (favouritesModel.getTipusResultat().equals("youtube#video")){
            String URL = "https://www.googleapis.com/youtube/v3/videos?id=" + favouritesModel.getId() + "&key=" + API_KEY + "&part=statistics";
            try {
                JsonObject jsonobj = jsonReader.getJsonFromURL(URL);
                int likes = jsonobj.getAsJsonArray("items").get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("likeCount").getAsInt();
                int dislikes = jsonobj.getAsJsonArray("items").get(0).getAsJsonObject().get("statistics").getAsJsonObject().get("dislikeCount").getAsInt();
                int percentatgeLikes = 100*likes/(likes + dislikes);

                favouritesModel.setPercentatgeDeLikes(percentatgeLikes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(gson.toJson(favouritesModel));
        return element.getAsJsonObject();
    }




}
