import java.awt.image.BufferedImage;

public class Enemy {
    //Hero's'ATTRIBUTES: image, board position, stats -- do image first
    BufferedImage imgEnemy;
    int intHP = 40;
    int intDMG = 10;
    int intDEF = 10;

    //Initialize the Object's (Hero) constructor with its parameters:
    public Enemy(BufferedImage imgEnemy, int intHP, int intDMG, int intDEF) {
        this.imgEnemy = imgEnemy;
        this.intHP = intHP;
        this.intDMG = intDMG;
        this.intDEF = intDEF;
    }

    //NOTE: G&S FOR DMG, DEF MAY NOT BE NEEDED, DEPENDS IF ITEMS TO INCREASE THOSE ARE ADDED
    //Below are Getters and Setters of the object:
    // 1) Setters allow us to continuously update the Hero's attributes.
    // 2) Getters allow us to take the Hero's current attributes to control the game's mechanics.
    public BufferedImage getEnemyImage() {
        return this.imgEnemy;
    }

    public void setEnemyImage(BufferedImage imgEnemy) {
        this.imgEnemy = imgEnemy;
    }

    //Important for healing in buildings and taking damage:
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

