package GUIwindows;

import Task.*;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

public class GUIupdate {

    public void update(Task temp, Label Q, Label A, Label B, Label C, Label D) {

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
                        updateMessage(temp.getAnswer_A());
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
                        updateMessage(temp.getAnswer_B());
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
                        updateMessage(temp.getAnswer_C());
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
                        updateMessage(temp.getAnswer_D());
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


        question.restart();
        Answer_A.restart();
        Answer_B.restart();
        Answer_C.restart();
        Answer_D.restart();
    }
}
