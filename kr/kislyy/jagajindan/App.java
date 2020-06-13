package kr.kislyy.jagajindan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    private static App instance;

    private Stage primaryStage;
    private VBox rootLayout;

    private ChromeDriver driver;
    private Client client;
    private Setting setting;

    public App() {
        instance = this;
    }

    @Override
    public void start(Stage s) throws Exception {
        System.out.println("설정 불러오는 중");
        setting = new Setting(new File("Files/setting.txt"));
        System.out.println("설정 불러오기 완료");

        System.setProperty(setting.getProperty(), setting.getDriver().getAbsolutePath());

        client = new Client(this);

        this.primaryStage = s;
        s.setTitle("자가진단 Bot 1.0 - Made By K.S.Y.");
        initRootLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource("design.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client getClient() {
        return client;
    }

    public Setting getSetting() {
        return setting;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static App getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
