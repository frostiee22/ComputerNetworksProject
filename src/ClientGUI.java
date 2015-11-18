/**
 * Created by mitra on 11/10/15.
 */

import GUIwindows.GUIupdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;





//import application.AlertBox;
import Task.Task;
import javafx.application.Application;
import javafx.concurrent.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientGUI extends Application{
	
	Stage window;
	Scene scene, gameScene;
	
	int Qnum = 1;

	static Label ruleLabel;
	static Label question;
	static Label option1;
	static Label option2;
	static Label option3;
	static Label option4;
	static Label result;
	static Button buttonA, buttonB, buttonC, buttonD, quit;
	
	String ans;

    private Service<Void> backgroundThread1, backgroundThread2;
    private static Task temp = new Task();

    private static final int DEFAULT_PORT = 13572;
    private static final String CLOSE_CONNECTION_COMMAND = "close";
    private static final String SHUT_DOWN_COMMAND = "shutdown";
    private static final String TASK_COMMAND = "task";
    private static final String RESULTS_COMMAND = "results";

    private static boolean shutdownCommandReceived;

    private static String ip;
    
    public static void main(String[] args) {  	
    	
    	launch(args);
    	

    }
    
    @Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
    	
    	  	
    	window = primaryStage;
		window.setTitle("Client Application");
		
		        
        //// Scene 2
        GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);
        ruleLabel = new Label("Please select a choice: ");
		question = new Label("Ques: ?");
		
		option1 = new Label("A) ");
		//option1.textProperty().bind(new SimpleStringProperty("A)" + task.getAnswer_A()));
		option2 = new Label("B) ");
		//option2.textProperty().bind(new SimpleStringProperty("B)" + task.getAnswer_B()));
		option3 = new Label("C) ");
		//option3.textProperty().bind(new SimpleStringProperty("C)" + task.getAnswer_C()));
		option4 = new Label("D) ");
		//option4.textProperty().bind(new SimpleStringProperty("D)" + task.getAnswer_D()));

        buttonA = new Button("Select");
        buttonB = new Button("Select");
        buttonC = new Button("Select");
        buttonD = new Button("Select");


        Service<Void> buttons = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(500);
                       return null;
                    }
                };
            }
        };

        buttons.restart();


        buttonA.setOnAction(e -> {
            if (temp.getAnswer_A().equalsIgnoreCase(temp.getAnswer())){
                temp.setScore(10);
            }
        });

        buttonB.setOnAction(e -> {
            if (temp.getAnswer_B().equalsIgnoreCase(temp.getAnswer())){
                temp.setScore(10);
            }
        });

        buttonC.setOnAction(e -> {
            if (temp.getAnswer_C().equalsIgnoreCase(temp.getAnswer())){
                temp.setScore(10);
            }
        });

        buttonD.setOnAction(e -> {
            if (temp.getAnswer_D().equalsIgnoreCase(temp.getAnswer())){
                temp.setScore(10);
            }
        });


		
		
		result = new Label();
		quit = new Button("Quit");
		//quit.setOnAction(this);

		
		GridPane.setConstraints(ruleLabel, 0, 0);
		
		GridPane.setConstraints(question, 0, 1);
		GridPane.setConstraints(option1, 0, 3);
		GridPane.setConstraints(option2, 0, 4);
		GridPane.setConstraints(option3, 0, 5);
		GridPane.setConstraints(option4, 0, 6);
		
		GridPane.setConstraints(buttonA, 1, 3);
		GridPane.setConstraints(buttonB, 1, 4);
		GridPane.setConstraints(buttonC, 1, 5);
		GridPane.setConstraints(buttonD, 1, 6);
		GridPane.setConstraints(result, 1, 7);
		GridPane.setConstraints(quit, 0, 8);
		
		grid.getChildren().addAll(ruleLabel, question, option1, option2, option3, option4, buttonA, buttonB, buttonC, buttonD, result, quit);
			
		//grid.getChildren().addAll(ruleLabel, question, option1, buttonA);
		gameScene = new Scene(grid,400,400);
        ////
        
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

//        backgroundThread1.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                System.out.println("Done!  1");
//            }
//        });

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
            while(true){
                String line = in.readLine();
                if(line == null){
                    throw new Exception("Connection closed unexpectedly");
                }
                if(line.startsWith(CLOSE_CONNECTION_COMMAND)){
                    System.out.println("Received closed command.");
                    break;
                }
                if (line.startsWith(SHUT_DOWN_COMMAND)){
                    System.out.println("Received shutdown command.");
                    shutdownCommandReceived = true;
                    break;
                }
                else if (line.startsWith(TASK_COMMAND)){
                    Task task = readTask(line);
                    //setQuestion(task.getQuestion());
                    //task.compute();

                    temp = task;
                    GUIupdate gui = new GUIupdate();
                    gui.update(temp, question, option1, option2, option3, option4);

                    // used to pause for 5 seconds
                    test();

                    
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
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    


    

}
