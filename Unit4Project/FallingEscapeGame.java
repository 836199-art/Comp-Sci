import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

// ================= GAME OBJECT =================
abstract class GameObject { //creates parent class for all game objects
    protected int x, y, width, height; //stores position and size of objects

    public GameObject(int x, int y, int width, int height) { //contructor that set starting values
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() { //creates a recatngle hitbox for collision detection
        return new Rectangle(x, y, width, height); //returns rectangle
    }

    public abstract void draw(Graphics g); //asbstract emthod that forces a subclass to create its own drawing method
}
// ================= PLATFORM =================
class Platform extends GameObject {
    public Platform(int x, int y) {  //constructor
        super(x, y, 120, 15); //calls parents constructor and creates a paltform size
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
        //draws the platform as a green rectangle
    }
}

// ================= FALLING OBJECT =================
class FallingObject extends GameObject { //creates falling obstacle class
    
    private int speed = 4; //controls falling speed
    
    public FallingObject(int x, int y){
        super(x, y, 25, 25); //constructor that sets the size
    }
   public void move(){
       y += speed; //moves object downward by increasing y
   } 
   public void reset(){
       y = -50; //resets object above the screen
       x = new Random().nextInt(760); //creates random x-position
   }
   @Override
   public void draw(Graphics g){
       g.setColor(Color.RED);
       g.fillOval(x, y, width, height);
       //draws the falling object
   }
}
// ================= PLAYER CLASS =================
class Player extends GameObject {
    private int velY = 0; //stores the vertical velocity
    private boolean jumping = false; //tracks if player is jumping

    public Player(int x, int y) {
        super(x, y, 40,40);
    }
    public void moveLeft() {
        x -= 6; //moves player left
        if( x < 0) x = 0; //prevents from going off of screen
    }
    public void moveRight() {
        x += 6; //moves player right
        if( x + width > 800) x = 800 - width; //prevents leaving the right edge of screen
    }
    public void jump() {
       if(!jumping){ //only jump if not already
        velY = -15; //launches player upward
        jumping = true; //player is in air
    }
}  
      
    public void update(){
        y += velY; //applies vertical movement
        velY += 1; //applies gravity
        
    
        if(velY > 10) velY = 10; //limits falling speed
        
        if(y >= 520){ //checks if player has reach ground
            y = 520;
            velY = 0;
            jumping = false; //allows jumping again
        }
    }
    public int getVelY(){
        return velY; //returns vertical velocity
    }
    public void landOn(int platformY){
        y = platformY - height; //player lands exactly on top of platform
        velY = 0;
        jumping = false; //stops falling
    }
    public void reset(){
        x = 300;
        y = 520;
        velY = 0;
        jumping = false;
        //restores start position and state
    }
        @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        //draws player as a blue square
    }
}
// ================= MENU =================
class MenuPanel extends JPanel implements ActionListener {
    //creates menu screena and listens for button clicks

    FallingEscapeGame frame; //refers to main game window

    JButton play = new JButton("PLAY");
    JButton instructions = new JButton("INSTRUCTIONS");
    JButton stats = new JButton("STATS");

    public MenuPanel(FallingEscapeGame frame) { //constructor for menu

        this.frame = frame; //stores to main frame

        setBackground(new Color(20, 80, 120));
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(4,1,10,10));
        panel.setBackground(new Color(40, 90, 140));

        JLabel title = new JLabel("AVOID IT!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        style(play);
        style(instructions);
        style(stats);
        //applies styling to buttons

        play.addActionListener(this);
        instructions.addActionListener(this);
        stats.addActionListener(this);
        //connects the buttons to click handler

        panel.add(title);
        panel.add(play);
        panel.add(instructions);
        panel.add(stats);
        //adds components to panel

        add(panel);
        //add panel to screen
    }

    private void style(JButton b) {
        b.setPreferredSize(new Dimension(120, 25));
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(Color.LIGHT_GRAY);
        b.setFocusPainted(false);
    }

