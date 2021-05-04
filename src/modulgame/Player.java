//Saya Khamidah Ahmad Syauqi mengerjakan evaluasi TP5 dalam mata kuliah DPBO untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan.Aamiin.

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Fauzan
 */
public class Player extends GameObject{
    private boolean bot = false;
    public Player(int x, int y, ID id){
        super(x, y, id);
        //speed = 1;
    }

    public Player(int x, int y, ID id, boolean bot){
        super(x, y, id);
        this.bot = bot;
        //speed = 1;
    }

    @Override
    public void tick() {
        x += vel_x;
        y += vel_y;
        
        x = Game.clamp(x, 0, Game.WIDTH - 60);
        y = Game.clamp(y, 0, Game.HEIGHT - 80);

    }

    @Override
    public void render(Graphics g) {
        if(bot) g.setColor(Color.decode("#823f3f"));
        else g.setColor(Color.decode("#3f6082"));
        g.fillRect(x, y, 50, 50);
    }
}
