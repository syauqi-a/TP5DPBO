//Saya Khamidah Ahmad Syauqi mengerjakan evaluasi TP5 dalam mata kuliah DPBO untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan.Aamiin.

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import modulgame.Game.STATE;
import java.util.Random;

/**
 *
 * @author Fauzan
 */
public class AutoBot{
    
    private Handler handler;
    Game game;
    int speed = 1;
    
    // konstruktor
    public AutoBot(Handler handler, Game game, int speed){
        this.game = game;
        this.handler = handler;
        this.speed = speed;
    }

    // method untuk menggerakkan bot secara otomatis
    public void move(){
        for(int i = 0;i<handler.object.size();i++){
            GameObject tempObject = handler.object.get(i);

            if(tempObject.getId() == ID.Bot){
                String[] arrMove = {"up", "left", "down", "right"};
                Random r = new Random();
                // generate random move
                int randomMove = r.nextInt(100)%4;
                if((randomMove==0 && (handler.object.get(i).getY()-speed) < 30) ||
                        (randomMove==1 && (handler.object.get(i).getX()-speed)< 30) ||
                        (randomMove==2 && (handler.object.get(i).getY()+speed)> 480) ||
                        (randomMove==3 && (handler.object.get(i).getX()+speed)> 695))
                    randomMove = (randomMove+2)%4;
                    

                //System.out.println("move: " + arrMove[randomMove]);
                if(arrMove[randomMove] == "up")
                    tempObject.setVel_y(-1*speed);

                else if(arrMove[randomMove] == "left")
                    tempObject.setVel_x(-1*speed);

                else if(arrMove[randomMove] == "down")
                    tempObject.setVel_y(+1*speed);

                else if(arrMove[randomMove] == "right")
                    tempObject.setVel_x(+1*speed);
            }
        }
    }
}
