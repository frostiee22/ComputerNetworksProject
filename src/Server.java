import Task.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by mitra on 11/9/15.
 */
public class Server {


    private static final int DEFAULT_PORT = 13572;
    private static final int NUM_JOBS = 2;
    private static final String CLOSE_CONNECTION_COMMAND = "close";
    private static final String SHUT_DOWN_COMMAND = "shutdown";
    private static final String TASK_COMMAND = "task";
    private static final String RESULTS_COMMAND = "results";

    private static ConcurrentLinkedQueue<Task> tasks;
    private static int taskCompleted;

    private static int[] SCORES = new int[NUM_JOBS];

    private static final String PASSWORD_TO_FIND = "aaaaa";

    public static long startTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();

        createJob();
        int amt = 255;
        if (args.length == 0) {


            WorkerConnection[] workers = new WorkerConnection[amt+2];

            String localnetIPS = "192.168.43.";
            for (int i=0;i<=amt;i++){
                workers[i] = new WorkerConnection(i+1,localnetIPS+i , DEFAULT_PORT);
            }

            //workers[amt+1] = new WorkerConnection(amt+2,"localhost" , DEFAULT_PORT);

            for (int i = 0; i <= amt; i++) {
                while (workers[i].isAlive()) {
                    try {
                        workers[i].join();
                    } catch (InterruptedException e) {

                    }
                }
            }

            if (taskCompleted != NUM_JOBS) {
                System.out.println("Something went wrong. Only " + taskCompleted);
                System.out.println("out of " + NUM_JOBS + " tasks were completed");
                System.exit(1);
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Finish in " + (elapsedTime / 1000.0) + " seconds");
        PrintResults();


    }

    private static void PrintResults(){
        for (int i=0;i<NUM_JOBS;i++){
            System.out.println(i +")"+SCORES[i]);
        }
    }


    private static void createJob() {

        tasks = new ConcurrentLinkedQueue<Task>();
        for (int i = 0; i < NUM_JOBS; i++) {
            Task task;
            task = new Task();
            task.setId(i);
            tasks.add(task);
        }
    }

    private static void reassignTask(Task task) {
        tasks.add(task);
    }

    synchronized private static void finishTask(Task task) {
        //System.out.println("Score obtain is : " + task.getScore());
        SCORES[task.getId()] = task.getScore();
        taskCompleted++;
    }


    private static String writeTask(Task task) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(TASK_COMMAND);
        buffer.append(' ');
        buffer.append(task.getId());
        buffer.append(' ');
        return buffer.toString();
    }

    private static void readResults(String data, Task task) throws Exception {
        Scanner scanner = new Scanner(data);
        scanner.next();
        int id = scanner.nextInt();
        if (id != task.getId()) {
            throw new IOException("Wrong task ID in results returend by worker");
        }
        int Score = scanner.nextInt();
        System.out.println("received score : "+ Score);
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


        WorkerConnection(int id, String host, int port) {
            this.id = id;
            this.host = host;
            this.port = port;
            start();
        }

        public void run() {
            int taskCompleted = 0;
            Socket socket;

            try {
                socket = new Socket(host, port);
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