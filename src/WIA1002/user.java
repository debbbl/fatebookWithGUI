package WIA1002;

import java.time.LocalDateTime;

public class user {
    protected String email;
    protected String password;
    protected String username;
    protected String contactNumber;
    protected int isAdmin;
    protected LocalDateTime timeStamp;
    protected user(Builder builder){
        this.email = builder.email;
        this.password = builder.password;
        this.username = builder.username;
        this.contactNumber = builder.contactNumber;
        this.isAdmin = builder.isAdmin;
        this.timeStamp=builder.timeStamp;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public static class Builder{
        protected String email;
        protected String password;
        protected String username;
        protected String contactNumber;
        protected int isAdmin;
        protected LocalDateTime timeStamp;
        public Builder email(String email){
            this.email=email;
            return this;
        }
        public Builder password(String password){
            this.password = password;
            return this;
        }
        public Builder username(String username){
            this.username = username;
            return this;
        }
        public Builder contactNumber(String contactNumber){
            this.contactNumber = contactNumber;
            return this;
        }
        public Builder isAdmin(int isAdmin){
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder timeStamp(LocalDateTime timeStamp){
            this.timeStamp = timeStamp;
            return this;
        }
        public user build(){
            return new user(this);
        }
    }
}
