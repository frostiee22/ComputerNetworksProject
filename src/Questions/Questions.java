package Questions;


public class Questions{

    private String Question;
    private String answer_A, answer_B, answer_C, answer_D, Answer;

    // Creation of a Question and its 4 possible answers
    public Questions(String Question,String A,String B, String C, String D, String Answer){
        setQuestion(Question);
        setAnswer_A(A);
        setAnswer_B(B);
        setAnswer_C(C);
        setAnswer_D(D);
        setAnswer(Answer);
    }

    @Override
    public String toString(){
        String str = "";
        str += getQuestion() + "\n";
        str += "A)" + getAnswer_A() + "\n";
        str += "B)" + getAnswer_B() + "\n";
        str += "C)" + getAnswer_C() + "\n";
        str += "D)" + getAnswer_D() + "\n";
        return str;
    }


    //    setters and getters

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
