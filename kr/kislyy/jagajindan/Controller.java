package kr.kislyy.jagajindan;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Button btn;
    @FXML private TextField address;
    @FXML private Button help;
    @FXML private CheckBox auto;
    @FXML private CheckBox hide;
    private App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        app = App.getInstance();
        auto.setSelected(app.getSetting().autoEnabled);
        hide.setSelected(app.getSetting().hide);
        address.setText(app.getSetting().address);

        btn.setOnAction(e -> {
            String a = address.getText();
            if(a == null || a.length() < 1 || !a.startsWith("http")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("페이지 주소 입력란에 자가진단 페이지의 주소를 입력해주세요");
                alert.showAndWait();
            } else {
                app.getSetting().address = a;
                app.getSetting().save();
                app.getClient().doMacro(a);
            }
        });

        hide.setOnAction(e->{
            app.getSetting().hide = hide.isSelected();
            app.getSetting().save();
            app.getClient().refresh();
        });

        auto.setOnAction(e-> {
            app.getSetting().autoEnabled = auto.isSelected();
            app.getSetting().save();
        });

        help.setOnAction(e->{
            try {
                java.awt.Desktop.getDesktop().browse(new URI("http://bot1.ksy.n-e.kr"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }
        });
    }
}
