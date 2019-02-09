package com.example.anonsurf.creditcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private EditText user;
    private EditText pass;
    private Button login;
    private TextView signin;
    private static boolean redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = this.findViewById(R.id.user);
        pass = this.findViewById(R.id.pass);
        login = this.findViewById(R.id.login);
        signin = this.findViewById(R.id.signin);
        redirect = false;

        login.setOnClickListener(this);
        signin.setOnClickListener(this);

    }

    public EditText getUser() {
        return user;
    }

    public EditText getPass() {
        return pass;
    }

    public void invalidInformation(){
        if(redirect){
            Bundle bundle = new Bundle();
            bundle.putString("user",getUser().getText().toString());
            bundle.putString("pass",getPass().getText().toString());
            Intent intent = new Intent(this,Bank.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else {
            Toast.makeText(this, "cannot access the resource.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login){
            if(getUser().getText().toString().isEmpty()){
                getUser().setError("enter username");
            }
            else if(getPass().getText().toString().isEmpty()){
                getPass().setError("enter password");
            }
            else{
                Main main = new Main();
                main.execute(getUser().getText().toString(),getPass().getText().toString());
            }
        }
        else if(v.getId() == R.id.signin){
            Intent intent = new Intent(this,Signin.class);
            startActivity(intent);
        }
        else{

        }
    }

    class Main extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                URL url = new URL("http://192.168.1.2:8080/CreditCard/api/validator/checkUser/" + strings[0] + "/" + strings[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.connect();

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
            if(s.equals("founded")) {
                redirect = true;
                invalidInformation();
            }
        }
    }
}