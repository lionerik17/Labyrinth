package com.example.proiectoop.model;

import java.util.*;

public class Maze {
    private int num;
    private char[][] maze;
    private char[][] borderedMaze;
    private Point[][] parent;

    public Maze(int num)
    {
        this.num = num;
        this.maze = initMaze(getNum());
        this.borderedMaze = initMaze(getNum() + 2);
        this.parent = new Point[getNum()][getNum()];
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
        maze[0][0] = 'O';
        maze[getNum() - 1][getNum() - 1] = 'O';

        int newNum = getNum() + 2;
        dfs();
        copyAsBordered(newNum);
    }

    public void solve()
    {
        int newNum = getNum() + 2;
        bfs();
        generatePath();
        copyAsBordered(newNum);
    }

    private void copyAsBordered(int newNum)
    {
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
        Stack<Point> stackForward = new Stack<>();

        stackForward.push(new Point(0, 0));
        visitedForward[0][0] = 1;

        while(!stackForward.empty())
        {
            Point current = stackForward.peek();
            int currentRow = current.getRow();
            int currentCol = current.getCol();

            visit(visitedForward, stackForward, current, currentRow, currentCol);
        }

        int[][] visitedBackward = initVisited(getNum());
        visitedBackward[getNum() - 1][getNum() - 1] = 1;

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

    private void bfs()
    {
        int[][] dir = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        int[][] visited = initVisited(getNum());

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(0, 0));
        visited[0][0] = 1;

        while(!queue.isEmpty())
        {
            Point current = queue.peek();
            queue.remove();

            for(int i = 0; i < 4; ++i)
            {
                int newRow = current.getRow() + dir[i][0];
                int newCol = current.getCol() + dir[i][1];
                Point newPoint = new Point(newRow, newCol);

                if(isValid(newPoint) && visited[newRow][newCol] == 0 && maze[newRow][newCol] != 'W')
                {
                    queue.add(newPoint);
                    parent[newRow][newCol] = current;
                    visited[newRow][newCol] = 1;
                }
            }
        }
    }

    private void visit(int[][] visited, Stack<Point> stack, Point current, int currentRow, int currentCol)
    {
        List<Point> neighbors = getWallNeighbors(current);
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

    private void generatePath()
    {
        Point current = new Point(getNum() - 1, getNum() - 1);

        while(current != null)
        {
            maze[current.getRow()][current.getCol()] = 'P';
            current = parent[current.getRow()][current.getCol()];
        }

        maze[0][0] = 'O';
        maze[getNum() - 1][getNum() - 1] = 'O';
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

    private List<Point> getWallNeighbors(Point point)
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