    public void actionPerformed(ActionEvent e) { //handles clicks
        if (e.getSource() == play) frame.showGame();  //starts game
        if (e.getSource() == instructions) frame.showInstructionsPopup(); //shows instructions
        if (e.getSource() == stats) frame.showStatsPopup();
    }
}

// ================= GAME PANEL =================
class GamePanel extends JPanel implements ActionListener {
    //main gameplat screen with timer loop
    
    Player player;
    ArrayList<Platform> platforms = new ArrayList<>();
    ArrayList<FallingObject> objects = new ArrayList<>();
    //game entities
    
    Timer timer; //game loop timer
    boolean left, right; //movement controls
    boolean paused;// pause state
    int score = 0;
    int lives = 3;
    //game stats
    
    FallingEscapeGame frame;
    Random r = new Random();
    //frame reference + randomness
    
    public GamePanel(FallingEscapeGame frame){
        this.frame = frame;
        setBackground(Color.BLACK);
        
        player = new Player(300,520);
    
        for(int i = 0; i < 10; i++)
        platforms.add(new Platform(r.nextInt(650), 100 + i *60));
        //creates 10 platforms spaced vertically
        
        for(int i = 0; i < 5 ; i ++)
        objects.add(new FallingObject(r.nextInt(760), r.nextInt(400)));
        //creates falling objects
        
        setupKeys(); //initializes keyboard controls
        
        timer = new Timer(20, this);
        timer.start();
        //game loop runs every 20 ms
    }
    
    private void setupKeys(){
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW); //stores which keys trigger action, captilized means the keys work even if th epnale is not directly clicked
        ActionMap am = getActionMap(); //stores what actually happens when a key is pressed
        
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "left"); //when left arrow is pressed, trigger action named left
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftOff");
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightOff");
        im.put(KeyStroke.getKeyStroke("SPACE"), "jump");
        im.put(KeyStroke.getKeyStroke("pressed P"), "pause");
        
        am.put("left", new AbstractAction(){ //whne left is pressed, left = true
            public void actionPerformed(ActionEvent e){
                left = true;
                //does not move the player directly but sets a flag so movement happens in the game loop
            }
        });
        am.put("leftOff", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                left = false;
            }
        });
        
        am.put("right", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                right = true;
                
            }
        });
          am.put("rightOff", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                right = false;
            }
          });
            am.put("jump", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                player.jump();
               }
            });
          am.put("pause", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                paused = !paused;
                if(paused) timer.stop();
                else timer.start();
                //stops game loop completely if paused
                //restarts timer when game resumes
            }
          });
    }
    
    public void actionPerformed(ActionEvent e){ //runs every frame
        
        if(!paused){ //only updates if game is not paused
            
         player.update();//applies gravity and movement
        
        if(left) player.moveLeft();
        if(right) player.moveRight();
        //handles movement
        
        Rectangle feet = new Rectangle(player.x, player.y + player.height, player.width, 5);
        //creates small rectangle under player for landing detection
        
        for(Platform p: platforms){
            p.y += 1;
            
            if(feet.intersects(p.getBounds()) && player.getVelY() >= 0){
                player.landOn(p.y);
            }
            if(p.y > 600){
                p.y = -20;
                p.x = r.nextInt(650);
            }
        }
        //Platform
        //Moves platforms down slowly
        //checks collisions
        //respawns when off screen
        
        for(FallingObject o: objects){
            o.move();
            
            if(o.y > 600){
                o.reset();
                score += 10;
            }
            if(player.getBounds().intersects(o.getBounds())){
                o.reset();
                lives--;
                //Falling objects:
                //move down
                //reset when off screen (+score)
                //detect collison with player(-life)
                
                if(lives <= 0){ //game over conditions
                    timer.stop();
                    frame.updateStats(score);
                    frame.saveStatsToFile();
                    frame.showYouLostPopup(score);
                    //stops game and saves results
                }
            }
        }
    }
        repaint(); //redraws screen
    }
    
      protected void paintComponent(Graphics g){
        super.paintComponent(g); //clears screen

       g.setColor(Color.WHITE);
       g.drawString("Score: " + score, 20, 20);
       
       for(int i = 0; i < lives; i ++){
           g.setColor(Color.PINK);
           g.fillOval(80 + i * 25, 30,18,18);
       }
       player.draw(g);
       for(Platform p : platforms) p.draw(g);
       for(FallingObject o : objects)o.draw(g);
       
      }    
}



