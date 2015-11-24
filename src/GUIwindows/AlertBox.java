package GUIwindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static void display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        //window.setMaxWidth(250);

        Label label = new Label();
        label.setText(message);

        Button closeButton = new Button("close");
        closeButton.setId("rButton");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setId("layout");
        layout.getChildren().addAll(label,closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        scene.getStylesheets().addAll(ConfirmBox.class.getResource("../TheStyles.css").toExternalForm());
        Image image = new Image("./resources/cursor/cursor_chalk.png");
        scene.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() /2));
        window.showAndWait();
    }
}

