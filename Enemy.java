import java.awt.image.BufferedImage;

public class Enemy {
    //Enemy's ATTRIBUTES: image and stats
    BufferedImage imgEnemy;
    int intHP;
    int intDMG = 10;
    int intDEF = 10;

    //Initialize the Object's (Enemy) constructor with its parameters:
    public Enemy(BufferedImage imgEnemy, int intHP, int intDMG, int intDEF) {
        this.imgEnemy = imgEnemy;
        this.intHP = intHP;
        this.intDMG = intDMG;
        this.intDEF = intDEF;
    }

    //Below are Getters and Setters of the object:
    // 1) Setters allow us to continuously update the Enemy's attributes.
    // 2) Getters allow us to take the Enemy's current attributes to control the battle engine.
    public BufferedImage getEnemyImage() {
        return this.imgEnemy;
    }

    public void setEnemyImage(BufferedImage imgEnemy) {
        this.imgEnemy = imgEnemy;
    }

    public int getCurrentHP() {
        return this.intHP;
    }

    public void setNewHP(int intHP) {
        this.intHP = intHP;
    }

    public int getCurrentDMG() {
        return this.intDMG;
    }

    public void setNewDMG(int intDMG) {
        this.intDMG = intDMG;
    }

    public int getCurrentDEF() {
        return this.intDEF;
    }

    public void setNewDEF(int intDEF) {
        this.intDEF = intDEF;
    }

}

