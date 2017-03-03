package at.u4a.tracer.gui;

import graphique.ZoneSaisirPointsAfficherSegments;

import javax.swing.*;
import java.awt.*;

/**
 * App
 *
 * @author Christophe-Alexandre Sonntag (http://u4a.at)
 * @version 1.0 (11/02/2017)
 */
public class App {


    /** La methode main. */
    public static void main(String[] args) {
        // Construction de la fenetre
        JFrame frame = new JFrame("Saisir des points et afficher des segments en resultat");

        // Construction de la zone d'affichage
        ZoneSaisirPointsAfficherSegments zoneAffichage = new ZoneSaisirPointsAfficherSegments();

        // Ajout de la zone d'affichage a la fenetre
        frame.getContentPane().add(zoneAffichage);

        // Dimension de la zone d'affichage
        zoneAffichage.setPreferredSize(new Dimension(600,800));

        // Resize autour de la zone d'affichage
        frame.pack();

        // Affichage de la fenetre
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
