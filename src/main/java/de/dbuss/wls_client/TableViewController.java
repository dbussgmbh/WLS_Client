package de.dbuss.wls_client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.*;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class TableViewController implements Initializable {
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;


    private ObservableList<User> loadUsersFromWebLogic(){

        ObservableList<User> data = FXCollections.observableArrayList();

        String username = ConfigLoader.get("weblogic.username");
        String password = ConfigLoader.get("weblogic.password");
        String url = ConfigLoader.get("weblogic.url");
        String jndiName = ConfigLoader.get("weblogic.jndiName");

      //  String url = "t3://LAP6.fritz.box:7001";  // ✅ Deine IP + Port
      //  String url = "t3://37.120.190.179:7001";  // ✅ Deine IP + Port
      //  String username = "admin";             // Benutzername
      //  String password = "7x24!admin4me";             // Passwort

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        //String jndiName = "jdbc/ekpDataSource";
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

                    PreparedStatement stmt = conn.prepareStatement("select ID,NAME from ekp.users");
                    ResultSet rs = stmt.executeQuery()) {

                System.out.println("📄 Ergebnisse:");

                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();


                while (rs.next()) {
                    Integer user = rs.getInt("ID");
                    String mail = rs.getString("NAME");
                    System.out.print(rs.getString("Name"));
                    data.add(new User(user, mail));
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



        return data;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<User> users = loadUsersFromWebLogic();

        tableView.setItems(users);
        //show_data();

    }
}
