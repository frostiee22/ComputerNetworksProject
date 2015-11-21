package Task;
import java.util.Scanner;


/**
 * Created by mitra on 11/9/15.
 */
public class Task {

    private int id;
    private int Score = 0;
    private String Question, answer_A, answer_B, answer_C, answer_D, Answer;

    public void compute() {


        Scanner scanner = new Scanner(System.in);


        System.out.println("Select a Choice of A,B,C or D.\n");

        System.out.println(toString());

        String res = scanner.next(); // gets client response for the question

        if(Answer(res)){
            Score = 10;
            System.out.println("Correct!");
        } else{
            Score = 0;
            System.out.println("Wrong!");
        }
    }

    public String toString(){
        String str = "";
        str += getQuestion() + "\n";
        str += "A)" + getAnswer_A() + "\n";
        str += "B)" + getAnswer_B() + "\n";
        str += "C)" + getAnswer_C() + "\n";
        str += "D)" + getAnswer_D() + "\n";
        return str;
    }

    // takes in the Question and the Answer which can be A,B,C or D
    public boolean Answer(String Answer){

        if (Answer.equalsIgnoreCase("A")){
            return checkAnswer(getAnswer_A());
        }
        else  if (Answer.equalsIgnoreCase("B")){
            return checkAnswer(getAnswer_B());
        }
        else if (Answer.equalsIgnoreCase("C")) {
            return checkAnswer(getAnswer_C());
        } else
            return Answer.equalsIgnoreCase("D") && checkAnswer(getAnswer_D());
    }

    public boolean checkAnswer(String str){
        return str.equalsIgnoreCase(getAnswer());
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

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer_A() {
        return answer_A;
    }

    public void setAnswer_A(String answer_A) {
        this.answer_A = answer_A;
    }

    public String getAnswer_B() {
        return answer_B;
    }

    public void setAnswer_B(String answer_B) {
        this.answer_B = answer_B;
    }

    public String getAnswer_C() {
        return answer_C;
    }

    public void setAnswer_C(String answer_C) {
        this.answer_C = answer_C;
    }

    public String getAnswer_D() {
        return answer_D;
    }

    public void setAnswer_D(String answer_D) {
        this.answer_D = answer_D;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

}
