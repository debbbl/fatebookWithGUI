package WIA1002;

public class Admin extends user {
    private Admin(Builder builder){
        super(builder);
        super.setIsAdmin(1);
    }
    public static class AdminBuilder extends user.Builder{
        public Admin build(){
            return new Admin(this);
        }
        public boolean isAdmin() {
            return true;
        }
    }
}
