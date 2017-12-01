/**
 * Created by angel on 01/12/2017.
 */
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class JsonReader {
    private static final String FILE_NAME = "laSallers.json";

    /**
     * Carrega i llegeix el fitxer Json corresponent
     * @return el fitxer Json llegit en forma JsonObject
     */
    JsonObject lectura(){
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(FILE_NAME);
            br = new BufferedReader(fr);
            jsonObject = gson.fromJson(br, JsonObject.class);
        } catch (FileNotFoundException ko) {
            ko.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * Llegeix i guarda el Json obtingut a partir de certa URL
     * @param url URL de la qual obtenir el Json
     * @return Un JsonObject el qual conte el Json llegit
     * @throws IOException
     */
    public JsonObject getJsonFromURL(String url) throws IOException {
        URL obj = new URL(url);
        JsonObject jsonObject = new JsonObject();
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder CadenaTotal = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            CadenaTotal.append(inputLine);
        }
        in.close();
        String aux = CadenaTotal.toString();
        Gson g = new Gson();
        jsonObject = g.fromJson(aux, JsonObject.class);
        return jsonObject;
    }
}
