import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ch.fxml"));
        stage.setTitle("Chat ver: 0.1");
        stage.setScene(new Scene(root, 600,460));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
