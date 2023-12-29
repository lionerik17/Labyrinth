package com.example.proiectoop.controller;

import com.example.proiectoop.model.Maze;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class ScenaLabirintController {
    @FXML
    private Pane pane;
    @FXML
    private Button generateMazeButton;
    @FXML
    private Button solveMazeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Label currentPositionLabel;

    private boolean game;
    private int row;
    private int col;
    private char[][] initialMaze;
    private char[][] solvedMaze;

    @FXML
    public void initialize()
    {
        game = false;
        row = col = 0;

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

        currentPositionLabel.setText("");
    }

    /***
     * Genereaza labirintul initial si cel rezolvat, afiseaza labirintul initial
     */

    public void onGenerateMazeButton()
    {
        pane.getChildren().clear();
        pane.getChildren().add(generateMazeButton);
        pane.getChildren().add(solveMazeButton);
        pane.getChildren().add(menuButton);
        pane.getChildren().add(currentPositionLabel);

        game = true;
        row = col = 40; // pozitia de start

        Maze maze = new Maze(10);

        maze.generate();
        initialMaze = maze.getBorderedMaze();

        for(int i = 0; i < 480; i += 40)
        {
            for(int j = 0; j < 480; j += 40)
            {
                Rectangle rectangle = setRectangle(i, j, initialMaze);
                pane.getChildren().add(rectangle);
            }
        }

        maze.solve();
        solvedMaze = maze.getBorderedMaze();

        currentPositionLabel.setText("Pozitie actuala (1, 1)");
    }

    /***
     * Listener la WASD
     * @param event keyboard event
     */

    public void onKeyPressed(KeyEvent event)
    {
        if(!game)
        {
            return;
        }

        KeyCode keyCode = event.getCode();
        String keyCodeString = keyCode.toString();

        switch(keyCodeString)
        {
            case "W":
                if(eligibleForMovement())
                {
                    if(col - 40 < 40)
                    {
                        break;
                    }

                    if(eligibleForColoring(row, col - 40))
                    {
                        colorCellAsWhite(row, col);
                        col -= 40;
                        colorCellAsBlue(row, col);
                    }

                    break;
                }

            case "S":
                if(eligibleForMovement())
                {
                    if(col + 40 > 400)
                    {
                        break;
                    }

                    if(eligibleForColoring(row, col + 40))
                    {
                        colorCellAsWhite(row, col);
                        col += 40;
                        colorCellAsBlue(row, col);
                    }

                    break;
                }

            case "A":
                if(eligibleForMovement())
                {
                    if(row - 40 < 40)
                    {
                        break;
                    }

                    if(eligibleForColoring(row - 40, col))
                    {
                        colorCellAsWhite(row, col);
                        row -= 40;
                        colorCellAsBlue(row, col);
                    }

                    break;
                }

            case "D":
                if(eligibleForMovement())
                {
                    if(row + 40 > 400)
                    {
                        break;
                    }

                    if(eligibleForColoring(row + 40, col))
                    {
                        colorCellAsWhite(row, col);
                        row += 40;
                        colorCellAsBlue(row, col);
                    }

                    break;
                }
        }

        if(row / 40 == 10 && col / 40 == 10)
        {
            currentPositionLabel.setText("Ai scapat din labirint!");
            return;
        }

        currentPositionLabel.setText("Pozitie actuala: (" + row / 40 + ", " + col / 40 + ")");
    }

    /***
     * Afiseaza labirintul rezolvat
     */

    public void onSolveMazeButton()
    {
        if(!game)
        {
            return;
        }

        pane.getChildren().clear();
        pane.getChildren().add(generateMazeButton);
        pane.getChildren().add(solveMazeButton);
        pane.getChildren().add(menuButton);
        pane.getChildren().add(currentPositionLabel);

        game = false;
        row = col = 400;

        for(int i = 0; i < 480; i += 40)
        {
            for(int j = 0; j < 480; j += 40)
            {
                Rectangle rectangle = setRectangle(i, j, solvedMaze);
                pane.getChildren().add(rectangle);

                // highlight the path
                Color color = getColorAtPosition(i, j);
                if(color != null)
                {
                    int r = (int) (color.getRed() * 255);
                    int g = (int) (color.getGreen() * 255);
                    int b = (int) (color.getBlue() * 255);

                    if(isVisitedCell(r, g, b))
                    {
                        addDotInCenter(rectangle);
                    }
                }
            }
        }

        currentPositionLabel.setText("Ai scapat din labirint!");
    }

    /***
     * Inapoi la meniu
     */

    public void onMenuButton()
    {
        initialize();
    }

    /***
     * Coloreaza o celula din labirint
     * @param i rand
     * @param j coloana
     * @param maze labirint
     * @return o celula colorata
     */

    private Rectangle setRectangle(int i, int j, char[][] maze)
    {
        Rectangle rectangle = new Rectangle(i, j, 40, 40);
        // pozitia in matrice
        int mazeRow = i / 40;
        int mazeCol = j / 40;

        switch(maze[mazeRow][mazeCol])
        {
            case '+': // margine
                rectangle.setFill(Color.PINK);
                break;
            case ' ': // celula libera
                rectangle.setFill(Color.WHITE);
                break;
            case 'O': // punct de start/finish
                rectangle.setFill(Color.LIME);
                break;
            case 'W': // perete
                rectangle.setFill(Color.BLACK);
                break;
            case 'P': // celula din rezolvare
                rectangle.setFill(Color.BLUE);
                break;
        }

        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }

    /***
     * Verifica daca o celula poate fi coloara
     * @param row rand
     * @param col coloana
     * @return true/false
     */

    private boolean eligibleForColoring(int row, int col)
    {
        Color colorAtPosition = getColorAtPosition(row, col);
        Rectangle rectangleAtPosition = getRectangleAtPosition(row, col);
        if(colorAtPosition == null || rectangleAtPosition == null)
        {
            return false;
        }

        int r = (int) (colorAtPosition.getRed() * 255);
        int g = (int) (colorAtPosition.getGreen() * 255);
        int b = (int) (colorAtPosition.getBlue() * 255);

        if(isWall(r, g, b) || isBorder(r, g, b))
        {
            return false;
        }

        return true;
    }

    /***
     * Verifica daca se poate deplasa in labirint
     * @return true/false
     */

    private boolean eligibleForMovement()
    {
        return row >= 40 && row <= 400 && col >= 40 && col <= 400;
    }

    /***
     * Coloreaza o celula ca fiind vizitata (Color.WHITE) si trateaza cazul de finish
     * Impreuna cu metoda colorCellAsWhite, da impresia de animate
     * @param row rand
     * @param col coloana
     */

    private void colorCellAsBlue(int row, int col)
    {
        Rectangle rectangle = getRectangleAtPosition(row, col);
        if(rectangle == null || isStart())
        {
            return;
        }

        if(isFinish())
        {
            game = false;
            return;
        }

        rectangle.setFill(Color.BLUE);
        addDotInCenter(rectangle);
    }

    /***
     * Coloreaza o celula ca fiind nevizitata (Color.WHITE) si trateaza cazul de finish.
     * Impreuna cu metoda colorCellAsBlue da impresia de animatie
     * @param row rand
     * @param col coloana
     */

    private void colorCellAsWhite(int row, int col)
    {
        Rectangle rectangle = getRectangleAtPosition(row, col);
        if(rectangle == null || isStart())
        {
            return;
        }

        if(isFinish())
        {
            game = false;
            return;
        }

        rectangle.setFill(Color.WHITE);
    }

    /***
     * Verifica daca o celula este un perete
     * @param red red
     * @param green green
     * @param blue blue
     * @return true/false
     */

    private boolean isWall(int red, int green, int blue)
    {
        return red == 0 && green == 0 && blue == 0;
    }

    /***
     * Verifica daca o celula este pozitia de start
     * @return true/false
     */

    private boolean isStart()
    {
        return row == 40 && col == 40;
    }

    /***
     * Verifica daca o celula este pozitia de finish
     * @return true/false
     */

    private boolean isFinish()
    {
        return row == 400 && col == 400;
    }

    /***
     * Verifica daca o celula este marginea labirintului
     * @param red red
     * @param green green
     * @param blue blue
     * @return true/false
     */

    private boolean isBorder(int red, int green, int blue)
    {
        return red == 255 && green == 192 && blue == 203;
    }

    /***
     * Verifica daca o celula este vizitata
     * @param red red
     * @param green green
     * @param blue blue
     * @return true/false
     */

    private boolean isVisitedCell(int red, int green, int blue)
    {
        return red == 0 && green == 0 && blue == 255;
    }

    /***
     * Ia culoarea unei celule de la pozitia (row, col)
     * @param row rand
     * @param col coloana
     * @return culoarea unei celule de la pozitia (row, col)
     */

    private Color getColorAtPosition(int row, int col)
    {
        for(Node node: pane.getChildren())
        {
            if(node instanceof Rectangle rectangle)
            {
                if(rectangle.getX() == row && rectangle.getY() == col)
                {
                    return (Color) rectangle.getFill();
                }
            }
        }

        return null;
    }

    /***
     * Ia celula de la pozitia (row, col)
     * @param row rand
     * @param col coloana
     * @return celula de la pozitia (row, col)
     */

    private Rectangle getRectangleAtPosition(int row, int col)
    {
        for(Node node: pane.getChildren())
        {
            if(node instanceof Rectangle rectangle)
            {
                if(rectangle.getX() == row && rectangle.getY() == col)
                {
                    return rectangle;
                }
            }
        }

        return null;
    }

    /***
     * Adauga un punct in mijlocul celulei
     * @param rectangle celula
     */

    private void addDotInCenter(Rectangle rectangle)
    {
        double centerX = rectangle.getX() + rectangle.getWidth() / 2;
        double centerY = rectangle.getY() + rectangle.getHeight() / 2;
        Circle dot = new Circle(centerX, centerY, 5, Color.WHITE);

        pane.getChildren().add(dot);
    }
}