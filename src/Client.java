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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application{

    Stage window;
    Scene scene, gameScene;

    int Qnum = 1;

    static String choice;
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
    static int time = 10;
    static Button buttonA, buttonB, buttonC, buttonD, quit;

    String ans;

    private Service<Void> backgroundThread1;
    private static Task temp = new Task();
    private static Countdown countdown = new Countdown();
    private static GUIupdate gui = new GUIupdate();


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
            Font.loadFont(getClass().getResource("./resources/font/ChalkDust.TTF").toExternalForm(), 10);

        window = primaryStage;
        window.setTitle("Client Application");


        //// Scene 2
        GridPane grid = new GridPane();
        grid.setId("pane");
        grid.setPadding(new Insets(50, 50, 50, 50));
        grid.setVgap(8);
        grid.setHgap(10);
        ruleLabel = new Label("Please select a choice: ");


        timeRemain = new Label("Time Remaining: " + time);

        question = new Label("Ques: ?");
        question.setId("ques");
        question.getStyleClass().add("question");

        option1 = new Label("A) ");
        option2 = new Label("B) ");
        option3 = new Label("C) ");
        option4 = new Label("D) ");

        buttonA = new Button("Select");
        buttonB = new Button("Select");
        buttonC = new Button("Select");
        buttonD = new Button("Select");



        option1.setOnMouseClicked(e -> {
            //if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();

                option1.setTextFill(Color.RED);
                option2.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_A();
                //temp.setScore(10);
            //}
        });


        option2.setOnMouseClicked(e -> {
            //if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                option2.setTextFill(Color.RED);
                option1.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_B();
                //temp.setScore(10);
                //countdown.interrupt();
            //}
        });


        option3.setOnMouseClicked(e -> {
            //if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                option3.setTextFill(Color.RED);
                option2.setTextFill(Color.WHITE);
                option1.setTextFill(Color.WHITE);
                option4.setTextFill(Color.WHITE);
                choice = temp.getAnswer_C();
                //temp.setScore(10);
                //countdown.interrupt();
            //}
        });


        option4.setOnMouseClicked(e -> {
            //if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                option4.setTextFill(Color.RED);
                option2.setTextFill(Color.WHITE);
                option3.setTextFill(Color.WHITE);
                option1.setTextFill(Color.WHITE);
                choice = temp.getAnswer_D();
                //temp.setScore(10);
                //countdown.interrupt();
            //}
        });



        buttonA.setOnAction(e -> {
            if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                temp.setScore(10);
                gui.setX(-1);
                //countdown.interrupt();
            }
        });

        buttonB.setOnAction(e -> {
            if (temp.getAnswer_B().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                temp.setScore(10);
            }
        });

        buttonC.setOnAction(e -> {
            if (temp.getAnswer_C().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                temp.setScore(10);
            }
        });

        buttonD.setOnAction(e -> {
            if (temp.getAnswer_D().equalsIgnoreCase(temp.getAnswer())){
                buttonClip.play();
                temp.setScore(10);
            }
        });



        result = new Label();
        quitLabel = new Label("Quit");
        quitLabel.setOnMouseClicked(e -> {
            if(ConfirmBox.display("Quit", "Are you sure you want to quit?")){
                AlertBox.display("Score", "Your final score is : " + FinalScore );
                window.close();
            }
        });

        nextQ = new Label();
        nextQ = new Label("Next Ques");
        nextQ.setOnMouseClicked(e -> {
            synchronized (temp) {
                temp.notify();
            }
        });


        GridPane.setConstraints(ruleLabel, 0, 0);
        GridPane.setConstraints(timeRemain, 3, 0);

        GridPane.setConstraints(question, 0, 1);
        GridPane.setConstraints(option1, 0, 3);
        GridPane.setConstraints(option2, 0, 4);
        GridPane.setConstraints(option3, 0, 5);
        GridPane.setConstraints(option4, 0, 6);

        //GridPane.setConstraints(buttonA, 1, 3);
        //GridPane.setConstraints(buttonB, 1, 4);
        //GridPane.setConstraints(buttonC, 1, 5);
        //GridPane.setConstraints(buttonD, 1, 6);
        //GridPane.setConstraints(result, 1, 7);
        GridPane.setConstraints(nextQ, 0, 8);
        GridPane.setConstraints(quitLabel, 0, 10);

        grid.getChildren().addAll(ruleLabel, timeRemain, question, option1, option2, option3, option4, result, nextQ, quitLabel);

        //grid.getChildren().addAll(ruleLabel, question, option1, buttonA);
        gameScene = new Scene(grid,800,600);
        gameScene.getStylesheets().addAll(this.getClass().getResource("./TheStyles.css").toExternalForm());


        window.setScene(gameScene);


        window.show();


        backgroundThread1 = new Service<Void>() {
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
                String line = in.readLine();

                if(line == null){
                    throw new Exception("Connection closed unexpectedly");
                }
                if(line.startsWith(CLOSE_CONNECTION_COMMAND)){
                    System.out.println("Received closed command.");
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
                    //setQuestion(task.getQuestion());
                    //task.compute();

                    temp = task;

                    gui = new GUIupdate();
                    gui.update(temp, question, option1, option2, option3, option4, timeRemain,FinalScore,Place);
                    //gui.updatetimer(x);
                    // used to pause for 5 seconds
                    //Thread.sleep(9999);

                    synchronized (temp) {

                        temp.wait();
                    }

                    if (choice.equalsIgnoreCase(temp.getAnswer())){
                        temp.setScore(10);
                    }
                    choice ="";

                    //test();
//                    countdown = new Countdown();
                    //countdown.start();

//                    synchronized (temp) {
//
//                        temp.wait();
//                    }

                    //test();


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
