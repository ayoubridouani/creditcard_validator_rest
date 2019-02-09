package com.creditcard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDB {
    private static Connection con;
    private static Statement stmt;
    private static ResultSet res;
    
    public static ResultSet execQuery(String sql,boolean isRes){
        res = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/creditcard", "root", "");
            stmt = con.createStatement();
            if(isRes){
                res = stmt.executeQuery(sql);
            }else{
                stmt.execute(sql);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
