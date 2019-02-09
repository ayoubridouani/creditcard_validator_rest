package com.example.anonsurf.creditcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Bank extends AppCompatActivity implements View.OnClickListener{
    private TextView cardNumber,codeSecurity,expirationDate;
    private Spinner paiementNetwork;
    private Button check;
    private ListView list;
    private static ArrayList<CreditCard> list_items;
    private static AdapterCredit adapterList;
    private static String username;
    private static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardNumber = findViewById(R.id.number);
        codeSecurity = findViewById(R.id.code);
        expirationDate = findViewById(R.id.date);
        paiementNetwork = findViewById(R.id.type);
        check = findViewById(R.id.valide);
        list = findViewById(R.id.list);

        String networks[] = {"Visa","Master card","CMI","Maestro"};
        ArrayAdapter<String> adapterSpin = new ArrayAdapter(this,android.R.layout.simple_spinner_item,networks);
        paiementNetwork.setAdapter(adapterSpin);

        list_items = new ArrayList<>();
        adapterList = new AdapterCredit(this,R.layout.lay_item_prod,list_items);
        list.setAdapter(adapterList);

        if(getIntent()!=null){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            username = bundle.getString("user");
            password = bundle.getString("pass");
        }

        Main main = new Main();
        main.execute("listCC","GET");

        check.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String types[] = {"visa","master card","cmi","maestro"};
        boolean isTyped = false;
        for(String type : types){
            if(type.equals(String.valueOf(paiementNetwork.getSelectedItem()).toLowerCase())) isTyped = true;
        }
        if(cardNumber.getText().length() == 16 && codeSecurity.getText().length() == 3 && isTyped){
            Main main = new Main();
            main.execute("addCC","POST","",cardNumber.getText().toString(),expirationDate.getText().toString(),String.valueOf(paiementNetwork.getSelectedItem()).toLowerCase(),codeSecurity.getText().toString());
            Toast.makeText(Bank.this,"Credit Card Added",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Bank.this,"Invalid Credit Card",Toast.LENGTH_LONG).show();
        }
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e){
            Log.d("convert String To XML:",e.getMessage());
        }
        return null;
    }

    static class Main extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                URL url = new URL("http://192.168.1.2:8080/CreditCard/api/validator/" + strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setRequestMethod(strings[1]);

                connection.setRequestProperty("Accept", "application/xml");
                String auth = username + ":" + password;
                byte[] data = auth.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                connection.setRequestProperty("Authorization", "Basic " + base64);
                if(strings[0].contains("deleteCC") || strings[0].contains("listCC")) {
                    connection.connect();
                }
                else{
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/xml");
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    String urlParameters = "<creditCard><cardNumber>" + strings[3] + "</cardNumber><expirationDate>" + strings[4] + "</expirationDate><payementNetwork>" + strings[5] + "</payementNetwork><securityCode>" + strings[6] + "</securityCode></creditCard>";
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String res;
                while ((res = in.readLine()) != null) result += res;
                in.close();

                Document doc = convertStringToXMLDocument(result);
                NodeList nodesCreditCard = doc.getElementsByTagName("creditCard");
                NodeList nodesId = doc.getElementsByTagName("id");
                NodeList nodesCardNumber = doc.getElementsByTagName("cardNumber");
                NodeList nodesExpirationDate = doc.getElementsByTagName("expirationDate");
                NodeList nodesSecurityCode = doc.getElementsByTagName("securityCode");
                NodeList nodesPayementNetwork = doc.getElementsByTagName("payementNetwork");

                list_items.clear();
                for(int i=0;i< nodesCreditCard.getLength();i++){
                    Element elementId = (Element) nodesId.item(i);
                    String nameId = elementId.getTextContent();
                    Element elementCardNumber = (Element) nodesCardNumber.item(i);
                    String nameCardNumber = elementCardNumber.getTextContent();
                    Element elementExpirationDate = (Element) nodesExpirationDate.item(i);
                    String nameExpirationDate = elementExpirationDate.getTextContent();
                    Element elementSecurityCode = (Element) nodesSecurityCode.item(i);
                    String nameSecurityCode = elementSecurityCode.getTextContent();
                    Element elementPayementNetwork = (Element) nodesPayementNetwork.item(i);
                    String namePayementNetwork = elementPayementNetwork.getTextContent();
                    list_items.add(new CreditCard(Integer.parseInt(nameId),nameCardNumber,nameExpirationDate,nameSecurityCode,namePayementNetwork));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strings[0];
        }

        @Override
        protected void onPostExecute(String s){
            adapterList.notifyDataSetChanged();
        }
    }

}

