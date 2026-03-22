package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.utility.ConfigLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() throws SQLException {
        String url = ConfigLoader.getProperty("db.url");
        String user = ConfigLoader.getProperty("db.user");
        String pass = ConfigLoader.getProperty("db.password");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle connection could not be established. Did you set up the config correctly?", e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null)
            instance = new DatabaseConnection();
        else if (instance.getConnection() == null || instance.getConnection().isClosed())
            instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
    public void closeConnection() {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
                System.out.println("Oracle Database connection safely closed.");
            }
        } catch (SQLException e)
        {
            System.err.println("Error while closing the database connection.");
            e.printStackTrace();
        }
    }
}
