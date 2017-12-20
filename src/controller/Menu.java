package controller;

import utils.Logica;

import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * Created by angel on 29/11/2017.
 */
public class Menu {


    /**
     * Mostra el menu permetent seleccionar opcions de la 1 a la 7 incloses amb control d'errors
     */
    public void mostraMenu() {
        int i;
        Logica logica = new Logica();
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
                    logica.opcio1();
                    break;
                case 2:
                    logica.opcio2();
                    break;
                case 3:
                    logica.opcio3();
                    break;
                case 4:
                    logica.opcio4();
                    break;
                case 5:
                    logica.opcio5();
                    break;
                case 6:
                    logica.opcio6();
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

}
