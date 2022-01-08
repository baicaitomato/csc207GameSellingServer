package src.main.users;

import src.main.exceptions.BalanceException;
import src.main.exceptions.UsernameException;

/**
 * A factory that can produce all types of users.
 */
public class UserFactory {

    /**
     * Makes a new user with the specified parameters.
     * @param userName the username of the new user
     * @param userType the type of the new user.
     * @param balance the initial balance of this users account
     * @throws UsernameException if the username violates restrictions or the username already exists.
     */
    public void makeUser(String userName, String userType, double balance) throws UsernameException, BalanceException {
        switch (userType) {
            case (User.ADMIN_TYPE):
                new AdminUser(userName, balance);
                break;
            case User.FULL_STANDARD_TYPE:
                new FullStandardUser(userName, balance);
                break;
            case User.BUYER_TYPE:
                new BuyStandardUser(userName, balance);
                break;
            case User.SELLER_TYPE:
                new SellStandardUser(userName, balance);
                break;
            default:
                throw new UsernameException("The type does not match any usertype!");
        }
    }
}
