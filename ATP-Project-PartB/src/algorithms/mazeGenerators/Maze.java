package algorithms.mazeGenerators;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class Maze  implements Serializable {
    private int[][] maze;
    private Position start_pos;
    private Position goal_pos;
    protected int default_rowSize=5;
    protected int default_colSize =5;
    private int mazeDetailsSize=18;
    /**
     * Maze constructor if either of the parameters is less than 2 a default maze of 5x5 will be created
     * @param rows
     * @param cols
     */
    public Maze(int rows, int cols){
        if(rows<2 || cols<2){
            rows=default_rowSize;
            cols= default_colSize;
        }
        start_pos=new Position(0,0);
        goal_pos=new Position(rows-1,cols-1);
        //generates the maze with zeros
        maze=new int[rows][cols];
    }

    /**
     * Maze constructor
     * @param bytesMaze gets bytes array that defines a maze
     */
    public Maze(byte[] bytesMaze) {
        if(bytesMaze==null){ //Wrong parameter generate a default maze
            start_pos=new Position(0,0);
            goal_pos=new Position(default_rowSize-1,default_colSize-1);
            //generates the maze with zeros
            maze=new int[default_rowSize][default_colSize];
        }
        //Generate maze details: rows&cols quantity and start&goal Positions
        ByteBuffer buff=ByteBuffer.wrap(bytesMaze);
        int rows=buff.getInt();
        int cols=buff.getInt();
        int startEdge=buff.get();
        int startIndex=buff.getInt();
        start_pos=bytesToPosition(startEdge,startIndex,rows,cols);
        int goalEdge=buff.get();
        int goalIndex=buff.getInt();
        goal_pos=bytesToPosition(goalEdge,goalIndex,rows,cols);
        //Generate maze grid
        maze=new int[rows][cols];
        bytesToMazeGrid(bytesMaze,maze,mazeDetailsSize);

    }

    /**
     * Method updated the grid from the data of bytesMaze
     * @param bytesMaze bytes[] which defines a maze
     * @param grid int[][] which is the maze's grid
     * @param index current index of byteMaze to read
     */
    private void bytesToMazeGrid(byte[] bytesMaze,int[][] grid, int index){
            for(int row=0;row< grid.length;row++)
                for (int col=0;col<grid[0].length;col++){
                    grid[row][col]=bytesMaze[index];
                    index++;
                }
    }

    /**
     *
     * @param Edge 0/1/2/3 = Top/Right/Bottom/Left
     * @param index int index on the edge starting from 0
     * @param rows
     * @param cols
     * @return Position matching to the parameters
     */
    private Position bytesToPosition(int Edge,int index, int rows,int cols){
        switch (Edge){
            case 0://Top Edge
                return new Position(0,index);
            case 1://Right Edge
                return new Position(index,cols-1);
            case 2://Bottom Edge
                return new Position(rows-1,index);
            case 3://Left Edge
                return new Position(index,0);
            default:
                return new Position(0,0);
        }
    }

    /**
     * Prints the maze in the format:
     *     // {{ 0 0 1 S 1 0}
     *     // ,{ 1 0 0 1 0 E}}
     */
    public void print(){
        for(int i=0; i<maze.length;i++){
            String tmp_str=(i!=0)?",{":"{{";
            System.out.print(tmp_str);
            for (int j=0;j<maze[0].length;j++){
                //Starting point
                if(i==start_pos.getRowIndex()&& j==start_pos.getColumnIndex())
                    System.out.print("S");
                //End point
                else if(i==goal_pos.getRowIndex()&& j==goal_pos.getColumnIndex())
                    System.out.print("E");
                else
                    System.out.print(maze[i][j]);
            }
            tmp_str=(i!= maze.length-1)?"}\n":"}}\n";
            System.out.print(tmp_str);
        }
    }

    /**
     * Method to create a quick simple GUI for the Maze
     * mainly for debug purposes
     */
    public void initializeWindow()
    {
        JFrame mainFrame = new JFrame("MazeSolver");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(maze.length, maze[0].length));
        mainFrame.setLocationRelativeTo(null);
        for (int row = 0; row < maze.length; row++)
        {
            for (int col = 0; col < maze[0].length; col++)
            {

                if ( row == start_pos.getRowIndex() && col == start_pos.getColumnIndex())
                {
                    JLabel label1 = new JLabel();
                    label1.setHorizontalAlignment(JLabel.CENTER);
                    label1.setPreferredSize(new Dimension(40, 40));
                    label1.setBackground(Color.green);
                    label1.setOpaque(true);
                    //label1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    mainFrame.add(label1);
                }

                else if ( row == goal_pos.getRowIndex() && col == goal_pos.getColumnIndex())
                {
                    JLabel label1 = new JLabel();
                    label1.setHorizontalAlignment(JLabel.CENTER);
                    label1.setPreferredSize(new Dimension(40, 40));
                    label1.setBackground(Color.red);
                    label1.setOpaque(true);
                    //label1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    mainFrame.add(label1);
                }
                else
                {
                    JLabel label = makeLabel(maze[row][col]);
                    mainFrame.add(label);
                }
            }
        }
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public JLabel makeLabel(int c)
    {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(40, 40));
        switch(c)
        {
            case 1:
                label.setBackground(Color.BLACK);
                break;
            case 0:
                label.setBackground(Color.WHITE);
                break;
            default:
                label.setBackground(Color.BLACK);
                break;
        }
        label.setOpaque(true);
        //label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return label;
    }

    public Position getStartPosition() {
        return start_pos;
    }

    public Position getGoalPosition() {
        return goal_pos;
    }

    public int[][] getMaze() {
        return maze;
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public int getLength(){
        return maze.length;
    }

    public int getWidth(){
        if(maze.length==0)
            return 0;
        return maze[0].length;
    }

    public void setGoal_pos(Position goal_pos) {
        this.goal_pos = goal_pos;
    }

    public void setStart_pos(Position start_pos) {
        this.start_pos = start_pos;
    }

    //Checks if the position is within the borders of the maze

    /**
     * @param pos gets a Maze Position
     * @return true if indexes are legal, false otherwise
     */
    public boolean isLegalPosition(Position pos){
        if(pos==null
                || pos.getColumnIndex()>=this.getWidth()
                || pos.getRowIndex()>=this.getLength()
                || pos.getRowIndex()<0
                || pos.getColumnIndex()<0)
            return false;
        return true;
    }

    /**
     * Sets value of a specific Position
     * @param pos
     * @param value
     * @return true if succeeded and false otherwise
     */
    public boolean setValue(Position pos,int value){
        if(value<0 || value>1 || !(isLegalPosition(pos)))
            return false;
        maze[pos.getRowIndex()][pos.getColumnIndex()]=value;
        return true;
    }

    /**
     * OverLoad, Checks if the indexes are within the borders of the maze
     * @param row
     * @param col
     * @return true if indexes are legal, false otherwise
     */
    public boolean isLegalPosition(int row,int col){
        if(col>=this.getWidth() || row>=this.getLength() || row<0 || col<0)
            return false;
        return true;
    }

    /**
     * //Sets value of a specific indexes
     * @param row
     * @param col
     * @param value
     * @return true if succeeded and false otherwise
     */
    public boolean setValue(int row,int col,int value){
        if(value<0 || value>1 || !(isLegalPosition(row,col)))
            return false;
        maze[row][col]=value;
        return true;
    }


    /**
     *  Get value of a specific Position
     * @param pos
     * @return value of the position: int 1/0, or -1 if illegal position
     */
    public int getValue(Position pos){
        if(isLegalPosition(pos))
            return maze[pos.getRowIndex()][pos.getColumnIndex()];
        //Error value
        return -1;
    }


    /**
     * OverLoad: Get value of a specific Position
     * @param row
     * @param col
     * @return value of the position: int 1/0, or -1 if illegal position
     */
    public int getValue(int row,int col){
        if(isLegalPosition(row,col))
            return maze[row][col];
        //Error value
        return -1;
    }

    /**
     * @return bytes array defining the current maze
     */
    public byte[] toByteArray(){
        int curIndex=0;
        byte[] mazeBytes=new byte[this.getLength()*this.getWidth()+mazeDetailsSize];
        curIndex=insertMazeDetails(mazeBytes,curIndex);
        insertMazeGrid(mazeBytes,curIndex);
        return mazeBytes;
    }

    /**
     * Method updates mazeBytes bytes array with maze values at each position
     * Order: Iterating through every row starting with 0
     * @param mazeGrid byte Array to update with proper values
     * @param curIndex index of the current byte in maze
     */
    private void insertMazeGrid(byte[] mazeGrid, int curIndex){
        for(int row=0;row<this.getLength();row++){
            for(int col=0;col<this.getWidth();col++){
                mazeGrid[curIndex]=(byte) this.getValue(row,col);
                curIndex++;
            }
        }
    }

    /**
     * Method updates mazeBytes with maze's number of rows and cols and starting and goal positions.
     * Order of mazeDetail after execution: rowQuantity(4 bytes), colQuantity(4 bytes), startEdge(1), start index on
     * the edge (4), goalEdge (1), goal index on the edge (4)
     * @param mazeBytes bytes array to store the maze's details
     * @return curIndex current value (int)
     */
    private int insertMazeDetails(byte[] mazeBytes,int curIndex){
        byte[] mazeDetails=new byte[mazeDetailsSize];
        ByteBuffer buff=ByteBuffer.wrap(mazeDetails);
        //allocate 4 bytes for rows,cols and indexes. At most  2,147,483,647 just like int
        byte[] rowQuantity=ByteBuffer.allocate(4).putInt(this.getLength()).array();
        byte[] colQuantity=ByteBuffer.allocate(4).putInt(this.getWidth()).array();
        int[] index=new int[1]; //int Wrapper to be changes by getEdge
        byte startEdge=getEdge(getStartPosition(),index);
        byte[] startIndex=ByteBuffer.allocate(4).putInt(index[0]).array();
        byte goalEdge=getEdge(getGoalPosition(),index);
        byte[] goalIndex=ByteBuffer.allocate(4).putInt(index[0]).array();
        //Combining all bytes to one bytes array
        buff.put(rowQuantity);
        buff.put(colQuantity);
        buff.put(startEdge);
        buff.put(startIndex);
        buff.put(goalEdge);
        buff.put(goalIndex);
        //Inserting mazeDetails to mazeBytes
        mazeDetails=buff.array();
        for(int i=0;i<mazeDetails.length;i++){
            mazeBytes[curIndex]=mazeDetails[i];
            curIndex++;
        }
        return curIndex;
    }

    /**
     * helper method to get Position's edge and update the index on the edge, helps to store the Position in less bytes
     * @param position start/goal position
     * @param index pointer for an int to be updated with index value
     * @return byte with a value of 0-3 (Top=0, Right=1, Bottom=2, Left=3)
     */
    private byte getEdge(Position position, int[] index){
        //Edges indexes are clockwise, Top=0, Right=1, Bottom=2, Left=3
        if(position.getRowIndex()==0){
            index[0]=position.getColumnIndex();
            return 0;
        }
        if(position.getRowIndex()==this.getLength()-1){
            index[0]= position.getColumnIndex();
            return 2;
        }
        if(position.getColumnIndex()==0){
            index[0]= position.getRowIndex();
            return 3;
        }
        index[0]= position.getRowIndex();
        return 1;
    }
}
