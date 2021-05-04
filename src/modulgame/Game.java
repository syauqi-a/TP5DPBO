//Saya Khamidah Ahmad Syauqi mengerjakan evaluasi TP5 dalam mata kuliah DPBO untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan.Aamiin.

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Random;

/**
 *
 * @author Fauzan
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private int score = 0;
    private int scoreTime = 0;
    
    private int time = 10;
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;
    private String username = "";

    AutoBot oAutoBot;
    int botSpeed = 5;
    int newBot = 50; // tambahkan bot baru setiap 50 detik
    
    dbConnection dbcon = new dbConnection();

    private Clip clipBG;
    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(String username, String kesulitan){
        this.username = username;
        if(kesulitan =="Easy"){
            this.botSpeed = 2;
            this.time = 20;
            this.newBot = 60;
        }
        else if(kesulitan =="Hard"){
            this.botSpeed = 8;
            this.time = 5;
            this.newBot = 45;
        }
        window = new Window(WIDTH, HEIGHT, "Tugas praktikum 5 DPBO - " + kesulitan, this);
        
        handler = new Handler();
        
        this.addKeyListener(new KeyInput(handler, this));
        oAutoBot = new AutoBot(handler, this, 1);
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));
            handler.addObject(new Player(200,200, ID.Player));
            handler.addObject(new Player(736,520, ID.Bot, true));
        }
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int count = 0;
        playBG("/bgm_Lost_Saga.wav");
        this.oAutoBot = new AutoBot(handler, this, botSpeed);
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                if(count > 30/botSpeed){ // tambahkan musuh baru :v
                    // create instance of Random class
                    Random rand = new Random();
                    int randX = rand.nextInt(745);
                    int randY = rand.nextInt(550);
                    handler.addObject(new Player(randX, randY, ID.Bot, true)); // banyak bot lebih baik kang :D
                    System.out.println("New bots have appeared on (" + randX + ", " + randY + ")");
                    count = 0;
                }
                else
                    count++;
                //System.out.println("FPS: " + frames);
                oAutoBot.move(); // panggil method untuk menggerakkan bot secara otomatis
                frames = 0;
                if(gameState == STATE.Game){
                    if(time>0){
                        time--;
                        scoreTime++;
                    }else{
                        stopBG();
                        gameState = STATE.GameOver;
                        dbcon.addHS(username, score, scoreTime);
                    }
                }
            }
        }
        stop();
    }
    
    private void tick(){
        handler.tick();
        if(gameState == STATE.Game){
            GameObject playerObject = null;
            GameObject botObject = null;
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player){
                   playerObject = handler.object.get(i);
                }
            }
            if(playerObject != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Bot){
                        if(checkEnemy(playerObject, handler.object.get(i))){
                            // permainan berakhir karena player menyentuh musuh
                            gameState = STATE.GameOver;
                            stopBG();
                            dbcon.addHS(username, score, scoreTime);
                        }
                    }
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));

                            // create instance of Random class
                            Random rand = new Random();

                            score += 2 + rand.nextInt(14);
                            time += 1 + rand.nextInt(8);
                            break;
                        }
                    }
                }
                int item=0;
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item) item++;
                }
                if(item == 0){ // jika item habis
                    // create instance of Random class
                    Random rand = new Random();
                    for(int j=0; j<2; j++){
                        // Generate random integers
                        int randX = rand.nextInt(745);
                        int randY = rand.nextInt(550);
                        handler.addObject(new Items(randX, randY, ID.Item));
                    }
                }
            }
        }
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    // Cek musuh
    public static boolean checkEnemy(GameObject player, GameObject enemy){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeEnemy = 45;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int enemyLeft = enemy.x;
        int enemyRight = enemy.x + sizeEnemy;
        int enemyTop = enemy.y;
        int enemyBottom = enemy.y + sizeEnemy;
        
        if((playerRight > enemyLeft ) &&
        (playerLeft < enemyRight) &&
        (enemyBottom > playerTop) &&
        (enemyTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
                
        
        
        if(gameState ==  STATE.Game){
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Total Score: " +Integer.toString(score+scoreTime), WIDTH/2 - 80, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }

        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    
    public void playBG(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clipBG = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clipBG.open(audioIn);
            clipBG.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }

    public void stopBG(){
        clipBG.stop();
    }
}
