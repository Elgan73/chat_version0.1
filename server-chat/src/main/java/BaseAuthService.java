import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BaseAuthService implements AuthService {
    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }
    }

    private ConcurrentLinkedDeque<Entry> entries;



//    public BaseAuthService(ConcurrentLinkedDeque<Entry> entries) {
//        entries.add(Server.getClients())
//    }

    @Override
    public void start() {
        System.out.println("AuthService is started");
    }

    @Override
    public String getNickByLogin(String login, String pass) {
        return null;
    }

    @Override
    public void stop() {
        System.out.println("AuthService is stopped");
    }
}
