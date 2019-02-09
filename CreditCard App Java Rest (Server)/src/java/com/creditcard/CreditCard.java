package com.creditcard;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreditCard{
    private int id;
    private String cardNumber;
    private String expirationDate;
    private String securityCode;
    private String payementNetwork;

    public CreditCard() {

    }

    public CreditCard(int id, String cardNumber, String expirationDate, String securityCode, String payementNetwork) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.securityCode = securityCode;
        this.payementNetwork = payementNetwork;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getPayementNetwork() {
        return payementNetwork;
    }

    public void setPayementNetwork(String payementNetwork) {
        this.payementNetwork = payementNetwork;
    }
}