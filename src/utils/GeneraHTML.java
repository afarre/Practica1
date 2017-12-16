package utils;

import java.io.*;

/**
 * Created by angel on 16/12/2017.
 */
public class GeneraHTML {

    private static String API_KEY;


    public GeneraHTML(String API_KEY){
        this.API_KEY = API_KEY;
    }

    public void creaPlantilla(String title, String body) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(title + ".html"));
        bw.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>" + title + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                body +
                "</body>\n" +
                "</html>");
        bw.close();
    }

    public String header(String title, int header){
        return "<h" + header + ">" + title + "</h" + header + ">";
    }

    public String img(String src, String alt, int size){
        return "<img src=" + src + " height=" + size + "width=" + size + " alt=" + alt + "/>";
    }

    public String enllaç (String href, String elem){
        return "<a href=" + href + "> \n    " + elem + "</a>";
    }

}
