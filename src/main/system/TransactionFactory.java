package src.main.system;

import src.main.command.*;
import src.main.exceptions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a factory that produces commands based on transaction codes 
 */
public class TransactionFactory {
    String pattern;
    Command command;
    private static final String LOGIN_CODE = "00";
    private static final String CREATE_CODE = "01";
    private static final String DELETE_CODE = "02";
    private static final String SELL_CODE = "03";
    private static final String BUY_CODE = "04";
    private static final String REFUND_CODE = "05";
    private static final String ADD_CREDIT_CODE = "06";
    private static final String AUCTION_CODE = "07";
    private static final String REMOVE_GAME_CODE = "08";
    private static final String GIFT_CODE = "09";
    private static final String LOGOUT_CODE = "10";

    /**
     * Gets a transaction command based on the provided code
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code the code that will be used to produce a transaction command.
     * @return a transaction command
     * @throws FatalException when the given code does not correspond to any existing transaction command
     */
    public Command getTransactionCommand(DistributionSystem system, String code) throws FatalException {
        String transactionType;
        try {
            transactionType = code.substring(0, 2);
        } catch (StringIndexOutOfBoundsException e) {
            throw new FatalException("Code: '" + code + "' is invalid.");
        }

        switch (transactionType) {
            case DELETE_CODE:
            case AUCTION_CODE:
            case LOGOUT_CODE:
                command = getLogoutDeleteAuction(system, code);
                break;
            case ADD_CREDIT_CODE:
                command = getAddCredit(system, code);
                break;
            case LOGIN_CODE:
            case CREATE_CODE:
                command = getLoginCreate(system, code);
                break;
            case REFUND_CODE:
                command = getRefund(system, code);
                break;
            case SELL_CODE:
                command = getSell(system, code);
                break;
            case BUY_CODE:
                command = getBuy(system, code);
                break;
            case REMOVE_GAME_CODE:
                command = getRemove(system, code);
                break;
            case GIFT_CODE:
                command = getGift(system, code);
                break;
            default:
                command = null;
        }

        if (command == null) {
            throw new FatalException("Code: '" + code + "' is invalid.");
        }
        return command;
    }

