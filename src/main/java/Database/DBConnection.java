/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connectDB() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String projectRoot = System.getProperty("user.dir");
            String dbPath = projectRoot + File.separator + "db" + File.separator + "HotelBookingSystem.accdb";

            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                System.err.println("Database file not found at: " + dbPath);
                return null;
            }

            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + dbPath);

            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

