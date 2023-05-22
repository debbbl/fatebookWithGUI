package WIA1002;

public class user {
    protected String email;
    protected String password;
    protected String username;
    protected String contactNumber;
    protected String role;

    protected user(Builder builder){
        this.email = builder.email;
        this.password = builder.password;
        this.username = builder.username;
        this.contactNumber = builder.contactNumber;
        this.role = builder.role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static class Builder{
        private String email;
        private String password;
        private String username;
        private String contactNumber;
        private String role;

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

        public Builder role(String role){
            this.role = role;
            return this;
        }

        public user build(){
            return new user(this);
        }
    }
}
