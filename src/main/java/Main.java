
import Database.DBConnection;
import GUI.LoginPage;
import com.formdev.flatlaf.FlatLightLaf;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ADMIN
 */
public class Main {
    public static void main(String[] args){
        FlatLightLaf.setup();
        new LoginPage().setVisible(true);
        try{
        DBConnection.connectDB();
        System.out.println("Connected Successfully!");
        }
        catch(Exception e){
            
        }
    }
}