//=================== FRAME ====================

public class FallingEscapeGame extends JFrame{
    
    int gamesPlayed = 0;
    int highScore = 0;
    //starts tracking
    
    private final String FILE_NAME = "stats.txt"; //file storage
    
    public FallingEscapeGame(){
        
        loadStatsFromFile(); //loads saved data
        
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new MenuPanel(this));
        setVisible(true);
    }
    public void showMenu(){
        setContentPane(new MenuPanel(this));
        revalidate();
    }
    public void showGame(){
        setContentPane(new GamePanel(this));
        revalidate();
    }
    public void updateStats(int score){ //updates high score and game countm 
        gamesPlayed++;
        if(score > highScore) highScore = score;
    }

// ========= FILE SAVE ================

public void saveStatsToFile(){
    
    try {
        FileWriter writer = new FileWriter(FILE_NAME, false);
        writer.write(gamesPlayed + "\n");
        writer.write(highScore + "\n");
        writer.close();
        
    }catch (IOException e){
        e.printStackTrace();
     }
    }
    
// ======== FILE LOAD =============

private void loadStatsFromFile(){
    
    try{
        File file = new File(FILE_NAME);
        
        if(!file.exists()) return;
        
        BufferedReader reader = 
                new BufferedReader(new FileReader(file));
        
        gamesPlayed = Integer.parseInt(reader.readLine());
        highScore = Integer.parseInt(reader.readLine());
        
        reader.close();
        
    } catch (Exception e){
        e.printStackTrace();
    }
  }
  public void showStatsPopup(){
      
      JDialog d = new JDialog(this, "STATS", true);
      d.setLayout(new BorderLayout());
      
      JTextArea t = new JTextArea(
          "GAMES PLAYED: " + gamesPlayed +
          "\nHIGH SCORE: " + highScore
          );
          
          JButton exit = new JButton("EXIT");
          exit.addActionListener( e -> d.dispose());
          
          d.add(t, BorderLayout.CENTER);
          d.add(exit, BorderLayout.SOUTH);
          
        d.setSize(250,200);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
  }
  
  public void showYouLostPopup(int score){
      
      JDialog d = new JDialog(this, "YOU LOST", true);
      d.setLayout(new BorderLayout());
      
     JTextArea t = new JTextArea("FINAL SCORE: " + score);

    JButton restart = new JButton("RESTART");
    JButton menu = new JButton("MENU");
    
    restart.addActionListener( e -> {
        d.dispose();
        showGame();
    });
    
    menu.addActionListener( e -> {
        d.dispose(); // closes and destroys a window
        showMenu();
    });
    JPanel bottom = new JPanel();
    bottom.add(restart);
    bottom.add(menu);
    
    d.add(t,BorderLayout.CENTER);
    d.add(bottom,BorderLayout.SOUTH);

    d.setSize(250,200);
    d.setLocationRelativeTo(this);
    d.setVisible(true);
  }
  public void showInstructionsPopup(){
      JDialog d = new JDialog(this, "INSTRUCTIONS", true);
      JTextArea t = new JTextArea(
          "LEFT OR RIGHT TO MOVE \n SPACE TO JUMP \n P TO PAUSE"
          );
          JButton exit = new JButton("EXIT");
          exit.addActionListener( e -> d.dispose());
          
          d.add(t, BorderLayout.CENTER);
          d.add(exit, BorderLayout.SOUTH);
          
        d.setSize(250,200);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    public static void main(String[] args){
        new FallingEscapeGame();
    }
}