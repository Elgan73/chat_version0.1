public interface AuthService {
    void start();
    String getNickByLogin(String login, String pass);
    void stop();
}
