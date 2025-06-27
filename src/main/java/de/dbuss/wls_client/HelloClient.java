package de.dbuss.wls_client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Hashtable;

public class HelloClient extends Application {

    public static void main(String[] args) {

        launch(args); //ruft die start-Methode auf


    }

    private static void listJndi(Context ctx, String baseName, String indent) {
        try {
            NamingEnumeration<NameClassPair> list = ctx.list(baseName);
            while (list.hasMore()) {
                NameClassPair ncp = list.next();
                String name = ncp.getName();
                String fullName = baseName.isEmpty() ? name : baseName + "/" + name;

                try {
                    Object obj = ctx.lookup(fullName);

                    if (obj instanceof javax.sql.DataSource) {
                        System.out.println(indent + "🔹 DataSource: " + fullName + " (" + obj.getClass().getName() + ")");
                    } else if (obj instanceof Context) {
                        // Optional: Problematische Kontexte überspringen
                        if (fullName.startsWith("weblogic") || fullName.startsWith("javax") || fullName.startsWith("jmx") || fullName.startsWith("eis") || fullName.startsWith("java:global")) {
                            System.out.println(indent + "⏩ Kontext übersprungen: " + fullName);
                        } else {
                            listJndi(ctx, fullName, indent + "  ");
                        }
                    }

                } catch (Exception ex) {
                    // Fehler beim Lookup — leise behandeln oder loggen
                    System.out.println(indent + "⚠️  Fehler bei Lookup: " + fullName + " → " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(indent + "⚠️  Fehler bei Zugriff auf Kontext '" + baseName + "': " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/dbuss/wls_client/TableView.fxml"));
        Scene scene = new Scene(loader.load(),650,450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("eKP Admin App Login");
        primaryStage.show();
    }




}











