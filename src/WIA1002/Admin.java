package WIA1002;

public class Admin extends user {
    private Admin(Builder builder){
        super(builder);
        super.setRole("Admin");
    }

    public static class Builder extends user.Builder{
        public Admin build(){
            return new Admin(this);
        }
    }
}
