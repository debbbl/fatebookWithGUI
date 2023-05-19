package sample;
import java.sql.*;
public class tempDatabase {
    public Connection databaseLink;

    public Connection getConnection(){
        String databaseName = "facebook";
        String databaseUser = "root";
        String databasePassword = "facebookds";
        String url = "jdbc:mysql://localhost:3306/facebook";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }
}
