package com.example.proiectoop.controller;

import com.example.proiectoop.model.Maze;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/***
 * Color.WHITE = Celula libera
 * Color.BLACK = Perete
 * Color.BEIGE = Margine
 * Color.GREEN = Punct de start/finish
 * Color.BLUE = Celula vizitata
 */

public class ScenaLabirintController {
    @FXML
    private Pane pane;
    @FXML
    private Button generateMazeButton;

    @FXML
    public void initialize()
    {
        for(int i = 0; i < 480; i += 40)
        {
            for(int j = 0; j < 480; j += 40)
            {
                Rectangle rectangle = new Rectangle(i, j, 40, 40);
                rectangle.setFill(Color.WHITE);
                rectangle.setStroke(Color.BLACK);
                pane.getChildren().add(rectangle);
            }
        }
    }

    public void onGenerateMazeButton()
    {
        Maze maze = new Maze(10);
        maze.generate();
        char[][] borderedMaze = maze.getBorderedMaze();
        for(int i = 0; i < 480; i += 40)
        {
            for(int j = 0; j < 480; j += 40)
            {
                Rectangle rectangle = new Rectangle(i, j, 40, 40);
                int mazeRow = i / 40;
                int mazeCol = j / 40;

                switch(borderedMaze[mazeRow][mazeCol])
                {
                    case '+':
                        rectangle.setFill(Color.PINK);
                        break;
                    case ' ':
                        rectangle.setFill(Color.WHITE);
                        break;
                    case 'O':
                        rectangle.setFill(Color.LIME);
                        break;
                    case 'W':
                        rectangle.setFill(Color.BLACK);
                        break;
                }

                rectangle.setStroke(Color.BLACK);
                pane.getChildren().add(rectangle);
            }
        }
    }
}