package WIA1002;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;
import java.util.Stack;
public class regularUser extends user {
    private int userId;
    private String name;
    private LocalDate birthday;
    private String gender;
    private int numOfFriend;
    private Stack<String> jobs;
    private List<String> hobbies;
    private String address;
    private String relationshipStatus;
    private byte[] profilePic;
    private List<String> friendList;
    private LinkedList<String> actionHistory;
    private LinkedList<ChatMessage> chatHistory;
    private int mutualConnectionsCount;

    public regularUser(RegularUserBuilder regularUserBuilder,LocalDateTime timestamp) {
        super(regularUserBuilder);
        super.setIsAdmin(0);
        super.setTimeStamp(timeStamp);
        this.userId = regularUserBuilder.userId;
        this.name = regularUserBuilder.name;
        this.birthday = regularUserBuilder.birthday;
        this.address = regularUserBuilder.address;
        this.gender = regularUserBuilder.gender;
        this.hobbies = regularUserBuilder.hobbies;
        this.jobs = regularUserBuilder.jobs;
        this.profilePic = regularUserBuilder.profilePic;
        this.relationshipStatus = regularUserBuilder.relationshipStatus;
        this.friendList = regularUserBuilder.friendList;
        this.actionHistory = new LinkedList<>();
        this.chatHistory = new LinkedList<>();
    }

    public void addActionToHistory(String action, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | hh.mm a");
        String formattedtimestamp = timestamp.format(formatter);
        String actionWithTimestamp = "[ "+formattedtimestamp+" ] "+action;
        actionHistory.add(actionWithTimestamp);
    }

    public int getMutualConnectionsCount() {
        return mutualConnectionsCount;
    }

    public void setMutualConnectionsCount(int mutualConnectionsCount) {
        this.mutualConnectionsCount = mutualConnectionsCount;
    }

    public LinkedList<String> getActivityHistory() {
        return actionHistory;
    }

    public LinkedList<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(LinkedList<ChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }
    public String getEmailAddress(){
        return getEmail();
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return userId;
    }

    public LocalDateTime getTimeStamp(){
        return super.getTimeStamp();
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
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

    public static class RegularUserBuilder extends user.Builder{
        private int userId;
        private String name;
        private LocalDate birthday;
        private String gender;
        private int numOfFriend;
        private Stack<String> jobs;
        private List<String> hobbies;
        private String address;
        private String relationshipStatus;
        private byte[] profilePic;
        private List<String> friendList;

         public RegularUserBuilder() {
             super();
             this.jobs = new Stack<>();
         }

         public RegularUserBuilder userId(int userId){
             this.userId = userId;
             return this;
         }

         public RegularUserBuilder timeStamp(LocalDateTime timeStamp) {
             this.timeStamp = timeStamp;
             return this;
         }
         public RegularUserBuilder username(String username) {
             super.username(username);
             return this;
         }

         public RegularUserBuilder email(String email) {
             super.email(email);
             return this;
         }

         public RegularUserBuilder contactNumber(String contactNumber) {
             super.contactNumber(contactNumber);
             return this;
         }

         public RegularUserBuilder name(String name) {
             this.name = name;
             return this;
         }

        public RegularUserBuilder birthday(LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public RegularUserBuilder gender(String gender){
            this.gender = gender;
            return this;
        }

         public RegularUserBuilder jobs(Stack<String> jobExperience) {
             this.jobs = jobExperience;
             return this;
         }

        public RegularUserBuilder hobbies(List<String> hobbies) {
            this.hobbies = hobbies;
            return this;
        }

        public RegularUserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public RegularUserBuilder profilePic(byte[] profilePic) {
            this.profilePic = profilePic;
            return this;
        }

        public RegularUserBuilder relationshipStatus(String relationshipStatus){
            this.relationshipStatus = relationshipStatus;
            return this;
        }

         public RegularUserBuilder numOfFriend(int numOfFriend) {
             this.numOfFriend = numOfFriend;
             return this;
         }

         public RegularUserBuilder friendList(List<String> friendList){
             this.friendList = friendList;
             return this;
         }

        public regularUser build() {
            return new regularUser(this, timeStamp);
        }



     }
}
