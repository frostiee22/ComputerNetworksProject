package Questions;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Mitra on 9/28/2015.
 */
public class SetUpGame {

    private Queue<Questions> Questions_Queue = new LinkedList<Questions>();

    public SetUpGame(String Topic) throws IOException {


        // Open the file
        FileInputStream fstream = null;
        try{
                fstream = new FileInputStream("Questions/"+Topic+".txt");
            }catch(FileNotFoundException fnf){
                System.out.println(fnf);
                // path used by ITELLIJ IDE
                fstream = new FileInputStream("src/Questions/"+Topic+".txt");
            }


        if (fstream != null){
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null){
                // creating question objects from data in the file
                Questions s = new Questions(strLine,br.readLine(),br.readLine(),br.readLine(),br.readLine(),br.readLine());
                Questions_Queue.add(s); // adding the new questions to the Queue
            }
            //Close the input stream
            br.close();
        }else {
            // Do nothing
        }
    }


//    // takes in the Question and the Answer which can be A,B,C or D
//    public boolean Answer(Questions Q,String Answer){
//
//        if (Answer.equalsIgnoreCase("A")){
//            return checkAnswer(Q,Q.getAnswer_A());
//        }
//        else  if (Answer.equalsIgnoreCase("B")){
//            return checkAnswer(Q,Q.getAnswer_B());
//        }
//        else  if (Answer.equalsIgnoreCase("C")){
//            return checkAnswer(Q,Q.getAnswer_C());
//        }
//        else if (Answer.equalsIgnoreCase("D")){
//            return checkAnswer(Q,Q.getAnswer_D());
//        }else {
//            return false;
//        }
//    }
//
//    // compares the answer for the question with the answer the user selects
//    public boolean checkAnswer(Questions Q, String Answer){
//        if (Q.getAnswer().equalsIgnoreCase(Answer)){
//            return true;
//        }else {
//            return false;
//        }
//    }




    public Queue<Questions> getQuestions_Queue() {
        return Questions_Queue;
    }
}
