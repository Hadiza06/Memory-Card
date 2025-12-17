package hhb.project.cardgame;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:8889/memorycard?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // mot de passe MySQL

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
