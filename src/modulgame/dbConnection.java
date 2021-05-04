//Saya Khamidah Ahmad Syauqi mengerjakan evaluasi TP5 dalam mata kuliah DPBO untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan.Aamiin.

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Fauzan
 */
public class dbConnection {
    public static Connection con;
    public static Statement stm;
    
    public void connect(){//untuk membuka koneksi ke database
        try {
            String url ="jdbc:mysql://localhost/db_gamepbo";
            String user="root";
            String pass="";
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (Exception e) {
            System.err.println("koneksi gagal" +e.getMessage());
        }
    }

    public void addHS(String username, int score, int waktu){
        connect();
        String sql = "SELECT * FROM highscore where Username = '" + username + "'";
        try {
            // cek apakah usernamenya sudah ada di database
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_FORWARD_ONLY);
            ResultSet res = stm.executeQuery(sql);
            res.first();
            if((score + waktu) > (res.getInt("Score") + res.getInt("Score"))){
                // perbarui score
                sql = "UPDATE highscore set Score = " + score + ", waktu = "+ waktu +" WHERE id = " + res.getInt("id");
                stm.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            // jika usernamenya belum ada di database
            sql = "INSERT INTO highscore (Username, Score, Waktu) VALUES ('" + username + "', " + score + ", " + waktu + ")";
            try {
                stm.executeUpdate(sql);
            } catch (SQLException ep) {
                System.err.println("Gagal menyimpan data ke database" +ep.getMessage());
            }
        }
    }
    
    public DefaultTableModel readTable(){
        
        DefaultTableModel dataTabel = null;
        try{
            Object[] column = {"No", "Username", "Score", "Waktu", "Total"};
            connect();
            dataTabel = new DefaultTableModel(null, column);
            String sql = "SELECT * FROM highscore ORDER BY (Score + Waktu) DESC";
            ResultSet res = stm.executeQuery(sql);
            
            int no = 1;
            while(res.next()){
                Object[] hasil = new Object[5];
                hasil[0] = no;
                hasil[1] = res.getString("Username");
                hasil[2] = res.getInt("Score");
                hasil[3] = res.getInt("Waktu");
                hasil[4] = res.getInt("Score")+res.getInt("Waktu");
                no++;
                dataTabel.addRow(hasil);
            }
        }catch(Exception e){
            System.err.println("Read gagal " +e.getMessage());
        }
        
        return dataTabel;
    }
}
