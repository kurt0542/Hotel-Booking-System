/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author ADMIN
 */
public class DBConnection {
    private static Connection conn;
    public static Connection connectDB(){
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            URL resource = DBConnection.class.getResource("/HotelBookingSystem.accdb");
            File file = new File(resource.toURI());
            String dbPath = file.getAbsolutePath();
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + dbPath);
            return conn;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }    
    }
    
}
