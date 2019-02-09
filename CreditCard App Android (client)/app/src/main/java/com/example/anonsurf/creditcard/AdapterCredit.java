package com.example.anonsurf.creditcard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class AdapterCredit extends ArrayAdapter<CreditCard> {
    ArrayList<CreditCard> arl;
    int res;
    Context context;

    public AdapterCredit(Context context, int resource, ArrayList<CreditCard> objects) {
        super(context, resource, objects);

        this.arl=objects;
        this.res=resource;
        this.context=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View v=inflater.inflate(res,parent,false);

        final ViewHolder vh = new ViewHolder();

        vh.id = v.findViewById(R.id.id);
        vh.cardNumber = v.findViewById(R.id.cardNumber);
        vh.expirationDate = v.findViewById(R.id.expirationDate);
        vh.securityCode = v.findViewById(R.id.securityCode);
        vh.payementNetwork = v.findViewById(R.id.payementNetwork);
        vh.update = v.findViewById(R.id.update);
        vh.delete = v.findViewById(R.id.delete);

        vh.id.setText(String.valueOf(arl.get(position).getId()));
        vh.cardNumber.setText("Card Number: " + arl.get(position).getCardNumber());
        vh.securityCode.setText("Security Code: " + arl.get(position).getSecurityCode());
        vh.expirationDate.setText("Expiration Date: " + arl.get(position).getExpirationDate());
        vh.payementNetwork.setText("Payement Network: " + arl.get(position).getPayementNetwork());

        vh.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custum_dialog);
                dialog.setTitle("Update Credit Card");

                final EditText d_id = dialog.findViewById(R.id.d_id);
                final EditText d_cardNumber = dialog.findViewById(R.id.d_cardNumber);
                final EditText d_expirationDate = dialog.findViewById(R.id.d_expirationDate);
                final EditText d_securityCode = dialog.findViewById(R.id.d_securityCode);
                final EditText d_payementNetwork = dialog.findViewById(R.id.d_payementNetwork);
                final Button d_update = dialog.findViewById(R.id.d_update);
                final Button d_cancel = dialog.findViewById(R.id.d_cancel);

                d_id.setText(vh.id.getText().toString());

                StringTokenizer tokenizer1 = new StringTokenizer(vh.cardNumber.getText().toString()," ");
                if(tokenizer1.countTokens() == 2)
                    d_cardNumber.setText("");
                else{
                    tokenizer1.nextToken();
                    tokenizer1.nextToken();
                    d_cardNumber.setText(tokenizer1.nextToken());
                }

                StringTokenizer tokenizer2 = new StringTokenizer(vh.securityCode.getText().toString()," ");
                if(tokenizer2.countTokens() == 2)
                    d_securityCode.setText("");
                else{
                    tokenizer2.nextToken();
                    tokenizer2.nextToken();
                    d_securityCode.setText(tokenizer2.nextToken());
                }

                StringTokenizer tokenizer3 = new StringTokenizer(vh.expirationDate.getText().toString()," ");
                if(tokenizer3.countTokens() == 2)
                    d_expirationDate.setText("");
                else{
                    tokenizer3.nextToken();
                    tokenizer3.nextToken();
                    d_expirationDate.setText(tokenizer3.nextToken());
                }

                StringTokenizer tokenizer4 = new StringTokenizer(vh.payementNetwork.getText().toString()," ");
                if(tokenizer4.countTokens() == 2)
                    d_payementNetwork.setText("");
                else{
                    tokenizer4.nextToken();
                    tokenizer4.nextToken();
                    d_payementNetwork.setText(tokenizer4.nextToken());
                }

                dialog.show();

                d_update.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int error = 0;
                        if(d_cardNumber.getText().toString().isEmpty()) {
                            error++;
                            d_cardNumber.setError("insert card number");
                        }
                        if(d_expirationDate.getText().toString().isEmpty()){
                            error++;
                            d_expirationDate.setError("insert expiration date");
                        }
                        if(d_securityCode.getText().toString().isEmpty()){
                            d_securityCode.setError("insert code security");
                        }
                        if(d_payementNetwork.getText().toString().isEmpty()){
                            d_payementNetwork.setError("insert payement network");
                        }
                        if(error == 0) {
                            Bank.Main main = new Bank.Main();
                            main.execute("updateCC/" + d_id.getText().toString(),"PUT",d_id.getText().toString(),d_cardNumber.getText().toString(),d_expirationDate.getText().toString(),d_payementNetwork.getText().toString(),d_securityCode.getText().toString());

                            dialog.cancel();
                            Toast.makeText(context,"Credit Card Updated",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                d_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });

        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bank.Main main = new Bank.Main();
                main.execute("deleteCC/id/" + vh.id.getText().toString(),"DELETE");
                Toast.makeText(context,"Credit Card Deleted",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    static class ViewHolder {
        private TextView id;
        private TextView cardNumber;
        private TextView expirationDate;
        private TextView securityCode;
        private TextView payementNetwork;
        private Button update;
        private Button delete;
    }

}