package com.example.proiectoop.controller;

import com.example.proiectoop.model.Maze;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
    private Button backButton;
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
        pane.getChildren().add(backButton);
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
     * @param event event keyboard
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
                        col -= 40;
                        colorCell();
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
                        col += 40;
                        colorCell();
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
                        row -= 40;
                        colorCell();
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
                        row += 40;
                        colorCell();
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
        pane.getChildren().add(backButton);
        pane.getChildren().add(currentPositionLabel);

        game = false;
        row = col = 400;

        for(int i = 0; i < 480; i += 40)
        {
            for(int j = 0; j < 480; j += 40)
            {
                Rectangle rectangle = setRectangle(i, j, solvedMaze);
                pane.getChildren().add(rectangle);
            }
        }

        currentPositionLabel.setText("Ai scapat din labirint!");
    }

    /***
     * Inapoi la meniu
     */

    public void onBackButton()
    {
        initialize();
    }

    /***
     * Metoda de debug, listener la mouse
     * @param event event mouse
     */

    public void onMouseClicked(MouseEvent event)
    {
        if(!game)
        {
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        int clickedRow = (int) (mouseX / 40) * 40; // normalizeaza pe axa Ox
        int clickedCol = (int) (mouseY / 40) * 40; // normalizeaza pe axa Oy

        Rectangle clickedRectangle = getRectangleAtPosition(clickedRow, clickedCol);
        Color clickedColor = getColorAtPosition(clickedRow, clickedCol);

        if(clickedRectangle == null || clickedColor == null)
        {
            return;
        }

        // converteste in rgb
        int red = (int) (clickedColor.getRed() * 255);
        int green = (int) (clickedColor.getGreen() * 255);
        int blue = (int) (clickedColor.getBlue() * 255);

        System.out.println("Clicked at " + clickedRow + " " + clickedCol);

        System.out.println("RGB Value: " + red + " " + green + " " + blue);

        if(red == 0 && green == 0 && blue == 0) // perete
        {
            System.out.println("Perete!");
        }
        else if(red == 255 && green == 192 && blue == 203) // margine
        {
            System.out.println("Margine!");
        }
        else if(red == 0 && green == 255 && blue == 0) // punct de start/finish
        {
            System.out.println("Incolorabil!");
        }
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
        if(colorAtPosition == null)
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
     * Coloreaza o celula ca fiind vizitata (Color.BLUE) si trateaza cazul de finish
     */

    private void colorCell()
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
}