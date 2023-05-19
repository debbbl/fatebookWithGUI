package Database;

import java.io.IOException;
import java.util.*;
import java.nio.file.*;
public class User {
    private String email;
    private String password;
    private String name;
    private String username;
    private String contactNumber;
    private String birthday;
    private char gender;
    private int numOfFriend;
    private String job;
    private List<String> hobbies;
    private String address;
    private String relationshipStatus;
    private byte[] profilePic;

    private User(Builder builder) {
        this.name = builder.name;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.contactNumber = builder.contactNumber;
        this.birthday = builder.birthday;
        this.address = builder.address;
        this.gender = builder.gender;
        this.hobbies = builder.hobbies;
        this.job = builder.job;
        this.profilePic = builder.profilePic;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getNumOfFriend() {
        return numOfFriend;
    }

    public void setNumOfFriend(int numOfFriend) {
        this.numOfFriend = numOfFriend;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String fileName) {
        try {
            Path filePath = Paths.get(fileName);
            byte[] profilePicData = Files.readAllBytes(filePath);

            if (profilePicData.length <= 16 * 1024 * 1024) {
                this.profilePic = profilePicData;
            } else {
                throw new IllegalArgumentException("Profile picture size exceeds the limit of 16MB.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public static class Builder{
        private String email;
        private String password;
        private String name;
        private String username;
        private String contactNumber;
        private String birthday;
        private char gender;
        private int numOfFriend;
        private String job;
        private List<String> hobbies;
        private String address;
        private String relationshipStatus;
        private byte[] profilePic;

        public Builder email(String email){
            this.email=email;
            return this;
        }

        public Builder password(String password){
            this.password=password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder contactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Builder birthday(String birthday) {
            this.birthday = birthday;
            return this;
        }

        public Builder gender(char gender){
            this.gender = gender;
            return this;
        }

        public Builder job(String job) {
            this.job = job;
            return this;
        }

        public Builder hobbies(List<String> hobbies) {
            this.hobbies = hobbies;
            return this;
        }



        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder profilePic(byte[] profilePic) {
            this.profilePic = profilePic;
            return this;
        }

        public User build() {
            return new User(this);
        }


    }
}
