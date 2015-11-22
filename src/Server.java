import Questions.Questions;
import Questions.SetUpGame;
import Task.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends Application {

    Stage window;
    Scene scene, scene2;


    private static final int DEFAULT_PORT = 13572;
    private static String networkAddress;
    private static int NUM_JOBS;
    private static final String CLOSE_CONNECTION_COMMAND = "close";
    private static final String SHUT_DOWN_COMMAND = "shutdown";
    private static final String TASK_COMMAND = "task";
    private static final String RESULTS_COMMAND = "results";
    private static final String RESULT_PLACEMENT = "placement";
    private static int  taskCompleted;

    private static ConcurrentLinkedQueue<Task> tasks;

    private static int[] SCORES = new int[257];

    private static ArrayList<Integer> addresses = new ArrayList<Integer>();

    public static long startTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Server Application");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Ip label
        Label ipLabel = new Label("Ip Address: 192.168.2.");
        GridPane.setConstraints(ipLabel, 0, 0);


        // ip input
        TextField ipInput = new TextField();
        ipInput.setPromptText("192.168.1.");
        GridPane.setConstraints(ipInput, 0, 1);


        // start ip label
        Label startIPlabel = new Label("start ip");
        GridPane.setConstraints(startIPlabel, 1, 0);

        // ip input
        TextField start = new TextField();
        start.setPromptText("100");
        start.setMaxSize(50, 10);
        GridPane.setConstraints(start, 1, 1);

        // end ip label
        Label endIPlabel = new Label("end ip");
        GridPane.setConstraints(endIPlabel, 2, 0);

        // ip input
        TextField end = new TextField();
        end.setPromptText("110");
        end.setMaxSize(50, 10);
        GridPane.setConstraints(end, 2, 1);




        GridPane grid2 = new GridPane();
        grid2.setPadding(new Insets(10, 10, 10, 10));
        grid2.setVgap(8);
        grid2.setHgap(10);

        Button button2 = new Button("go back");
        button2.setOnAction(e -> window.setScene(scene));
        GridPane.setConstraints(button2, 0, 0);

        Label questiontext = new Label("Results");
        GridPane.setConstraints(questiontext, 0, 1);

        grid2.getChildren().addAll(button2,questiontext);
        scene2 = new Scene(grid2, 400, 200);


        // error messages
        Label error = new Label("Error messages");
        GridPane.setConstraints(error, 0, 5);

        //button
        Button startServer = new Button("Launch Server");
        GridPane.setConstraints(startServer, 0, 4 );
        startServer.setOnAction(e -> ServerRun(ipInput, start, end, questiontext,error));


        //button
        Button Results = new Button("View Results");
        GridPane.setConstraints(Results, 1, 4);
        Results.setOnAction(e -> window.setScene(scene2));

        grid.getChildren().addAll(ipLabel, ipInput, startIPlabel, start, endIPlabel, end, startServer,Results,error);

        scene = new Scene(grid, 400, 150);
        window.setScene(scene);

        window.show();

    }

    public static void ServerRun(TextField tf, TextField start, TextField end, Label label,Label error){
        // Application thread
        Service<Void> application = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(new Runnable(){
                            public void run(){
                                LaunchServer(tf, start, end, error);
                            }
                        });
                        return null;
                    }
                };

            }
        };



        // Results thread
        Service<Void> results = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while(true) {
                            updateMessage(PrintResults(label));
                            Thread.sleep(1000);
                        }
                    }
                };
            }
        };

        label.textProperty().bind(results.messageProperty());

        results.restart();
        application.restart();


    }

    public static boolean CheckString(String str) {
        int needed = 3, amt = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') {
                amt++;
            }
        }
        return needed == amt;
    }


    public static void LaunchServer(TextField tf, TextField start, TextField end, Label error) {
        // Results thread
        Service<Void> errorThread = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        ///////////////////////////////////////////////////////////
                        addresses.clear();
                        Arrays.fill(SCORES, 0);
                        networkAddress = tf.getText();
                        if (networkAddress.equals("") || !CheckString(networkAddress)) {
                            updateMessage("wrong ip");
                            return null;
                        } else {

                            startTime = System.currentTimeMillis();

                            createJob();

                            int startip, endip;
                            try {
                                startip = Integer.parseInt(start.getText());
                            }catch(NumberFormatException e){
                                startip = 1;
                                updateMessage("Incorrect Start address");
                                Thread.sleep(500);
                            }
                            try{
                                endip = Integer.parseInt(end.getText());
                            }catch(NumberFormatException e){
                                endip = 254;
                                updateMessage("Incorrect End address");
                                Thread.sleep(500);
                            }


                            int amt = endip;

                            WorkerConnection[] workers = new WorkerConnection[amt + 2];
                            int inc = 0;
                            for (int i = startip; i <= amt; i++) {
                                try {
                                    Socket socket = new Socket(networkAddress+i, DEFAULT_PORT);
                                    if (socket.isConnected()){
                                        System.out.println("connected to : " + networkAddress + i);
                                        workers[inc] = new WorkerConnection(inc + 1, i,socket);
                                        inc++;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not Connected to :" + networkAddress+i);
                                }
                            }

                            for (int i = 0; i < inc; i++) {
                                while (workers[i].isAlive()) {
                                    try {
                                        workers[i].join();
                                    } catch (InterruptedException e) {

                                    }
                                }
                            }
//                            if (taskCompleted != NUM_JOBS) {
//                                System.out.println("Something went wrong. Only " + taskCompleted);
//                                System.out.println("out of " + NUM_JOBS + " tasks were completed");
//                                System.exit(1);
//                            }

                            long elapsedTime = System.currentTimeMillis() - startTime;
                            System.out.println("Finish in " + (elapsedTime / 1000.0) + " seconds");

                            //PrintResults(label);


                            sendResults();




                        }

                        ////////////////////////////////////////////////////////////
                          return null;

                    }
                };
            }
        };

        error.textProperty().bind(errorThread.messageProperty());
        errorThread.restart();

    }

    private static String PrintResults(Label l) {
        Iterator<Integer> iter = addresses.iterator();
        String str = "";
        str += "=======> SCORES <=======\n";

        while(iter.hasNext()){
            Integer j = iter.next();
            str += (networkAddress + j + ") " + SCORES[j]) + "\n";
        }
        return str;
    }

    private static int loc(ArrayList numbers,int val){
        Collections.sort(numbers);
        Iterator<Integer> iter = numbers.iterator();
        int count = 1;
        while (iter.hasNext()){
            Integer I = iter.next();
            if (SCORES[I] == val){
                return count;
            }else{
                count++;
            }
        }
      return -1;
    }

    private static void sendResults(){
        System.out.println("Server sending results");
        Iterator<Integer> iter = addresses.iterator();
        while(iter.hasNext()){
            Integer j = iter.next();
            try {
                Socket socket = new Socket(networkAddress+j, DEFAULT_PORT);
                if (socket.isConnected()){
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(RESULT_PLACEMENT);
                        buffer.append(' ');
                        buffer.append(SCORES[j]);
                        buffer.append(' ');
                        buffer.append(loc(addresses,SCORES[j]));
                        buffer.append(' ');
                        out.println(buffer.toString());
                        out.flush();
                        out.println(CLOSE_CONNECTION_COMMAND);
                        out.flush();
                    }catch(Exception e){

                    }

                }
            } catch (Exception e) {
                System.out.println("Not Connected to :" + networkAddress+j);
            }
        }
    }


    private static void sendResults(Socket socket, int num){
        System.out.println("Server sending results");
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                StringBuffer buffer = new StringBuffer();
                buffer.append(RESULT_PLACEMENT);
                buffer.append(' ');
                buffer.append(SCORES[num]);
                buffer.append(' ');
                buffer.append(loc(addresses,SCORES[num]));
                buffer.append(' ');
                out.println(buffer.toString());
                out.flush();
            }catch(Exception e){

            }
    }


    private static void createJob() {
        SetUpGame Game;
        try {
            Game = new SetUpGame();
            tasks = new ConcurrentLinkedQueue<Task>();
            NUM_JOBS = Game.getQuestions_Queue().size();
            for (int i = 0; i < NUM_JOBS; i++) {
                Questions questions = Game.getQuestions_Queue().poll();
                Task task;
                task = new Task();
                task.setId(i);
                task.setQuestion(questions.getQuestion());
                task.setAnswer_A(questions.getAnswer_A());
                task.setAnswer_B(questions.getAnswer_B());
                task.setAnswer_C(questions.getAnswer_C());
                task.setAnswer_D(questions.getAnswer_D());
                task.setAnswer(questions.getAnswer());
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reassignTask(Task task) {
        tasks.add(task);
    }

    synchronized private static void finishTask(Task task) {
        taskCompleted++;
    }

    private static String convertTOoneString(String str) {
        return str.replaceAll(" ", "_");
    }


    private static String writeTask(Task task) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(TASK_COMMAND);
        buffer.append(' ');
        buffer.append(task.getId());
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getQuestion()));
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getAnswer_A()));
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getAnswer_B()));
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getAnswer_C()));
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getAnswer_D()));
        buffer.append(' ');
        buffer.append(convertTOoneString(task.getAnswer()));
        buffer.append(' ');
        return buffer.toString();
    }

    private static void readResults(String data, Task task) throws Exception {
        Scanner scanner = new Scanner(data);
        scanner.next();
        int id = scanner.nextInt();
        if (id != task.getId()) {
            throw new IOException("Wrong task ID in results returned by worker");
        }
        int Score = scanner.nextInt();
        task.setScore(Score);
        if (Score != task.getScore()) {
            throw new IOException("Wrong data in Score returned by worker");
        }
        scanner.close();
    }


    private static class WorkerConnection extends Thread {

        int id;
        int num;
        Socket socket;


        WorkerConnection(int id, int num, Socket socket ) {
            this.id = id;
            this.num = num;
            this.socket = socket;
            start();
        }

        public void run() {
            int taskCompleted = 0;
            //Socket socket;
            addresses.add(num);

            Task currentTask = null;
            Task nextTask = null;

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendResults(socket,num);

                currentTask = tasks.poll();
                if (currentTask != null) {
                    String taskString = writeTask(currentTask);
                    out.println(taskString);
                    out.flush();
                }
                while (currentTask != null) {
                    sendResults(socket,num);
                    String resultsString = in.readLine();
                    if (resultsString == null) {
                        throw new IOException("Connection closed unexpectedly.");
                    }
                    if (!resultsString.startsWith(RESULTS_COMMAND)) {
                        throw new IOException("Illegal string received from worker");
                    }

                    nextTask = tasks.poll();
                    if (nextTask != null) {
                        String taskString = writeTask(nextTask);
                        out.println(taskString);
                        out.flush();
                    }
                    readResults(resultsString, currentTask);
                    finishTask(currentTask);
                    taskCompleted++;
                    // saving the user scores
                    SCORES[num] += currentTask.getScore();

                    currentTask = nextTask;
                    nextTask = null;
                }
                out.println(CLOSE_CONNECTION_COMMAND);
                out.flush();

            } catch (Exception e) {
                System.out.println("Client " + networkAddress+num + " ending after completing " +
                        taskCompleted + " task");
                System.out.println("   Error: " + e);
                e.printStackTrace();

                // put uncompleted task, if any, back into the task list.
                if (currentTask != null) {
                    reassignTask(currentTask);
                }
                if (nextTask != null) {
                    reassignTask(nextTask);
                }
            } finally {
                System.out.println("Client " + networkAddress+num + " ending after completing " + taskCompleted + " tasks");
                try {
                    socket.close();
                } catch (Exception e) {

                }
            }
        }


    }


}
