package com.model;
import java.io.Serializable;

public class User implements Serializable {

	private int userId;
    private String userName;
    private String email;
    private String mobileNumber;
    private String address;
    private String password;
    private Role role;
    
    public User(int userId, String userName, String email,
            String mobileNumber, String address,
            String password, Role role) {

    this.userId = userId;
    this.userName = userName;
    this.email = email;
    this.mobileNumber = mobileNumber;
    this.address = address;
    this.password = password;
    this.role = role;
}
    
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    @Override
    public String toString() {

        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", address='" + address + '\'' +
                ", role=" + role +
                '}';
    }
    
    
	
}
