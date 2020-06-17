public class Message {
    private String nick;
    private String date;
    private String message;

    public Message() {
    }

    public Message(String nick, String date, String message) {
        this.nick = nick;
        this.date = date;
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
