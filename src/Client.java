/**
 * Created by mitra on 11/10/15.
 */

import GUIwindows.AlertBox;
import GUIwindows.ConfirmBox;
import GUIwindows.GUIupdate;
import Task.Task;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application{

    Stage window;
    Scene welcomeScene, gameScene;


    static String clchange;
    static String choice;
    static Label nameLabel;
    static Label ruleLabel;
    static Label question;
    static Label option1;
    static Label option2;
    static Label option3;
    static Label option4;
    static Label result;
    static Label timeRemain;
    static Label quitLabel;
    static Label nextQ;

    private static Task temp = new Task();
    //private static Countdown countdown = new Countdown();


    private static final int DEFAULT_PORT = 13572;
    private static final String CLOSE_CONNECTION_COMMAND = "close";
    private static final String SHUT_DOWN_COMMAND = "shutdown";
    private static final String TASK_COMMAND = "task";
    private static final String RESULTS_COMMAND = "results";
    private static final String RESULT_PLACEMENT = "placement";

    private static int FinalScore = 0;
    private static int Place;

    private static boolean shutdownCommandReceived;

    private static boolean popUp = false;

    private static String ip;


    public static void main(String[] args) {

        launch(args);


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub


            AudioClip backgroundClip = new AudioClip(getClass().getResource("./resources/sound/click.wav").toExternalForm());
            AudioClip buttonClip = new AudioClip(getClass().getResource("./resources/sound/click.wav").toExternalForm());
            AudioClip quitClip = new AudioClip(getClass().getResource("./resources/sound/quit.wav").toExternalForm());
            AudioClip noClip = new AudioClip(getClass().getResource("./resources/sound/positive.wav").toExternalForm());
            AudioClip yesClip = new AudioClip(getClass().getResource("./resources/sound/negative.wav").toExternalForm());
            AudioClip exitClip = new AudioClip(getClass().getResource("./resources/sound/negative_2.wav").toExternalForm());
            AudioClip nameClip = new AudioClip(getClass().getResource("./resources/sound/save.wav").toExternalForm());


            Font.loadFont(getClass().getResource("./resources/font/ChalkDust.ttf").toExternalForm(), 10);



        window = primaryStage;
        window.setTitle("Client Application");

        /// scene 1
        GridPane g = new GridPane();
        g.setId("pane");
        g.setAlignment(Pos.CENTER);
        g.setVgap(18);
        g.setHgap(15);

        nameLabel = new Label("NAME: ");
        Label label = new Label("NAME: ");
        Button valName = new Button("ENTER");
        TextField nameInput = new TextField();
        nameInput.setId("nameInput");
        nameInput.setOnKeyPressed(e -> {
            buttonClip.play();
            if(e.getCode().equals(KeyCode.ENTER)){
                nameLabel.setText("Player: " + nameInput.getText());
                nameClip.play();
                window.setScene(gameScene);
            }
        });
        valName.setOnAction(e -> {
            nameLabel.setText("Player: " + nameInput.getText());
            nameClip.play();
            window.setScene(gameScene);
        });

        GridPane.setConstraints(label, 0, 0);
        GridPane.setConstraints(nameInput, 1, 0);
        GridPane.setConstraints(valName, 1, 1);
        g.getChildren().addAll(label, nameInput, valName);
        welcomeScene = new Scene(g,800,600);
        welcomeScene.getStylesheets().addAll(this.getClass().getResource("./TheStyles.css").toExternalForm());

        Image image = new Image("./resources/cursor/cursor_chalk.png");

        welcomeScene.setCursor(new ImageCursor(image));


        /// Scene 1


        //// Scene 2
        GridPane grid = new GridPane();
        grid.setId("pane");
        grid.setPadding(new Insets(50, 50, 50, 50));
        grid.setVgap(8);
        grid.setHgap(10);

        ruleLabel = new Label("Please select a choice: ");
        timeRemain = new Label("");

        question = new Label("Please Wait for the server to start the game...");
        question.setId("ques");
        question.getStyleClass().add("question");

        option1 = new Label("A) ");
        option2 = new Label("B) ");
        option3 = new Label("C) ");
        option4 = new Label("D) ");

        option1.setOnMouseClicked(e -> {
                buttonClip.play();
                option1.setTextFill(Color.YELLOW);
                option2.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_A();
        });

        option2.setOnMouseClicked(e -> {
                buttonClip.play();
                option2.setTextFill(Color.YELLOW);
                option1.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_B();
        });

        option3.setOnMouseClicked(e -> {
                buttonClip.play();
                option3.setTextFill(Color.YELLOW);
                option2.setTextFill(Color.WHITE);
                option1.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_C();
        });

        option4.setOnMouseClicked(e -> {
                buttonClip.play();
                option4.setTextFill(Color.YELLOW);
                option2.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option1.setTextFill(Color.WHITE);
                choice = temp.getAnswer_D();
        });


        result = new Label();
        quitLabel = new Label("Quit");
        quitLabel.setOnMouseClicked(e -> {
            quitClip.play();
            if(ConfirmBox.display("Quit", "Are you sure you want to quit?")){
                yesClip.play();
                AlertBox.display("Score", "Your final score is : " + FinalScore + "\nYou did not place in the game!");
                exitClip.play();
                window.close();
            }
            noClip.play();
        });

        nextQ = new Label("Next Ques");
        nextQ.setOnMouseClicked(e -> {
            buttonClip.play();
            synchronized (temp) {
                temp.notify();
            }
        });

        quitLabel.setId("rLabel");
        nextQ.setId("bLabel");

        GridPane.setConstraints(nameLabel, 0, 0);
        GridPane.setConstraints(timeRemain, 0, 1);
        GridPane.setConstraints(ruleLabel, 0, 6);
        GridPane.setConstraints(question, 0, 9);
        GridPane.setConstraints(option1, 0, 11);
        GridPane.setConstraints(option2, 0, 13);
        GridPane.setConstraints(option3, 0, 15);
        GridPane.setConstraints(option4, 0, 17);
        GridPane.setConstraints(nextQ, 0, 20);
        GridPane.setConstraints(quitLabel, 0, 30);


        grid.getChildren().addAll(ruleLabel, timeRemain, question, option1, option2, option3, option4, result, nextQ, quitLabel, nameLabel);
        gameScene = new Scene(grid,800,600);
        gameScene.getStylesheets().addAll(this.getClass().getResource("./TheStyles.css").toExternalForm());
        gameScene.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() /2));


        /// scene 2

        window.setScene(welcomeScene);
        window.show();


        Service<Void> backgroundThread1 = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        LaunchClient();
                        return null;
                    }
                };
            }
        };
        backgroundThread1.restart();

    }




    public static void LaunchClient() {


        int port = DEFAULT_PORT;


        System.out.println("Starting with listening port number " + port);

        while (!shutdownCommandReceived) {

            ServerSocket listener = null;
            try {
                listener = new ServerSocket(port);
            } catch (Exception e) {
                System.out.println("ERROR: cannot create listening socket on  port " + port);
                System.exit(1);
            }

            try {

                Socket connection = listener.accept();
                listener.close();
                System.out.println("Accepted connection from " + connection.getInetAddress());
                handleConnection(connection);
            } catch (Exception e) {
                System.out.println("ERROR Server shut down with error:");
                System.out.println(e);
                System.exit(2);
            }
        }
        System.out.println("Shutting down normally");


    }

    private static String RemoveUnderscore(String str){
        return str.replaceAll("_"," ");
    }


    private static void placement(String taskData) throws IOException {
        try {
            Scanner scanner = new Scanner(taskData);
            scanner.next();
            FinalScore = scanner.nextInt();
            Place = scanner.nextInt();
        } catch (Exception e) {
            throw new IOException("Illegal data found while reading task information.");
        }
    }


    private static Task readTask(String taskData) throws IOException {
        try {
            Scanner scanner = new Scanner(taskData);
            Task task = new Task();
            scanner.next();
            task.setId(scanner.nextInt());
            task.setQuestion(RemoveUnderscore(scanner.next()));
            task.setAnswer_A(RemoveUnderscore(scanner.next()));
            task.setAnswer_B(RemoveUnderscore(scanner.next()));
            task.setAnswer_C(RemoveUnderscore(scanner.next()));
            task.setAnswer_D(RemoveUnderscore(scanner.next()));
            task.setAnswer(RemoveUnderscore(scanner.next()));
            return task;
        } catch (Exception e) {
            throw new IOException("Illegal data found while reading task information.");
        }
    }

    private static String writeResults(Task task) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RESULTS_COMMAND);
        buffer.append(' ');
        buffer.append(task.getId());
        buffer.append(' ');
        buffer.append(task.getScore());
        buffer.append(' ');
        return buffer.toString();
    }


    private static void handleConnection(Socket connection){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            int x = 1;

            String ScoreData = in.readLine();

            if (ScoreData.startsWith(RESULT_PLACEMENT)){
                placement(ScoreData);
                System.out.println("Score :"+ FinalScore +"|| Place :"+ Place);
            }

            while(true){
                GUIupdate gui = new GUIupdate();
                String line = in.readLine();

                if(line == null){
                    throw new Exception("Connection closed unexpectedly");
                }
                if(line.startsWith(CLOSE_CONNECTION_COMMAND)){
                    System.out.println("Received closed command.");

                    gui.update(temp, question, option1, option2, option3, option4, timeRemain,FinalScore,Place,"Game End: ");
                    popUp = true;
                    break;
                }
                if (line.startsWith(SHUT_DOWN_COMMAND)){
                    System.out.println("Received shutdown command.");
                    shutdownCommandReceived = true;
                    break;
                }
                else if(line.startsWith(RESULT_PLACEMENT)){
                    placement(line);
                    System.out.println("Score :"+ FinalScore +"|| Place :"+ Place);
                }
                else if (line.startsWith(TASK_COMMAND)){

                    ScoreData = in.readLine();

                    if (ScoreData.startsWith(RESULT_PLACEMENT)){
                        placement(ScoreData);
                        System.out.println("Score :"+ FinalScore +"|| Place :"+ Place);
                    }

                    Task task = readTask(line);

                    temp = task;

//                    GUIupdate gui = new GUIupdate();
                    gui.update(temp, question, option1, option2, option3, option4, timeRemain,FinalScore,Place,"");
                    choice ="";
                    option1.setTextFill(Color.WHITE);
                    option2.setTextFill(Color.WHITE);
                    option3.setTextFill(Color.WHITE);
                    option4.setTextFill(Color.WHITE);

                    synchronized (temp) {

                        temp.wait();
                    }

                    if (choice.equalsIgnoreCase(temp.getAnswer())){
                        temp.setScore(10);
                    }

                    out.println(writeResults(task));
                    out.flush();

                }
                else{
                    throw new Exception("Illegal command received.");
                }
            }

        }catch (Exception e){
            System.out.println("Client connection closed error " + e);
        }
        finally {
            try{
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public static void test(){
        try {
            int i = 10;
            while(i>0){
                //timeRemain.setText(Integer.toString(i));
                //timeRemain.textProperty().bind(Integer.toString(i).messageProperty());
                //gui.updatetimer(x);
                Thread.sleep(1000);
                i--;
            }

            synchronized (temp) {
                temp.notify();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class Countdown extends Thread {
        public void run() {
            test();
        }
    }








}
