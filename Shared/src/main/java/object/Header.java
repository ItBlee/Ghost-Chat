package object;

import java.util.Objects;

/**
 * Tag header của DTO
 */
public final class Header {
    public static final Header BREAK_CONNECT = new Header("Break_Connect");
    public static final Header BREAK_PAIR = new Header("Break_Pair");
    public static final Header PAIR_LEFT = new Header("Pair_Left");
    public static final Header NAME_CHECK = new Header("Name_Check");
    public static final Header SERVER_BUSY = new Header("Server_Busy");
    public static final Header FIND_CHAT = new Header("Find_Chat");
    public static final Header STOP_FIND = new Header("Stop_Find_Chat");
    public static final Header INVITE_CHAT = new Header("Invite_Chat");
    public static final Header INVITE_CHAT_FAIL = new Header("Invite_Chat_Fail");
    public static final Header CONFIRM_CHAT = new Header("Confirm_Chat");
    public static final Header PAIRED_CHAT = new Header("Paired_Chat");
    public static final Header USER_INFO = new Header("User_Info");
    public static final Header HISTORY_RECOVERY = new Header("History_Chat");
    public static final Header MESSAGE = new Header("Message");
    public static final Header MESSAGE_SERVER = new Header("Message_Server");

    public static final Header AUTH_LOGIN = new Header("Log_In");
    
    private final String string;

    private Header(String string) {
        this.string = string;
    }

    public boolean equals(String s) {
        return string.equalsIgnoreCase(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Header)) return false;
        Header header1 = (Header) o;
        return Objects.equals(string, header1.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }

    @Override
    public String toString() {
        return string;
    }
}
