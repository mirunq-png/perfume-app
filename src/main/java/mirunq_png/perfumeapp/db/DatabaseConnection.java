package mirunq_png.perfumeapp.db;

import mirunq_png.perfumeapp.model.Perfume;
import mirunq_png.perfumeapp.utility.ConfigLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName("oracle.jdbc.driver.OracleDriver"); // safety measure for tomcat
        String url = ConfigLoader.getProperty("db.url");
        String user = ConfigLoader.getProperty("db.user");
        String pass = ConfigLoader.getProperty("db.password");
        try
        {
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e)
        {
            throw new SQLException("Oracle connection could not be established. Did you set up the config correctly?", e); // fatal
        }
    }

    public static DatabaseConnection getInstance() throws SQLException, ClassNotFoundException
    {
        if (instance!=null && isHealthy(instance)) // connection exists and is valid
            return instance;
        instance=new DatabaseConnection(); // connection needs to be created
        return instance;
    }

    private static boolean isHealthy(DatabaseConnection db) throws SQLException
    {
        try
        {
            return db!=null && db.getConnection()!=null&&!db.getConnection().isClosed();
        } catch (SQLException e)
        {
            return false;
        }
    }

    public Connection getConnection()
    {
        return connection;
    }
    public void closeConnection()
    {
        try
        {
            if (connection==null || connection.isClosed()) // nothing to close
                return;
            connection.close();
            System.err.println("Oracle Database connection safely closed.");
        } catch (SQLException e)
        {
            System.err.println("Error while closing the database connection.");
            e.printStackTrace();
        }
    }
}
