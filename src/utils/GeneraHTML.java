package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angel on 16/12/2017.
 */
class GeneraHTML {

    /**
     * Genera la plantilla base d'un fitxer HTML
     * @param title Titol a posar al fitxer HTML
     * @param body Cos del HTML
     * @throws IOException if file has not been successfully created
     */
    void creaPlantilla(String title, String body) throws IOException {
        if (title.matches("^[^\\\\/:*?<>|]+$")){
            BufferedWriter bw = new BufferedWriter(new FileWriter(title + ".html"));
            bw.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"Angel Farre & Natalia Jimenez\" content=\"Angel Farre - ls30927 & Natalia Jimenez - natalia.jimenez.2015@hotmail.com\">\n" +
                    "    <title>" + title + "</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    body +
                    "</body>\n" +
                    "</html>");
            bw.close();
        }else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Date date = new Date();
            date.getTime();
            BufferedWriter bw = new BufferedWriter(new FileWriter(dateFormat.format(date) + ".html"));
            bw.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"Angel Farre & Natalia Jimenez\" content=\"Angel Farre - ls30927 & Natalia Jimenez - natalia.jimenez.2015@hotmail.com\">\n" +
                    "    <title>" + title + "</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    body +
                    "</body>\n" +
                    "</html>");
            bw.close();
        }
    }

    /**
     * Genera un header de HTML amb els atributs especificats
     * @param title Nom del header
     * @param header Tamany del header
     * @return El header generat
     */
    String header(String title, int header){
        return "<h" + header + ">" + title + "</h" + header + ">" + "\n";
    }

    /**
     * Genera un img de HTML amb els atributs especificats
     * @param src Directori o URL d'on obtenir la imatge
     * @param alt Text alternatiu en cas de no poder mostrar la imatge
     * @param height Altura especificada en pixels
     * @param width Amplada especificada en pixels
     * @return El img generat
     */
    String img(String src, String alt, int height, int width){
        return "<img src=\"" + src + "\" height=\"" + height + "\" width=\"" + width + "\" alt=\"" + alt + "\"/>" + "\n";
    }

    /**
     * Genera un enllaç de HTML amb els atributs especificats
     * @param href Enllaç que volem aplicar al element
     * @param elem Element sobre el qual aplicar el enllaç especificat
     * @return El enllaç generat
     */
    String enllaç(String href, String elem){
        return "<a href=\"" + href + "\"> \n    " + elem + "</a>" + "\n";
    }

    /**
     * Genera una graella de HTML amb els atributs especificats
     * @param graella Contingut de la graella
     * @return La graella generada
     */
    String graella(String graella){
        return "<table>\n" +
                    graella +
                "</table>\n";
    }

    /**
     * Genera una fila de graella de HTML amb els atributs especificats
     * @param graella Contingut de la fila
     * @return La fila generada
     */
    String generaFila(String graella){
        return "   <tr>\n" +
                        graella +
                "   </tr>\n";
    }

    /**
     * Genera una casella de fila de HTML amb els atributs especificats
     * @param element Contingut de la casella
     * @return La casella generada
     */
    String generaCasella(String element){
        return  "       <td>\n" +
                            element +
                "       </td>\n";
    }

    /**
     * Genera un iframe de HTML amb els atributs especificats
     * @param url Enllaç del qual obtindre el embed video
     * @param height Altura especificada en pixels
     * @param width Amplada especificada en pixels
     * @return El iframe generat
     */
    String generaIframe(String url, int height, int width){
        return "<iframe width=\"" + width + "\" height=\"" + height + "\" src=\"" + url + "\"></iframe>" + "\n";
    }

}
