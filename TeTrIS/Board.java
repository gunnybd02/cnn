import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
public class Board  extends JPanel implements KeyListener {
    public static int STATE_GAME_PLAY=0;
    private boolean leftClick = false;
    private boolean gamePaused = false;
    private JLabel scoreLabel;
    private boolean gameOver = false;
    private static Shape currentShape, nextShape;

    public static int STATE_GAME_PAUSE=1;
    public static int STATE_GAME_OVER=2;
    private int state=STATE_GAME_PLAY;
    public static final int Board_Width=10;
    public  static final int Board_Height=20;
    public static final int Block_Size=30;
    private static int FPS=60;
    private static int delay =FPS/1000;
    private int normal=600;
    private int fast=5;
    private int delayTimeForMovement=normal;
    private long beginTime;
    private int deltaX=0;
    private boolean collision=false;

    private Random random;
    private static final long serialVersionUID = 1L;
    private int score = 0;

    private Timer looper;
    private Color[][] board=new Color[Board_Height][Board_Width];

    private Color[] colors = {Color.decode("#ed1c24"), Color.decode("#ff7f27"), Color.decode("#fff200"),
            Color.decode("#22b14c"), Color.decode("#00a2e8"), Color.decode("#a349a4"), Color.decode("#3f48cc")};
    private Shape[] shapes = new Shape[7];
    private BufferedImage pause,refresh,arrow;
    private Rectangle stopBounds,refreshBounds;
    private int mouseX,mouseY;

    public Board(){


        scoreLabel = new JLabel("Score: 0");
        add(scoreLabel);
        random=new Random();
        shapes[0] = new Shape(new int[][]{
                {1, 1, 1, 1} // I shape;
        }, colors[0],this );

        shapes[1] = new Shape(new int[][]{
                {1, 1, 1},
                {0, 1, 0}, // T shape;
        }, colors[1],this);

        shapes[2] = new Shape(new int[][]{
                {1, 1, 1},
                {1, 0, 0}, // L shape;
        }, colors[2],this);

        shapes[3] = new Shape(new int[][]{
                {1, 1, 1},
                {0, 0, 1}, // J shape;
        }, colors[3],this);

        shapes[4] = new Shape(new int[][]{
                {0, 1, 1},
                {1, 1, 0}, // S shape;
        }, colors[4],this);

        shapes[5] = new Shape(new int[][]{
                {1, 1, 0},
                {0, 1, 1}, // Z shape;
        }, colors[5],this);

        shapes[6] = new Shape(new int[][]{
                {1, 1},
                {1, 1}, // O shape;
        }, colors[6],this);

currentShape=shapes[0];
        looper=new Timer(5/100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        looper.start();
        setPreferredSize(new Dimension(Board_Width * Block_Size, Board_Height * Block_Size));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
    }
    private void update() {


        currentShape.update();
        if(gameOver){
            looper.stop();
        }
    }


    public  void setCurrentShape(){
       currentShape=shapes[random.nextInt(shapes.length)];
       currentShape.reset();

        for (int row = 0; row < currentShape.getCoords().length; row++) {
            for (int col = 0; col < currentShape.getCoords()[0].length; col++) {
                if (currentShape.getCoords()[row][col] != 0) {
                    if (board[currentShape.getY() + row][currentShape.getX() + col] != null) {
                        gameOver = true;
                    }
                }
            }
        }
    }

    @Override
    protected  void paintComponent(Graphics g){
        if (currentShape == null) {
            // currentShape is null, so we can't draw it
            // you can return early or do some other kind of error handling here
            return;
        }
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0,0,getWidth(),getHeight());
        currentShape.render(g);

        for(int row=0;row<board.length;row++){
            for(int col=0;col<board[0].length;col++){
                if(board[row][col]!=null){
                    g.setColor(board[row][col]);
                    g.fillRect(col*Block_Size,row*Block_Size,Block_Size,Block_Size);
                    g.fillRect(col*Block_Size,row*Block_Size,Block_Size,Block_Size);
                }

            }
        }
//draw board
        g.setColor(Color.WHITE);
        for(int row=0;row<Board_Height;row++){
            g.drawLine(0,Block_Size*row,Block_Size*Board_Width,Block_Size*row);
        }

        for(int col=0;col<Board_Width+1;col++){
            g.drawLine(col*Block_Size,0,col*Block_Size,Block_Size*Board_Height);
        }
        if(state==STATE_GAME_OVER) {
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 200, 200);
        }

        if(state==STATE_GAME_PAUSE) {
            g.setColor(Color.WHITE);
            g.drawString("GAME PAUSE", 200, 200);
        }
    }
    public Color[][] getBoard(){
        return board;
    }
    private void checkOverGame(){
        int [][] coords=currentShape.getCoords();
        for(int row=0;row<coords.length;row++){
            for(int col=0;col<coords[0].length;col++){
                if(coords[row][col]!=0){
                    if(board[row+currentShape.getY()][col+currentShape.getX()]!=null){
                        state=STATE_GAME_OVER;
                    }
                }
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                delayTimeForMovement=fast;
                break;
            case KeyEvent.VK_RIGHT:
                currentShape.moveRight();
                break;
            case KeyEvent.VK_LEFT:
                currentShape.moveLeft();
                break;
            case KeyEvent.VK_UP:
                currentShape.rotateShape();
        }
        if(state==STATE_GAME_OVER){
            if(e.getKeyChar()==KeyEvent.VK_SPACE){
                for(int row=0;row<board.length;row++){
                    for(int col=0;col<board[0].length;col++){
                        board[row][col]=null;
                    }
                }
                setCurrentShape();
                state=STATE_GAME_PLAY;
            }
        }

        if(e.getKeyChar()==KeyEvent.VK_SPACE){
            if(state==STATE_GAME_PLAY){
                state=STATE_GAME_PAUSE;
            } else if(state==STATE_GAME_PAUSE){
                state=STATE_GAME_PLAY;
            }
        }

    }
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar()==KeyEvent.VK_SPACE){
               currentShape.speedDown();
        }
    }


    public void setNextShape() {
        int index = random.nextInt(shapes.length);
        int colorIndex = random.nextInt(colors.length);
        nextShape = new Shape(shapes[index].getCoords(),  colors[colorIndex],this);
    }



}


