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
                //TODO: FICAR EL LS DE LA NATALIA
                "    <meta name=\"Angel Farre & Natalia Jimenez\" content=\"Angel Farre - ls30927 & Natalia Jimenez - \">\n" +
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

    public String img(String src, String alt, int height, int lenght){
        return "<img src=\"" + src + "\" height=\"" + height + "\" width=\"" + lenght + "\" alt=\"" + alt + "\"/>";
    }

    public String enllaç (String href, String elem){
        return "<a href=\"" + href + "\"> \n    " + elem + "</a>";
    }

    public String graella(String graella){
        return "<table>\n" +
                    graella +
                "</table>\n";
    }

    public String generaFila(String graella){
        return "   <tr>\n" +
                        graella +
                "   </tr>\n";
    }

    public String generaCasella(String element){
        return  "       <td>\n" +
                            element +
                "       </td>\n";
    }

}
