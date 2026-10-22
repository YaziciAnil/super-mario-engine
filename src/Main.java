//Batuhan Özgür
//2024400150

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int width = 800;
        final int height = 800;

        final int cameraModeW = 400;
        final int cameraModeH = 400;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setTitle("Super Mario Bros.");
        StdDraw.enableDoubleBuffering();
        String clue0 = "Go to the exit pipe!";
        String clue1 = "Do not mess with mushrooms!";
        String clue2 = "Do not forget to collect all the coins!";

        Color skyColor = new Color(146, 144, 255);
        Color mainMenuColor0 = new Color(153, 78, 0);
        Color mainMenuColor1 = new Color(100, 50, 0);
        Color mainMenuColor2 = new Color(255, 204, 197);
        Color marioDarkBlue = new Color(32, 56, 136);
        Color marioRed = new Color(228, 0, 18);
        Color marioGold = new Color(255, 183, 0);
        Color marioLightBlue = new Color(107, 140, 255);
        int[][] obstacles = readCoords("data/obstacles.txt");
        int[][] pipes = readCoords("data/pipes.txt");
        int[][] portals = readCoords("data/portals.txt");
        int[][] coins = readCoords("data/coins.txt");
        ArrayList<Level> levels = new ArrayList<>();
        Map map = new Map(obstacles,pipes,portals,coins);
        levels.add(new Level(1, new ArrayList<>(),clue0, new int[0][0]));
        //we are adjusting level 1 enemies
        ArrayList<Enemy> level2Enemies = new ArrayList<>();
        level2Enemies.add(new Enemy(340, 580, 40, 1, 260, 420, false));
        level2Enemies.add(new Enemy(440, 300, 40, 1, 380, 500, false));
        //filling levels with clues,coins,enemies
        levels.add(new Level(2, level2Enemies, clue1, new int[0][0]));
        //level3 enemies
        ArrayList<Enemy> level3Enemies = new ArrayList<>();
        level3Enemies.add(new Enemy(340, 580, 40, 1, 260, 420, false));
        level3Enemies.add(new Enemy(440, 300, 40, 1, 380, 500, false));
        //filling levels with clues,coins,enemies
        levels.add(new Level(3, level3Enemies, clue2, coins));
        //level4 enemies
        ArrayList<Enemy> level4Enemies = new ArrayList<>();
        level4Enemies.add(new Enemy(280, 580, 40, 1, 240, 440, true));
        level4Enemies.add(new Enemy(400, 620, 40, -1, 450, 600, true));
        level4Enemies.add(new Enemy(260, 260, 40, -1, 260, 360, true));
        level4Enemies.add(new Enemy(360, 300, 40, 1, 370, 520, true));
        //filling levels with clues,coins,enemies
        levels.add(new Level(4, level4Enemies, "Watch out for jumping enemies!", new int[0][0]));
        Game game = new Game(levels, map);
        game.run();

    }
    public static int[][] readCoords(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            ArrayList<int[]> list = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                //skipping empty rows
                if (line.trim().isEmpty()) continue;
                //split datas with commas
                String[] parts = line.split(",");
                int[] row = new int[4];
                //saving datas with commas
                row[0] = (int) Double.parseDouble(parts[0].trim());
                row[1] = (int) Double.parseDouble(parts[1].trim());
                row[2] = (int) Double.parseDouble(parts[2].trim());
                row[3] = (int) Double.parseDouble(parts[3].trim());
                list.add(row);
            }
            sc.close();

            return list.toArray(new int[0][0]);
        } catch (Exception e) {
            //empty array if there is a problem
            return new int[0][0];
        }
    }
}