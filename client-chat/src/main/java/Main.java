import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("authorized.fxml"));
        primaryStage.setTitle("Stark Chat ver: 0.1");
        primaryStage.setScene(new Scene(root, 600,460));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());

    }


    public static void main(String[] args) {
        launch(args);
    }
}
