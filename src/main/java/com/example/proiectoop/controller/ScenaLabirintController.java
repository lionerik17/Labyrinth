package com.example.proiectoop.controller;

import com.example.proiectoop.model.Maze;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/***
 * Color.WHITE = Celula libera
 * Color.BLACK = Perete
 * Color.RED = Punct de start
 * Color.GREEN = Punct de finish
 * Color.BLUE = Celula vizitata
 */

public class ScenaLabirintController {
    @FXML
    private Pane pane;

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
}