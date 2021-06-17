package com.example.sosapp.Model;

public class Contacts extends com.example.sosapp.Model.ContactID {

    String name, contact_num;

    public Contacts() {}

    public Contacts(String name, String contact_num) {
        this.name = name;
        this.contact_num = contact_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_num() {
        return contact_num;
    }

    public void setContact_num(String contact_num) {
        this.contact_num = contact_num;
    }
}
