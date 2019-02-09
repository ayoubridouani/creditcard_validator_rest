package com.example.anonsurf.creditcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Signin extends AppCompatActivity implements View.OnClickListener{
    private EditText firstname;
    private EditText lastname;
    private EditText username;
    private EditText password;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firstname = this.findViewById(R.id.firstname);
        lastname = this.findViewById(R.id.lastname);
        username = this.findViewById(R.id.username);
        password = this.findViewById(R.id.password);
        register = this.findViewById(R.id.register);

        register.setOnClickListener(this);
    }

    public EditText getFirstname() {
        return firstname;
    }

    public EditText getLastname() {
        return lastname;
    }

    public EditText getUsername() {
        return username;
    }

    public EditText getPassword() {
        return password;
    }

    public Button getRegister() {
        return register;
    }

    @Override
    public void onClick(View v) {
        if(getFirstname().getText().toString().isEmpty()){
            getFirstname().setError("enter your firstname");
        }
        if(getLastname().getText().toString().isEmpty()){
            getLastname().setError("enter your lastname");
        }
        if(getUsername().getText().toString().isEmpty()){
            getUsername().setError("enter your username");
        }
        if(getPassword().getText().toString().isEmpty()){
            getPassword().setError("enter your password");
        }
        if(!getFirstname().getText().toString().isEmpty() && !getLastname().getText().toString().isEmpty() && !getUsername().getText().toString().isEmpty() && !getPassword().getText().toString().isEmpty()){
            Main main = new Main();
            main.execute(getFirstname().getText().toString(),getLastname().getText().toString(),getUsername().getText().toString(),getPassword().getText().toString());
        }
    }

    class Main extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                URL url = new URL("http://192.168.1.2:8080/CreditCard/api/validator/addUser");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/xml");
                //connection.setRequestProperty("Accept", "text/plain");
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                String urlParameters = "<users><id></id><firstname>" + strings[0] + "</firstname><lastname>" + strings[1] + "</lastname><username>" + strings[2] + "</username><password>" + strings[3] + "</password></users>";
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String res;
                while ((res = in.readLine()) != null) result += res;
                in.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            if(s.equals("true")) {
                Log.d("lklk","kjkljkl");
               // invalidInformation();
            }
        }
    }

}
