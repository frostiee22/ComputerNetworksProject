package Task;
import Questions.*;

import java.util.Scanner;


/**
 * Created by mitra on 11/9/15.
 */
public class Task {

    private int id;
    private int Score = 0;

    private SetUpGame Game;

    public void compute() {



        try{
            Game = new SetUpGame();
        }catch(Exception e){
            e.printStackTrace();
        }

        int numQuestions = Game.getQuestions_Queue().size();
        int rounds = 1;
        int points = 0;
        boolean quit = false;

        Scanner scanner = new Scanner(System.in);


        System.out.println("Select a Choice of A,B,C or D. Enter q to Quit the Game \n");

        while(Game.getQuestions_Queue().size() > 0 && !quit) {  //  run while there are items in the Queue

            Questions temp = Game.getQuestions_Queue().poll(); // pulls a question to be us// ed

            System.out.println((temp.toString())); // sends the question to the client

            String res = scanner.next(); // gets client response for the question

            // checks if a user wants to quit the program
            if (res.equalsIgnoreCase("q")) {
                System.out.println("points : " + points + "\n");
                setScore(points);
                quit = true;
            }else{
                // checking if the answer to the question is correct or wrong
                if (Game.Answer(temp, res)) {
                    System.out.println("Correct!!" + "\n");

                    // allocating points based on how the questions were answered
                    if (rounds <= numQuestions){
                        points += 10; // for getting it correct the first time
                    }else{
                        points += 5; // for getting it correct any other time
                    }
                    setScore(points);
                    rounds++; // amount of questions asked
                } else {
                    System.out.println("Wrong!!" + "\n");
                    // adding the question that was wrong back in the queue
                    Game.getQuestions_Queue().add(temp);
                    rounds++;
                }
            }

        }
        // checking if there are no more questions to send to the client
        if (Game.getQuestions_Queue().size() <= 0) {
            System.out.println("points :" + points +" NO MORE QUESTIONS\n");
            setScore(points);
        }else{
            System.out.println("Error!!"); // While and IF statement fail
        }



    }





//////////////////////////////////////////////////////

    // Setters and Getters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
}
