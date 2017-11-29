import com.google.api.services.youtube.YouTube;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {

    /** Global instance properties filename. */
    private static String PROPERTIES_FILENAME = "youtube.properties";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /** Global instance of Youtube object to make all API requests. */
    private static YouTube youtube;

    public Menu(){
        mostraMenu();
    }
//https://www.googleapis.com/youtube/v3/videos?id=/*quin video*/7lCDEYXw3mM/*API KEY*/&key=AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0&part=/*que vols del vieo*/statistics
//AIzaSyCHI5qNldMo0BcX8iVv7Gnx9Zc0i1fcIQ0
    private void mostraMenu() {
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
        YouTube youTube = new YouTube();

    }

    private void opcio2() {

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
