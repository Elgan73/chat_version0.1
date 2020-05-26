
import java.util.concurrent.ConcurrentLinkedDeque;

public class BaseAuthService {
    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login) {
            this.login = login;

        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }


    }







}
