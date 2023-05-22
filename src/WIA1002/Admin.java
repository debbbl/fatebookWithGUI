package WIA1002;

import java.time.LocalDate;
import java.util.List;

public class Admin extends user {
    private Admin(Builder builder){
        super(builder);
        super.setRole("Admin");
    }

    public void deleteUserAccount(String username) {
        tempDatabase database = new tempDatabase();
        database.deleteUserAccount(username);
    }

    public List<regularUser> viewNewlyCreatedAccounts(LocalDate startDate, LocalDate endDate) {
        tempDatabase database = new tempDatabase();
        return database.getNewlyCreatedAccounts(startDate, endDate);
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
