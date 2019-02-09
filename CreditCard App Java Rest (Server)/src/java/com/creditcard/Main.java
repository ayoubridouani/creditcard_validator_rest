package com.creditcard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/validator")
public class Main {
    private ResultSet res;
    
    public Main() {
        res = null;
    }
    
    @GET
    @Path("/checkUser/{username}/{password}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String checkUser(@PathParam("username") String username,@PathParam("password") String password){
        int resnum = 0;
        res = ConnectDB.execQuery("select count(*) from users where username = '" + username + "' and password = '" + password + "'", true);
        try {
            res.next();
            resnum = res.getInt(1);
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(SecurityFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(resnum != 0) { 
            return "founded";
        }else{
            return "not founded";
        }
    }
    
    @POST
    @Path("/addUser")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public String addUser(Users user){
        ConnectDB.execQuery("insert into users (firstname, lastname, username, password) value ('" + user.getFirstname() + "','" + user.getLastname() + "','" + user.getUsername() + "','" + user.getPassword() + "')", false);
        return "true";
    }
    
    @GET
    @Path("/listCC")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public ArrayList<CreditCard> listCC(){
        return listerCC();
    }
    
    @POST
    @Path("/addCC")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public ArrayList<CreditCard> validator(CreditCard cc){
        ConnectDB.execQuery("insert into creditcard (cardNumber, code, expirationDate, payementNetwork) value ('" + cc.getCardNumber() + "','" + cc.getSecurityCode() + "','" + cc.getExpirationDate() + "','" + cc.getPayementNetwork() + "')", false);
        return listerCC();
    }
    
    @PUT
    @Path("/updateCC/{id}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public ArrayList<CreditCard> updateCC(@PathParam("id") int id,CreditCard cc){
        String query = "update creditcard set cardNumber = '" + cc.getCardNumber() + "' ,code = '" + cc.getSecurityCode() + "' ,expirationDate = '"  + cc.getExpirationDate() + "' ,payementNetwork = '"  + cc.getPayementNetwork() + "' where id = " + id;
        ConnectDB.execQuery(query, false);
        return listerCC();
    }
    
    @DELETE
    @Path("/deleteCC/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public ArrayList<CreditCard> deleteCC(@PathParam("id") int id){
        ConnectDB.execQuery("delete from creditcard where id = " + id, false);
        return listerCC();
    }
    
    public ArrayList<CreditCard> listerCC(){
        ArrayList<CreditCard> creditCard = new ArrayList<>();
        res = ConnectDB.execQuery("select * from creditcard", true);
        try {
            while(res.next()){
                CreditCard cc = new CreditCard();
                cc.setId(res.getInt(1));
                cc.setCardNumber(res.getString(2));
                cc.setSecurityCode(res.getString(3));
                cc.setExpirationDate(res.getString(4));
                cc.setPayementNetwork(res.getString(5));
                creditCard.add(cc);
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return creditCard;
    }
}
