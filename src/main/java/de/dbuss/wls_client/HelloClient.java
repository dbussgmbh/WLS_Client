package de.dbuss.wls_client;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Hashtable;

public class HelloClient {

    public static void main(String[] args) {

   //     System.setProperty("java.rmi.server.hostname", "LAP6.fritz.box");
        //     System.setProperty("weblogic.rmi.extensions.server.Hostname", "LAP6.fritz.box");
        //     System.setProperty("weblogic.debug.DebugJNDI", "true");
        //System.setProperty("weblogic.debug.DebugJNDIResolution", "true");
        //System.setProperty("weblogic.StdoutDebugEnabled", "true");

         String url = "t3://LAP6.fritz.box:7001";  // ✅ Deine IP + Port
        //String url = "t3://37.120.190.179:7001";  // ✅ Deine IP + Port
        String username = "admin";             // Benutzername
        String password = "7x24!admin4me";             // Passwort

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        String jndiName = "jdbc/ekpDataSource";
        try {
            // 1. JNDI-Context aufbauen
            Context ctx = new InitialContext(env);

            // 2. DataSource per JNDI holen
            //DataSource ds = (DataSource) ctx.lookup(jndiName);

            DataSource ds = (DataSource) ctx.lookup("jdbc/ekpDataSource");
            System.out.println("✅ DataSource gefunden: " + jndiName);

            // 3. Verbindung holen und Abfrage ausführen
            try (

//                    Connection conn = ds.getConnection();
                    Connection conn = ds.getConnection(); // Dieser Aufruf "arbeitet" serverseitig

                 PreparedStatement stmt = conn.prepareStatement("select * from ekp.users");
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("📄 Ergebnisse:");

                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= colCount; i++) {
                        System.out.print(rs.getString(i));
                        if (i < colCount) System.out.print(" | ");
                    }
                    System.out.println();
                }

            } catch (SQLException e) {
                System.out.println("❌ SQL-Fehler: " + e.getMessage());
                e.printStackTrace();
            }

            ctx.close();

        } catch (Exception e) {
            System.out.println("❌ Fehler beim Zugriff auf die DataSource: " + e.getMessage());
            e.printStackTrace();
        }


        /*

        try {
            Context ctx = new InitialContext(env);
            String jndiName = "jdbc/ekpDataSource";
            System.out.println("🔍 Suche nach DataSource: " + jndiName);

            Object obj = ctx.lookup(jndiName);

            if (obj instanceof DataSource) {
                System.out.println("✅ DataSource gefunden: " + jndiName + " → " + obj.getClass().getName());
            } else {
                System.out.println("⚠️  Objekt gefunden, aber kein DataSource: " + obj.getClass().getName());
            }

            ctx.close();
        } catch (NamingException e) {
            System.out.println("❌ DataSource nicht gefunden: " + e.getMessage());
            e.printStackTrace();
        }



         */

        /*

        try {
            Context ctx = new InitialContext(env);
            System.out.println("📦 Durchsuche JNDI nach DataSources:");
            //listJndi(ctx, "ekpDataSource", "");
            listJndi(ctx,"","");
            ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

         */

/*
        try {
            System.out.println("🟡 Versuche Verbindung zu WebLogic via " + url);
            Context ctx = new InitialContext(env);

            // JNDI-Einträge im Root-Kontext ausgeben
            System.out.println("🟢 Verbindung erfolgreich. JNDI-Einträge:");
            NamingEnumeration<NameClassPair> list = ctx.list("");
            while (list.hasMore()) {
                NameClassPair entry = list.next();
                System.out.println(" - " + entry.getName());
            }

            ctx.close();
        } catch (NamingException e) {
            System.out.println("🔴 Fehler beim Verbindungsaufbau: " + e.getMessage());
            e.printStackTrace();
        }

 */
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



}











