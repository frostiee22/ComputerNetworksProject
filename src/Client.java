/**
 * Created by mitra on 11/10/15.
 */

import Task.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

   // public static final DBConnection DB = new DBConnection();


    private static final int DEFAULT_PORT = 13572;
    private static final String CLOSE_CONNECTION_COMMAND = "close";
    private static final String SHUT_DOWN_COMMAND = "shutdown";
    private static final String TASK_COMMAND = "task";
    private static final String RESULTS_COMMAND = "results";

    private static boolean shutdownCommandReceived;

    private static String ip;

    public static void main(String[] args) {

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


    private static Task readTask(String taskData) throws IOException {
        try {
            Scanner scanner = new Scanner(taskData);
            Task task = new Task();
            scanner.next();
            task.setId(scanner.nextInt());
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
                else if (line.startsWith(SHUT_DOWN_COMMAND)){
                    System.out.println("Received shutdown command.");
                    shutdownCommandReceived = true;
                    break;
                }
                else if (line.startsWith(TASK_COMMAND)){
                    Task task = readTask(line);
                    task.compute();
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

}
