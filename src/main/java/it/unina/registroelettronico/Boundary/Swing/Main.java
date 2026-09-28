package it.unina.registroelettronico.Boundary.Swing;


import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {



        UIManager.put(
                "defaultFont",
                new Font("Inter", Font.PLAIN, 14)
        );





        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Area Docente");

            /*SchermataDocente schermataDocente =
                    new SchermataDocente();
            */

            SchermataIniziale schermataIniziale=new SchermataIniziale();
            frame.setContentPane(
                    schermataIniziale.getContentPane()
            );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}