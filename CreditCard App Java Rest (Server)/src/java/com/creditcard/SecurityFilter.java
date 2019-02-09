package com.creditcard;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;

@Provider
public class SecurityFilter implements ContainerRequestFilter {	
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization"; 
    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic "; 
    private static final String SECURED_URL_PREFIX[] = {"validator/listCC","validator/addCC","validator/updateCC","validator/deleteCC"};

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        boolean filter = false;
        
        for (String prefix : SECURED_URL_PREFIX) {
            if (requestContext.getUriInfo().getPath().contains(prefix)) {
                filter = true;
                break;
            }
        }
        
        if(filter) {
            List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
            if (authHeader != null && authHeader.size() > 0) {
                String authToken = authHeader.get(0);
                authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
                byte[] valueDecoded;
                try {
                    valueDecoded = Base64.decode(authToken);
                    String decodedString = new String(valueDecoded);
                    StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
                    String username = tokenizer.nextToken();
                    String password = tokenizer.nextToken();
                    
                    int resnum = 0;
                    ResultSet res = ConnectDB.execQuery("select count(*) from users where username = '" + username + "' and password = '" + password + "'", true);
                    try {
                        res.next();
                        resnum = res.getInt(1);
                    } catch (SQLException ex) {
                        Logger.getLogger(SecurityFilter.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if(resnum != 0) return;
                } catch (Base64DecodingException ex) {}
            }
            Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity("User cannot access the resource.").build();
            requestContext.abortWith(unauthorizedStatus);
        }
    }
}