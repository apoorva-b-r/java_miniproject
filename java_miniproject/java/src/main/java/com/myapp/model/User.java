package com.myapp.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String name;
    private String mobile;
    private String gmail;

    // constructors
    public User() {}
    public User(int id, String username, String passwordHash,String name, String mobile, String gmail) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.mobile = mobile;
        this.gmail = gmail;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }
}
