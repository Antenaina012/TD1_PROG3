import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/product_management_db";
    private static final String USER = "product_manager_user";
    private static final String PASSWORD = "123456";

    public Connection getDBConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connexion à la base de données établie avec succès.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
            throw new RuntimeException("Échec de la connexion à la DB.", e);
        }
    }
}