package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

enum DirectionMessage {
    LEFT,
    RIGHT
}

public class SpeechBox extends HBox {

    private Color DEFAULT_SENDER_COLOR = Color.GRAY;
    private Color DEFAULT_RECEIVER_COLOR = Color.LIMEGREEN;
    private Background DEFAULT_SENDER_BACKGROUND, DEFAULT_RECEIVER_BACKGROUND;

    private String message;
    private String nick;
    private String date;
    private DirectionMessage direction;

    private Label displayedNick;
    private Label displayedDate;
    private Label displayedMessage;

    private SVGPath directionIndicator;

    public SpeechBox(String nick, String date, String message, DirectionMessage direction){
        this.nick = nick;
        this.date = date;
        this.message = message;
        this.direction = direction;
        initialiseDefaults();
        setupElements();
    }

    private void initialiseDefaults(){
        DEFAULT_SENDER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5,0,5,5,false), Insets.EMPTY));
        DEFAULT_RECEIVER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0,5,5,5,false), Insets.EMPTY));
    }

    private void setupElements(){
        displayedNick = new Label(nick);
        displayedDate = new Label(date);
        displayedMessage = new Label(message);
        displayedNick.setPadding(new Insets(5));
        displayedDate.setPadding(new Insets(5));
        displayedMessage.setPadding(new Insets(5));
        displayedNick.setWrapText(true);
        displayedDate.setWrapText(true);
        displayedMessage.setWrapText(true);
        directionIndicator = new SVGPath();

//        if(direction == DirectionMessage.LEFT){
//            configureForReceiver();
//        }
//        else{
//            configureForSender();
//        }
    }

//    private void configureForSender(){
//        displayedText.setBackground(DEFAULT_SENDER_BACKGROUND);
//        displayedText.setAlignment(Pos.CENTER_RIGHT);
//        directionIndicator.setContent("M10 0 L0 10 L0 0 Z");
//        directionIndicator.setFill(DEFAULT_SENDER_COLOR);
//
//        HBox container = new HBox(displayedText, directionIndicator);
//        //Use at most 75% of the width provided to the SpeechBox for displaying the message
//        container.maxWidthProperty().bind(widthProperty().multiply(0.75));
//        getChildren().setAll(container);
//        setAlignment(Pos.CENTER_RIGHT);
//    }
//
//    private void configureForReceiver(){
//        displayedText.setBackground(DEFAULT_RECEIVER_BACKGROUND);
//        displayedText.setAlignment(Pos.CENTER_LEFT);
//        directionIndicator.setContent("M0 0 L10 0 L10 10 Z");
//        directionIndicator.setFill(DEFAULT_RECEIVER_COLOR);
//
//        HBox container = new HBox(directionIndicator, displayedText);
//        //Use at most 75% of the width provided to the SpeechBox for displaying the message
//        container.maxWidthProperty().bind(widthProperty().multiply(0.75));
//        getChildren().setAll(container);
//        setAlignment(Pos.CENTER_LEFT);
//    }

}
