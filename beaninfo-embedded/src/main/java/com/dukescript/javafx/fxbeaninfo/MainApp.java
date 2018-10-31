package com.dukescript.javafx.fxbeaninfo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.java.html.boot.fx.FXBrowsers;
import net.java.html.json.Models;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TabPane tabPane = new TabPane();
        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        tabPane.getTabs().add(new Tab("FXML", parent));

        WebView webview = new WebView();
        tabPane.getTabs().add(new Tab("HTML",webview));
        FXBrowsers.load(webview, MainApp.class.getResource("/html/view.html"), () -> {
            HTMLController ui = new HTMLController();
            Models.applyBindings(ui);
        });

        Scene scene = new Scene(tabPane);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("JavaFX and DukeScript");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
