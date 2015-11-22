package GUIwindows;

import Task.Task;
import javafx.concurrent.Service;
import javafx.scene.control.Label;

public class GUIupdate {

    private static int x;
    private static boolean quesSel = false;


    public void updateTime(Task temp, Label T) {
        // Time thread
        Service<Void> Time2 = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage(Integer.toString(x));
                        //T.textProperty().bind(new SimpleStringProperty(Integer.toString(x)));
                        return null;
                    }
                };
            }
        };

        T.textProperty().bind(Time2.messageProperty());
        Time2.restart();
    }


    public void update(Task temp, Label Q, Label A, Label B, Label C, Label D, Label T, int FinalScore,int Place) {

        x=10;

        final int s = FinalScore;
        final int p = Place;

        // Question thread
        Service<Void> question = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage(temp.getQuestion());
                        return null;
                    }
                };
            }
        };



        // Answer A
        Service<Void> Answer_A = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage("A) " + temp.getAnswer_A());
                        return null;
                    }
                };
            }
        };


        // Answer B
        Service<Void> Answer_B = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage("B) " + temp.getAnswer_B());
                        return null;
                    }
                };
            }
        };

        // Answer C
        Service<Void> Answer_C = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage("C) " + temp.getAnswer_C());
                        return null;
                    }
                };
            }
        };


        // Answer D
        Service<Void> Answer_D = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        updateMessage("D) " + temp.getAnswer_D());
                        return null;

                    }
                };
            }
        };

        // Label T
        Service<Void> Time = new Service<Void>() {
            @Override
            protected javafx.concurrent.Task<Void> createTask() {
                return new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                       // private static int x = 10;
                        int y = 10;
                        while (y > 0){
                            Thread.sleep(1000);
                            updateMessage("Score:"+s+" Place:"+p+" time:"+y);
                            y--;
                        }
                        if (!quesSel){
                            synchronized (temp) {
                                temp.notify();
                            }
                        }

                        return null;
                    }
                };
            }
        };




        Q.textProperty().bind(question.messageProperty());
        A.textProperty().bind(Answer_A.messageProperty());
        B.textProperty().bind(Answer_B.messageProperty());
        C.textProperty().bind(Answer_C.messageProperty());
        D.textProperty().bind(Answer_D.messageProperty());
        T.textProperty().bind(Time.messageProperty());



        question.restart();
        Answer_A.restart();
        Answer_B.restart();
        Answer_C.restart();
        Answer_D.restart();
        Time.restart();

    }

    public void setX(int num) {
        this.x = num;
    }
    public int getX() {
        return this.x;
    }


}
