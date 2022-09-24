package algorithms.mazeGenerators;

import java.util.ArrayList;
import java.util.Random;

public class MyMazeGenerator extends AMazeGenerator{
    private ArrayList<Position> unvisited=new ArrayList<>();
    private static final Random rnd=new Random();

    /**
     * @param rows
     * @param cols
     * @return a Maze generated using Prims algorithm
     */
    @Override
    public Maze generate(int rows, int cols) {
        Maze maze=new Maze(rows,cols);
        init_walls(maze);
        maze.setStart_pos(starting_point_generate(maze));
        primMazeGenerator(maze);
        maze.setGoal_pos(chooseEndPosition(maze));
        return maze;
    }


    /**
     * Method inits a maze with 1 in all the cells (walls only)
     * @param maze
     */
    private void init_walls(Maze maze){
        if(maze==null)
            return;
        for(int row=0;row<maze.getLength();row++){
            for(int col=0;col< maze.getWidth();col++){
                maze.setValue(row,col,1);
            }
        }
    }

    /**
     * Method generates maze using Prim's algorithm, meaning a span tree is created in the maze
     * @param maze
     */
    private void primMazeGenerator(Maze maze){
        Position cur=maze.getStartPosition();
        maze.setValue(cur,0);
        addPotentialNeighbors(maze,cur);
        // Keep traversing the maze until no more neighbors can be visited
        while(!unvisited.isEmpty()){
            int index=rnd.nextInt(unvisited.size());
            //Randomly pick one unvisited neighbor
            cur=unvisited.get(index);
            if(isClosingALoop(maze, cur)){
                unvisited.remove(index);
            }else{
                //set value 0(path) to the chosen cell
                maze.setValue(cur,0);
                unvisited.remove(index);
                addPotentialNeighbors(maze,cur);
            }
        }
    }


    /**
     *  Method adds to unvisited ArrayList current's cell potential neighbors,
     *  which weren't visited & legal positions & do not close a loop
     * @param maze
     * @param cur
     */
    private void addPotentialNeighbors(Maze maze, Position cur){
        Position left=new Position(cur.getRowIndex(), cur.getColumnIndex()-1);
        Position right=new Position(cur.getRowIndex(), cur.getColumnIndex()+1);
        Position top=new Position(cur.getRowIndex()-1, cur.getColumnIndex());
        Position bottom=new Position(cur.getRowIndex()+1, cur.getColumnIndex());
        if(maze.isLegalPosition(left) && !isClosingALoop(maze,left)) unvisited.add(left);
        if(maze.isLegalPosition(right) && !isClosingALoop(maze,right)) unvisited.add(right);
        if(maze.isLegalPosition(bottom) && !isClosingALoop(maze,bottom)) unvisited.add(bottom);
        if(maze.isLegalPosition(top) && !isClosingALoop(maze,top)) unvisited.add(top);
    }


    /**
     * @param maze
     * @param pos
     * @return true if changing position's value to 0 will cause a loop, false otherwise
     */
    private boolean isClosingALoop(Maze maze,Position pos){
            // A position may close a loop only if: 1. its value is 0,
            // 2. valid position in the maze, 3. has at least two 0 value neighbors
            if(maze.getValue(pos)==0)
                return true;
            int accessible_neighbors=0;
            int row=pos.getRowIndex();
            int col=pos.getColumnIndex();
            //Left neighbor
            if(maze.isLegalPosition(row,col-1)&& maze.getValue(row,col-1)==0)
                accessible_neighbors++;
            //Right neighbor
            if(maze.isLegalPosition(row,col+1)&& maze.getValue(row,col+1)==0)
                accessible_neighbors++;
                if (accessible_neighbors>=2)
                    return true;
            //Top neighbor
            if(maze.isLegalPosition(row-1,col)&& maze.getValue(row-1,col)==0)
                accessible_neighbors++;
                if (accessible_neighbors>=2)
                    return true;
            //Bottom neighbor
            if(maze.isLegalPosition(row+1,col)&& maze.getValue(row+1,col)==0)
                accessible_neighbors++;
            return (accessible_neighbors >= 2);
    }

    /**
     * @param maze
     * @return randomly chosen valid end position
     */
    private Position chooseEndPosition(Maze maze){
        //Randomly pick to iterate from start to end of array: 0 Or from end to start of array:1
        Position goal_pos;
        int startEnd=rnd.nextInt(2);
        if (startEnd==0)
            goal_pos=chooseFromStart(maze);
        else
            goal_pos=chooseFromEnd(maze);
        // Making sure that start and end positions aren't the same position
        while(goal_pos.equals(maze.getStartPosition())){
            if (startEnd==0)
                goal_pos=chooseFromStart(maze);
            else
                goal_pos=chooseFromEnd(maze);
        }
        return goal_pos;
    }


    /**
     * Helper method to randomly pick end position.
     * Randomly generates position on a maze's edge, looking for first 0(path) value on the edge
     * @param maze
     * @return Position on one of the edges
     */
    private Position chooseFromStart(Maze maze){
        Position goal_pos=new Position(0,0);
        int edgeIdx=rnd.nextInt(4);
        Edge edge=Edge.values()[edgeIdx];
        switch (edge){
            case Left:
                for (int i=0;i<maze.getLength();i++)
                    if(maze.getValue(i,0)==0)
                        goal_pos=new Position(i,0);
            case Right:
                for (int i=0;i<maze.getLength();i++)
                    if(maze.getValue(i, maze.getWidth()-1)==0)
                        goal_pos=new Position(i,maze.getWidth()-1);
                break;
            case Bottom:
                for (int i=0;i<maze.getWidth();i++)
                    if(maze.getValue(maze.getLength()-1,i)==0)
                        goal_pos=new Position(maze.getLength()-1,i);
                break;
            case Top:
                for (int i=0;i<maze.getWidth();i++)
                    if(maze.getValue(0,i)==0)
                        goal_pos=new Position(0,i);
                break;
        }
        return goal_pos;
    }

    //Helper method to randomly pick end position.
    // Randomly generates position on a maze's edge, looking for last 0(path) value on the edge
    /**
     * Helper method to randomly pick end position.
     * Randomly generates position on a maze's edge, looking for last 0(path) value on the edge
     * @param maze
     * @return Position on one of the edges
     */
    private Position chooseFromEnd(Maze maze){
        Position goal_pos=new Position(0,0);
        int edgeIdx=rnd.nextInt(4);
        Edge edge=Edge.values()[edgeIdx];
        switch (edge){
            case Left:
                for (int i=maze.getLength()-1;i>=0;i--)
                    if(maze.getValue(i,0)==0)
                        goal_pos=new Position(i,0);
            case Right:
                for (int i=maze.getLength()-1;i>=0;i--)
                    if(maze.getValue(i, maze.getWidth()-1)==0)
                        goal_pos=new Position(i,maze.getWidth()-1);
                break;
            case Bottom:
                for (int i=maze.getWidth()-1;i>=0;i--)
                    if(maze.getValue(maze.getLength()-1,i)==0)
                        goal_pos=new Position(maze.getLength()-1,i);
                break;
            case Top:
                for (int i=maze.getWidth()-1;i>=0;i--)
                    if(maze.getValue(0,i)==0)
                        goal_pos=new Position(0,i);
                break;
        }
        return goal_pos;
    }

}
