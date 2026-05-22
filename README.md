Project Introduction / Overview:
This game is called Avoid It! It is a survival game, the player must dodge falling objects while jumping up from
platform to platform. The player has three lives in the form of hearts, and the game keeps on going until they lose all their lives.
The player must avoid falling objects and survive as long as they can, and if they do, they gain points. As the game progresses and
the score increases, the game will become harder as the falling objects move faster.

What the program does:
Lets the player move left and right using keys and jump as well.
Also lets the player press buttons to look at intstrcutions and stats and also move 
between different panels of the game.

How to run the program:
Firstly, the main game panel shows up. The player gets three choices: Play, Stats, Instructions. The player can click 
instructions button to learn how to play the game or go straight into to game if they wish by choosing play. Once the game starts,
the player keeps on going until they lose all three hearts. Once the game is over, a game over text pops up along with the score. The player can 
then click the menu button and it takes you back to the menu. After the player had played multiple games, they are free to check the stats panel on the menu panel
to check their high score, games played, and lives.


Project Goals and purpose:
The goal of this project is incorporate forms of GUI applications into the code to 
create a game that works efficiently.
The purpose of this game is to reach the highest score the player can possibly achieve.

Any additional notes or documentation links (references, etc)
https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html 




       y = -50;
       x = new Random().nextInt(760);
   }
   @Override
   public void draw(Graphics g){
       g.setColor(Color.RED);
       g.fillOval(x, y, width, height);
   }
}
// ================= PLAYER CLASS =================
class Player extends GameObject {
    private int velY = 0;
    private boolean jumping = false;

    public Player(int x, int y) {
        super(x, y, 40,40);
    }
    public void moveLeft() {
        x -= 6;
        if( x < 0) x = 0;
    }
    public void moveRight() {
        x += 6;
        if( x + width > 800) x = 800 - width;
    }
    public void jump() {
       if(!jumping){
        velY = -15;
        jumping = true;
    }
}  
      
    public void update(){
        y += velY;
        velY += 1;
        
    
        if(velY > 10) velY = 10;
        
        if(y >= 520){
            y = 520;
            velY = 0;
            jumping = false;
        }
    }
    public int getVelY(){
        return velY;
    }
    public void landOn(int platformY){
        y = platformY - height;
        velY = 0;
        jumping = false;
    }
    public void reset(){
        x = 300;
        y = 520;
        velY = 0;
        jumping = false;
    }
        @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
// ================= MENU =================
class MenuPanel extends JPanel implements ActionListener {

    FallingEscapeGame frame;

    JButton play = new JButton("PLAY");
    JButton instructions = new JButton("INSTRUCTIONS");
    JButton stats = new JButton("STATS");

    public MenuPanel(FallingEscapeGame frame) {

        this.frame = frame;

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

        play.addActionListener(this);
        instructions.addActionListener(this);
        stats.addActionListener(this);

        panel.add(title);
        panel.add(play);
        panel.add(instructions);
        panel.add(stats);

        add(panel);
    }

    private void style(JButton b) {
        b.setPreferredSize(new Dimension(120, 25));
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(Color.LIGHT_GRAY);
        b.setFocusPainted(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == play) frame.showGame();
        if (e.getSource() == instructions) frame.showInstructionsPopup();
        if (e.getSource() == stats) frame.showStatsPopup();
    }
}

// ================= GAME PANEL =================
class GamePanel extends JPanel implements ActionListener {
    
    Player player;
    ArrayList<Platform> platforms = new ArrayList<>();
    ArrayList<FallingObject> objects = new ArrayList<>();
    
    Timer timer;
    boolean left, right;
    boolean paused;
    int score = 0;
    int lives = 3;
    
    FallingEscapeGame frame;
    Random r = new Random();
    
    public GamePanel(FallingEscapeGame frame){
        this.frame = frame;
        setBackground(Color.BLACK);
        
        player = new Player(300,520);
    
        for(int i = 0; i < 10; i++)
        platforms.add(new Platform(r.nextInt(650), 100 + i *60));
        
        for(int i = 0; i < 5 ; i ++)
        objects.add(new FallingObject(r.nextInt(760), r.nextInt(400)));
        
        setupKeys();
        
        timer = new Timer(20, this);
        timer.start();
    }
    
    private void setupKeys(){
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "left");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftOff");
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightOff");
        im.put(KeyStroke.getKeyStroke("SPACE"), "jump");
        im.put(KeyStroke.getKeyStroke("pressed P"), "pause");
        
        am.put("left", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                left = true;
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
            }
          });
    }
    
    public void actionPerformed(ActionEvent e){
        
        if(!paused){
            
         player.update();
        
        if(left) player.moveLeft();
        if(right) player.moveRight();
        
        Rectangle feet = new Rectangle(player.x, player.y + player.height, player.width, 5);
        
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
        
        for(FallingObject o: objects){
            o.move();
            
            if(o.y > 600){
                o.reset();
                score += 10;
            }
            if(player.getBounds().intersects(o.getBounds())){
                o.reset();
                lives--;
                
                if(lives <= 0){
                    timer.stop();
                    frame.updateStats(score);
                    frame.saveStatsToFile();
                    frame.showYouLostPopup(score);
                }
            }
        }
    }
        repaint();
    }
    
      protected void paintComponent(Graphics g){
        super.paintComponent(g);

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
    
    private final String FILE_NAME = "stats.txt";
    
    public FallingEscapeGame(){
        
        loadStatsFromFile();
        
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
    public void updateStats(int score){
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
        d.dispose();
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
