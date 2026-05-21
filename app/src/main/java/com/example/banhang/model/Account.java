package com.example.banhang.model;

public class Account {
    private int account_id;
    private String sdt;
    private String password;

    public Account() {
    }

    public Account(int account_id, String sdt, String password) {
        this.account_id = account_id;
        this.sdt = sdt;
        this.password = password;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
