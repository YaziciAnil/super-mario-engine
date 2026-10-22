//Batuhan Özgür
//2024400150
import java.util.ArrayList;

public class Level {
    private int id;//number of levels
    private ArrayList<Enemy> enemies;//list of enemies
    private String clue;//clue text at the bottom
    private int[][] coins;//datas of coins regarding coordinates and sizes
    //constructor
    public Level(int id, ArrayList<Enemy> enemies, String clue, int[][] coins) {
        this.id =id;
        this.enemies = enemies;
        this.clue = clue;
        this.coins = coins;
    }
    //Getters
    public int getId() {return id;}
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
    public String getClue() {
        return clue;
    }
    public int[][] getCoins() {
        return coins;
    }
}
