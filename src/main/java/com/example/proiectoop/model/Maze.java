package com.example.proiectoop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Maze {
    private int num;
    private char[][] maze;
    private char[][] borderedMaze;
    public Maze(int num)
    {
        this.num = num;
        this.maze = initMaze(getNum());
        this.borderedMaze = initMaze(getNum() + 2);
    }

    public int getNum()
    {
        return num;
    }

    public char[][] getMaze()
    {
        return maze;
    }

    public char[][] getBorderedMaze()
    {
        return borderedMaze;
    }

    public void generate()
    {
        int newNum = getNum() + 2;
        dfs();

        for(int i = 0; i < newNum; ++i)
        {
            for(int j = 0; j < newNum; ++j)
            {
                if(i == 0 || j == 0 || i == newNum - 1 || j == newNum - 1)
                {
                    borderedMaze[i][j] = '+';
                    continue;
                }

                borderedMaze[i][j] = maze[i - 1][j - 1];
            }
        }
    }

    private void dfs()
    {
        int[][] visitedForward = initVisited(getNum());
        visitedForward[0][0] = 1;
        maze[0][0] = 'O';

        Stack<Point> stackForward = new Stack<>();
        stackForward.push(new Point(0, 0));

        while(!stackForward.empty())
        {
            Point current = stackForward.peek();
            int currentRow = current.getRow();
            int currentCol = current.getCol();

            visit(visitedForward, stackForward, current, currentRow, currentCol);
        }

        int[][] visitedBackward = initVisited(getNum());
        visitedBackward[getNum() - 1][getNum() - 1] = 1;
        maze[getNum() - 1][getNum() - 1] = 'O';

        Stack<Point> stackBackward = new Stack<>();
        stackBackward.push(new Point(getNum() - 1, getNum() - 1));

        while(!stackBackward.empty())
        {
            Point current = stackBackward.peek();
            int currentRow = current.getRow();
            int currentCol = current.getCol();

            if(maze[currentRow][currentCol] == ' ')
            {
                break;
            }

            visit(visitedBackward, stackBackward, current, currentRow, currentCol);
        }
    }

    private void visit(int[][] visited, Stack<Point> stack, Point current, int currentRow, int currentCol)
    {
        List<Point> neighbors = getNeighbors(current);
        List<Point> unvisitedNeighbors = new ArrayList<>();

        for(Point point: neighbors)
        {
            int row = point.getRow();
            int col = point.getCol();
            if(visited[row][col] == 0)
            {
                unvisitedNeighbors.add(point);
            }
        }

        if(!unvisitedNeighbors.isEmpty())
        {
            Random random = new Random();
            Point next = neighbors.get(random.nextInt(unvisitedNeighbors.size()));

            int newRow = next.getRow();
            int newCol = next.getCol();

            maze[(currentRow + newRow) / 2][(currentCol + newCol) / 2] = ' ';
            maze[newRow][newCol] = ' ';

            visited[newRow][newCol] = 1;
            stack.push(next);
        }
        else
        {
            stack.pop();
        }
    }

    private char[][] initMaze(int num)
    {
        char[][] maze = new char[num][num];

        for(int i = 0; i < maze.length; ++i)
        {
            for(int j = 0; j < maze[0].length; ++j)
            {
                maze[i][j] = 'W';
            }
        }

        return maze;
    }

    private int[][] initVisited(int num)
    {
        return new int[num][num];
    }

    public void print()
    {
        for(int i = 0; i < borderedMaze.length; ++i)
        {
            for(int j = 0; j < borderedMaze[0].length; ++j)
            {
                System.out.print(borderedMaze[i][j]);
            }
            System.out.println();
        }
    }

    private boolean isValid(Point point)
    {
        return point.getRow() >= 0 && point.getRow() < getNum() && point.getCol() >= 0 && point.getCol() < getNum();
    }

    private List<Point> getNeighbors(Point point)
    {
        int[][] dir = {{-2, 0}, {0, -2}, {2, 0}, {0, 2}};
        List<Point> neighbors = new ArrayList<>();

        for(int i = 0; i < 4; ++i)
        {
            int newRow = point.getRow() + dir[i][0];
            int newCol = point.getCol() + dir[i][1];
            Point newPoint = new Point(newRow, newCol);

            if(isValid(newPoint) && maze[newRow][newCol] == 'W')
            {
                neighbors.add(newPoint);
            }
        }

        return neighbors;
    }
}
