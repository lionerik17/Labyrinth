package com.example.proiectoop.model;

import java.util.*;

public class Maze {
    private int size; // size of the maze (10 x 10)
    private char[][] maze; // generated maze
    private char[][] borderedMaze; // generated maze, with borders around it
    private Point[][] parent; // used for generating the solution of the maze

    public Maze(int size)
    {
        this.size = size;
        this.maze = initMaze(getSize());
        this.borderedMaze = initMaze(getSize() + 2);
        this.parent = new Point[getSize()][getSize()];
    }

    public int getSize()
    {
        return size;
    }

    public char[][] getBorderedMaze()
    {
        return borderedMaze;
    }

    /***
     * Generate a random maze
     */

    public void generate()
    {
        int newNum = getSize() + 2;

        maze[0][0] = 'O';
        maze[getSize() - 1][getSize() - 1] = 'O';

        dfs();
        copyAsBordered(newNum);
    }

    /***
     * Solve a random maze
     */

    public void solve()
    {
        int newNum = getSize() + 2;

        bfs();
        generatePath();
        copyAsBordered(newNum);
    }

    /***
     * Surround the generated maze with walls
     * @param newNum original size + 2
     */

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

    /***
     * Generate the maze using a modified DFS:
     * 0. initialize the maze only with walls
     * 1. push onto stack & mark as visited the start cell (0, 0)
     * 2. while there are elements in the stack
     * 3. get the neighboring walls of the current cell (marked as 'W')
     * 4. if there are unvisited neighbors
     *    choose of them
     *    remove the wall between the current cell and the picked neighbor (both marked as ' ' for 'reasons')
     *    mark the current cell as part of the maze
     *    push onto stack & mark as visited the current cell
     * 5. if there are no visited neighbors, pop the current cell and go to step 2

     * There will be cases when the finish cell is surrounded with walls
     * The fix: start the modified DFS again, but stop when the current cell is an empty cell
     */

    private void dfs()
    {
        int[][] visitedForward = initVisited(getSize());
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

        int[][] visitedBackward = initVisited(getSize());
        visitedBackward[getSize() - 1][getSize() - 1] = 1;

        Stack<Point> stackBackward = new Stack<>();
        stackBackward.push(new Point(getSize() - 1, getSize() - 1));

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

    /***
     * Solve the maze using BFS on a matrix
     */

    private void bfs()
    {
        int[][] dir = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        int[][] visited = initVisited(getSize());

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

    /***
     * From the BFS tree, go back to start using the parent matrix
     */

    private void generatePath()
    {
        Point current = new Point(getSize() - 1, getSize() - 1);

        while(current != null)
        {
            maze[current.getRow()][current.getCol()] = 'P';
            current = parent[current.getRow()][current.getCol()];
        }

        // these will be altered by the while loop, mark them as the start and the finish again
        maze[0][0] = 'O';
        maze[getSize() - 1][getSize() - 1] = 'O';
    }

    /***
     * Step 0 of modified DFS
     * @param num size of the maze (10 x 10)
     * @return A maze with walls only
     */

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

    private boolean isValid(Point point)
    {
        return point.getRow() >= 0 && point.getRow() < getSize() && point.getCol() >= 0 && point.getCol() < getSize();
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
