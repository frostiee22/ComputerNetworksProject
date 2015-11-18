import GUIwindows.AlertBox;
import Questions.Questions;
import Questions.SetUpGame;
import Task.Task;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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

    private static ConcurrentLinkedQueue<Task> tasks;
    private static int taskCompleted = 0;

    private static int[] SCORES = new int[257];
    //private static Queue<Integer> addresses = new LinkedList<Integer>();
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
        questiontext.textProperty().bind(new SimpleStringProperty());

        grid2.getChildren().addAll(button2,questiontext);
        scene2 = new Scene(grid2, 600, 300);



        //button
        Button startServer = new Button("Launch Server");
        GridPane.setConstraints(startServer, 0, 4 );
        startServer.setOnAction(e -> ServerRun(ipInput, start, end,window, scene2, questiontext));


        //button
        Button Results = new Button("View Results");
        GridPane.setConstraints(Results, 1, 4);
        Results.setOnAction(e -> window.setScene(scene2));



        grid.getChildren().addAll(ipLabel, ipInput, startIPlabel, start, endIPlabel, end, startServer,Results);

        scene = new Scene(grid, 400, 150);
        window.setScene(scene);

        window.show();

    }

    public static void ServerRun(TextField tf, TextField start, TextField end,Stage window,Scene scene, Label label){
        window.setScene(scene);
        // Application thread
        Service<Void> application = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        LaunchServer(tf, start, end, label);
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
        if (needed == amt) {
            return true;
        } else {
            return false;
        }
    }


    public static void LaunchServer(TextField tf, TextField start, TextField end, Label label) {
        networkAddress = tf.getText();
        if (networkAddress.equals("") || !CheckString(networkAddress)) {
            AlertBox.display("Error!!", "Wrong IP Address");
        } else {

            startTime = System.currentTimeMillis();

            createJob();

            int startip, endip;
            try {
                startip = Integer.parseInt(start.getText());
                endip = Integer.parseInt(end.getText());
            }catch(NumberFormatException e){
                startip = 1;
                endip = 254;
                AlertBox.display("Error!!", "not a valid number\n using default start and end");
            }


            int amt = endip;

            WorkerConnection[] workers = new WorkerConnection[amt + 2];
            int inc = 0;
            String localnetIPS = networkAddress;
            for (int i = startip; i <= amt; i++) {
                boolean inet = false;
//                try {
                    System.out.println(networkAddress + i);
                    //inet = InetAddress.getByName(networkAddress + i).isReachable(15000);
                    //if (inet == true) {
                        workers[inc] = new WorkerConnection(inc + 1, localnetIPS + i, DEFAULT_PORT, i);
                        inc++;

                    //}
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            for (int i = 0; i < inc; i++) {
                while (workers[i].isAlive()) {
                    try {

                        workers[i].join();
                    } catch (InterruptedException e) {

                    }
                }
            }


//            if (taskCompleted != NUM_JOBS) {
//                System.out.println("Something went wrong. Only " + taskCompleted);
//                System.out.println("out of " + NUM_JOBS + " tasks were completed");
//                System.exit(1);
//            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Finish in " + (elapsedTime / 1000.0) + " seconds");

            //PrintResults(label);



        }

    }

    private static String PrintResults(Label l) {
        int size = addresses.size();
        Iterator<Integer> iter = addresses.iterator();
        String str = "";
        str += "=======> SCORES <=======\n";

        while(iter.hasNext()){
            Integer j = iter.next();
            str += (networkAddress + j.intValue() + ") " + SCORES[j.intValue()]) + "\n";
        }



//        for (int i = 0; i < size; i++) {
//            int j = addresses.poll();
//            str += (networkAddress + j + ") " + SCORES[j]) + "\n";
//        }
        //l.textProperty().bind(new SimpleStringProperty(str));
        //AlertBox.display("Results", str);
        return str;

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
        //System.out.println("Score obtain is : " + task.getScore());
        //SCORES[task.getId()] += task.getScore();
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
        String host;
        int port;
        int num;
        Label l;


        WorkerConnection(int id, String host, int port, int num) {
            this.id = id;
            this.host = host;
            this.port = port;
            this.num = num;
            start();
        }

        public void run() {
            int taskCompleted = 0;
            Socket socket;

            try {
                socket = new Socket(host, port);
                addresses.add(num);
            } catch (Exception e) {
//                System.out.println("Thread " + id + " could not open connection to " +
//                        host + ":" + port);
//                System.out.println("   Error: " + e);
                return;
            }

            Task currentTask = null;
            Task nextTask = null;

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                currentTask = tasks.poll();
                if (currentTask != null) {
                    String taskString = writeTask(currentTask);
                    out.println(taskString);
                    out.flush();
                }
                while (currentTask != null) {
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
                System.out.println("Thread " + id + " ending after completing " +
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
                System.out.println("Thread " + id + " ending after completing " + taskCompleted + " tasks");
                try {
                    socket.close();
                } catch (Exception e) {

                }
            }
        }


    }


}
