import java.awt.*;

public class Shape {
    private int x=4,y=0;
    private int normal=600;
    private int fast=50;
    private int delayTimeForMovement=normal;
    private long beginTime;
    private int deltaX=0;
    private boolean collision=false;
    private int[][] coords;
    public static final int Block_Size=30;
    private Color color;
    private  Board board;
    public static final int Board_Width=10;
    public  static final int Board_Height=20;



    public Shape(int[][] coords, Color color, Board board) {
        this.coords = coords;
        this.color = color;
        this.board = board;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y){
        this.y=y;
    }
    public void reset(){
        this.x=4;
        this.y=0;
        collision=false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[][] getCoords(){
        return coords;
    }

    public void update(){
        boolean moveX=true;

        if(!(x+deltaX+coords[0].length>10)&&!(x+deltaX<0)){
            for(int row=0;row<coords.length;row++){
                for(int col=0;col<coords[row].length;col++){
                    if(coords[row][col]!=0){
                    if(board.getBoard()[y+row][x+deltaX+col]!=null){
                        moveX=false;
                    }
                }
                }
            }if(moveX){
            x+=deltaX;}
        }
        deltaX=0;
        if(System.currentTimeMillis()-beginTime>delayTimeForMovement){
            if(!(y+1+coords.length>Board_Height)){
                for(int row=0;row< coords.length;row++){
                    for (int col=0;col<coords[row].length;col++){
                        if(coords[row][col]!=0){
                            if(board.getBoard()[y+row+1][x+deltaX+col]!=null){
                                collision=true;
                            }
                        }
                    }
                }
                if(!collision){
                    y++;
                }

            }else{
                collision=true;
            }
            beginTime=System.currentTimeMillis();
        }

        if (collision) {
            for(int row=0;row<coords.length;row++){
                for(int col=0;col<coords[0].length;col++){
                    if(coords[row][col]!=0){
                        board.getBoard()[y+row][x+col]=color.red;
                    }
                }
            }
            checkLine();
            board.setCurrentShape();
            return;
        }
    }
    public void rotateShape(){
        int [][] rotatedShape=transposeMatrix(coords);
        reverseRows(rotatedShape);
        if(x+rotatedShape[0].length>Board_Width||(y+rotatedShape.length>20)){
            return;
        }
        for(int row=0;row<rotatedShape.length;row++){
            for(int col=0;col<rotatedShape[row].length;col++){
                if(rotatedShape[row][col]!=0){
                    if(board.getBoard()[y+row][x+col]!=null){
                        return;
                    }
                }
            }
        }
        coords=rotatedShape;

    }
    private  int [][]  transposeMatrix(int [][] matrix){
        int [][] temp=new int[matrix[0].length][matrix.length];
        for(int row=0;row<matrix.length;row++){
            for(int col=0;col<matrix[0].length;col++){
                temp[col][row]=matrix[row][col];
            }
        }
        return  temp;
    }
    private void reverseRows(int [][] matrix){
        int middle= matrix.length/2;
        for(int row=0;row<middle;row++){
            int[] temp=matrix[row ];
            matrix[row]=matrix[matrix.length-row-1];
            matrix[matrix.length-row-1]=temp;
        }
    }


    public void render(Graphics g){
        g.setColor(color);
        for(int row=0;row< coords.length;row++){
            for(int col=0;col<coords[0].length;col++){
                if(coords[row][col]!=0){
                    g.fillRect(col*Block_Size+x*Block_Size,row*Block_Size+y*Block_Size,Block_Size,Block_Size);
                    g.fillRect( col * Block_Size ,
                            row * Block_Size ,Block_Size, Block_Size);
                }
            }
        }
    }
    public void speedUp(){
    delayTimeForMovement=fast;
    }
    public void speedDown(){
    delayTimeForMovement=normal;
    }
    public void moveRight(){
    deltaX=1;
    }
    public void moveLeft(){
    deltaX=-1;
    }
    public void checkLine(){
        int bottomLine=board.getBoard().length-1;
        for(int topLine=board.getBoard().length-1;topLine>0;topLine--){
            int count=0;
            for(int col=0;col<board.getBoard()[0].length;col++){
                if(board.getBoard()[topLine][col]!=null){
                    count++;
                }
                board.getBoard()[bottomLine][col]=board.getBoard()[topLine][col];
            }
            if(count<board.getBoard()[0].length){
                bottomLine--;

            }
        }
    }

}