    /**
     * Produces a matcher to be used in regex operations given a regex pattern and a provided code.
     * @param pattern a regex pattern
     * @param code a transaction code to be matched
     * @return a Matcher based on the given pattern and code
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Matcher getMatcher(String pattern, String code) throws FatalException {
        Pattern P = Pattern.compile(pattern);
        Matcher matcher = P.matcher(code);
        if (!matcher.find()) {
            throw new FatalException("'" + code + "' does not follow a correct transaction code format.");
        }
        return matcher;
    }

    /**
     * Produces a Delete, AuctionSale, or Logout command based on the given code.
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a code that corresponds to either a delete, auction sale, or logout operation.
     * @return one of DeleteCommand, AuctionSaleCommand, or LogoutCommand
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getLogoutDeleteAuction(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<username>.{15}) (?<type>\\s{2}) (?<unused>(0{6}\\.0{2}|0{9}))";
        Matcher matcher = getMatcher(pattern, code);
        switch (matcher.group("code")) {
            case DELETE_CODE:
                return new DeleteCommand(system, matcher.group("username"));
            case AUCTION_CODE:
                return new AuctionsaleCommand(system, matcher.group("username"));
            case LOGOUT_CODE:
                return new LogoutCommand(system);
        }
        return null;
    }


    /**
     * Produces an AddCredit command based on the given system and code
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a code that corresponds to an AddCreditCommand
     * @return an AddCreditCommand relating to the given add credit code.
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getAddCredit(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<username>.{15}) (?<type>\\s{2}) (?<credit>\\d{6}\\.\\d{2})";
        Matcher matcher = getMatcher(pattern, code);
        return new AddCreditCommand(system, matcher.group("username"),
                matcher.group("credit"));
    }

    /**
     * Produces either a LoginCommand or a CreateCommand.
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return a LoginCommand or a CreateCommand corresponding to the given code
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getLoginCreate(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<username>.{15}) (?<type>(FS|AA|BS|SS)) (?<credit>\\d{6}\\.\\d{2})";
        Matcher matcher = getMatcher(pattern, code);
        switch (matcher.group("code")) {
            case LOGIN_CODE:
                return new LoginCommand(system, matcher.group("username"),
                        matcher.group("type"),
                        matcher.group("credit"));
            case CREATE_CODE:
                return new CreateCommand(system, matcher.group("username"),
                        matcher.group("type"),
                        matcher.group("credit"));
        }
        return null;
    }

    /**
     * Generates a RefundCommand based on the given transaction code.
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return A RefundCommand corresponding to the provided code.
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getRefund(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<buyerUsername>.{15}) (?<sellerUsername>.{15}) (?<credit>\\d{6}\\.\\d{2})";
        Matcher matcher = getMatcher(pattern, code);
        return new RefundCommand(system, matcher.group("buyerUsername"),
                matcher.group("sellerUsername"),
                matcher.group("credit"));
    }

    /**
     * Generates a SellCommand based on the given transaction code.
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return A SellCommand corresponding to the provided transaction code
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getSell(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<gameName>.{25}) (?<sellerUsername>.{15}) (?<discount>\\d{2}\\.\\d{2}) (?<price>\\d{3}\\.\\d{2})";
        Matcher matcher = getMatcher(pattern, code);
        return new SellCommand(system, matcher.group("gameName"),
                matcher.group("sellerUsername"),
                matcher.group("discount"),
                matcher.group("price"));
    }

    /**
     * Generates a BuyCommand based on the given transaction code.
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return A BuyCommand corresponding to the provided transaction code
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getBuy(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<gameName>.{25}) (?<sellerUsername>.{15}) (?<buyerUsername>.{15})";
        Matcher matcher = getMatcher(pattern, code);
        return new BuyCommand(system, matcher.group("gameName"),
                matcher.group("sellerUsername"),
                matcher.group("buyerUsername"));
    }

    /**
     * Generates either a RemoveGameCommand based on the given transaction code
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return One of RemoveGameCommand
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getRemove(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<gameName>.{25}) (?<ownerName>.{15}) (?<receiverName>\\s{15})";
        Matcher matcher = getMatcher(pattern, code);
        return new RemoveGameCommand(system, matcher.group("gameName"),
                matcher.group("ownerName"));
    }

    /**
     * Generates either a GiftCommand based on the given transaction code
     * @param system the distribution system that keeps track of the User that would perform the returned transaction
     * @param code a transaction code
     * @return One of GiftCommand
     * @throws FatalException when the provided code doesn't follow the provided pattern
     */
    private Command getGift(DistributionSystem system, String code) throws FatalException {
        pattern = "(?<code>\\d{2}) (?<gameName>.{25}) (?<ownerName>.{15}) (?<receiverName>.{15})";
        Matcher matcher = getMatcher(pattern, code);
        return new GiftCommand(system, matcher.group("gameName"),
                matcher.group("ownerName"),
                matcher.group("receiverName"));
    }

    /**
     * @return the code corresponding to a CreateCommand transaction.
     */
    public static String getCreateCode() {
        return CREATE_CODE;
    }

    /**
     * @return the code corresponding to a DeleteCommand transaction.
     */
    public static String getDeleteCode() {
        return DELETE_CODE;
    }

    /**
     * @return the code corresponding to a SellCommand transaction.
     */
    public static String getSellCode() {
        return SELL_CODE;
    }

    /**
     * @return the code corresponding to a LoginCommand transaction.
     */
    public static String getLoginCode() {
        return LOGIN_CODE;
    }

    /**
     * @return the code corresponding to a SellCommand transaction.
     */
    public static String getBuyCode() {
        return BUY_CODE;
    }

    /**
     * @return the code corresponding to a RefundCommand transaction.
     */
    public static String getRefundCode() {
        return REFUND_CODE;
    }

    /**
     * @return the code corresponding to a AuctionSaleCommand transaction.
     */
    public static String getAuctionCode() {
        return AUCTION_CODE;
    }

    /**
     * @return the code corresponding to a AddCreditCommand transaction.
     */
    public static String getAddCreditCode() {
        return ADD_CREDIT_CODE;
    }

    /**
     * @return the code corresponding to a GiftCommand transaction.
     */
    public static String getGiftCode() {
        return GIFT_CODE;
    }

    /**
     * @return the code corresponding to a LogoutCommand transaction.
     */
    public static String getLogoutCode() {
        return LOGOUT_CODE;
    }

    /**
     * @return the code corresponding to a RemoveGameCommand transaction.
     */
    public static String getRemoveGameCode() {
        return REMOVE_GAME_CODE;
    }
}
