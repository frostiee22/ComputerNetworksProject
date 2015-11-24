package GUIwindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by shiva-dev on 18/11/2015.
 */
public class ConfirmBox {

    static boolean answer;

    public static boolean display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        //window.setMaxWidth(250);

        Label label = new Label();
        label.setText(message);

        Button yesButton = new Button("yes");
        yesButton.setId("rButton");
        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        Button noButton = new Button("no");
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        layout.setId("layout");
        HBox layout2 = new HBox(100);
        layout2.getChildren().addAll(yesButton, noButton);
        layout2.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, layout2);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        scene.getStylesheets().addAll(ConfirmBox.class.getResource("../TheStyles.css").toExternalForm());
        Image image = new Image("./resources/cursor/cursor_chalk.png");
        scene.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() /2));


        window.showAndWait();

        return answer;
    }
}
