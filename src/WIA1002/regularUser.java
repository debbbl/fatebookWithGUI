package WIA1002;

import java.util.*;
import java.time.*;
import java.util.Stack;
public class regularUser extends user {
    private String name;
    private LocalDate birthday;
    private String gender;
    private int numOfFriend;
    private Stack<String> jobs;
    private List<String> hobbies;
    private String address;
    private String relationshipStatus;
    private byte[] profilePic;

    public regularUser(Builder builder) {
        super(builder);
        super.setRole("Regular User");
        this.name = builder.name;
        this.birthday = builder.birthday;
        this.address = builder.address;
        this.gender = builder.gender;
        this.hobbies = builder.hobbies;
        this.jobs = builder.jobs;
        this.profilePic = builder.profilePic;
        this.relationshipStatus = builder.relationshipStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getNumOfFriend() {
        return numOfFriend;
    }

    public void setNumOfFriend(int numOfFriend) {
        this.numOfFriend = numOfFriend;
    }

    public void addJobExperience(String job) {
        jobs.push(job);
    }

    public String getCurrentJobExperience() {
        return jobs.isEmpty() ? "" : jobs.peek();
    }

    public Stack<String> getJobExperience() {
        return jobs;
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

    public void setProfilePic(byte[] profilePicData) {
        if (profilePicData.length <= 16 * 1024 * 1024) {
            this.profilePic = profilePicData;
        } else {
            throw new IllegalArgumentException("Profile picture size exceeds the limit of 16MB.");
        }
    }

     public static class Builder extends user.Builder{
        private String name;
        private LocalDate birthday;
        private String gender;
        private int numOfFriend;
        private Stack<String> jobs;
        private List<String> hobbies;
        private String address;
        private String relationshipStatus;
        private byte[] profilePic;

         public Builder() {
             super();
             this.jobs = new Stack<>();
         }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder birthday(LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public Builder gender(String gender){
            this.gender = gender;
            return this;
        }

         public Builder jobs(Stack<String> jobExperience) {
             this.jobs = jobExperience;
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

        public Builder relationshipStatus(String relationshipStatus){
            this.relationshipStatus = relationshipStatus;
            return this;
        }

        public regularUser build() {
            return new regularUser(this);
        }


    }
}
