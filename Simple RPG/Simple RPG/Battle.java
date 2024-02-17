import arc.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Battle {
    public static boolean blnWonBattle = false;

    public static void main(String[] args) {
        Console con = new Console("Battle Testing", 800, 600);

        String[] strItems = new String[5];
        Hero hero = new Hero(con.loadImage("Hero.png"), 10, 10, 50, 15, 10, 0, strItems);
        Enemy enemy = new Enemy(con.loadImage("EnemyLarge.png"), 60, 10, 10);
        BufferedImage imgBattlefield = con.loadImage("Battlefield.png");
        BufferedImage imgHeroBattle = con.loadImage("HeroBattle.png");

        battleListener(con, hero, enemy, imgBattlefield, imgHeroBattle);

    }

    //Battle Listener triggers when the hero steps on an "e" tile.
    public static void battleListener(Console con, Hero hero, Enemy enemy, BufferedImage imgBattlefield, BufferedImage imgHero) {

        //Initialize variables:
        boolean blnValidSelection = false;
        BufferedImage imgKatana = con.loadImage("Katana.png");
        BufferedImage imgSlashWave = con.loadImage("SlashWave.png");

        //Clear the console:
        con.setDrawColor(Color.BLACK);
        con.fillRect(0, 0, 800, 600);
        //Draw the images on the console:
        con.drawImage(imgBattlefield, 0, 0);
        con.drawImage(imgHero, 50, 100);
        con.drawImage(enemy.getEnemyImage(), 450, 100);
        con.repaint();

        //Before the battle initiates, we need to see if the player wants to battle or retreat for -10 life:
        con.setDrawColor(Color.WHITE);
        con.drawRect(25, 400, 200, 100);
        con.drawString("(B)attle", 75, 425);

        //Only draw the Retreat button for a normal enemy battle:
        con.drawRect(375, 400, 200, 100);
        con.drawString("(R)etreat", 425, 425);

        con.repaint();

        //Battle Engine Game Loop (for normal enemy):
        while (true) {
            //Ensure that the player's selection is valid:
            char chrChoice = con.getChar();
            while (blnValidSelection != true) {
                chrChoice = con.getChar();
                if (chrChoice == 'b' || chrChoice == 'r') {
                    blnValidSelection = true;
                }
            }

            if (chrChoice == 'r') {
                //If the player chooses to retreat, take away 10 life and reset the Console background
                hero.setNewHP(hero.getCurrentHP() - 10);
                con.setDrawColor(Color.BLACK);
                con.fillRect(0, 0, 800, 600);
                con.repaint();
                break;

            } else if (chrChoice == 'b') {
                //Reset the screen to prepare for battle:
                Main.resetScreen(con);

                //Display both hero and enemy stats:
                Main.displayHeroStats(con, hero);
                displayEnemyStats(con, enemy);

                //Initialize new variables specific for the battle:
                int intHeroEnergy = 0;
                int intTurn = 0;
                final double dblCritRate = 0.3;
                final double dblHeroCritDMG = 0.7;
                final double dblEnemyCritDMG = 0.6;
                boolean blnHasLightningDOT = false;

                //Display Hero's skill and ultimate for the battle:
                renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);

                //Battle Turn Loop:
                while (true) {

                    //Set the hero to go first
                    if (intTurn == 0) {

                        //If Hero's HP is 0 or below, the hero is dead:
                        if (hero.getCurrentHP() <= 0) {
                            Main.deathMenu(con);
                            break;
                        }

                        //Cap the Hero's Max Energy at 100:
                        if (intHeroEnergy >= 100) {
                            intHeroEnergy = 100;
                        }

                        //Get the user's input for a skill or ult (only when Hero has 100 Energy):
                        blnValidSelection = false;
                        chrChoice = con.getChar();
                        while (blnValidSelection != true) {
                            chrChoice = con.getChar();
                            if (chrChoice == 'm' || (chrChoice == 't' && intHeroEnergy == 100)) {
                                blnValidSelection = true;
                            }
                        }

                        //Animation and Stats of Hero Basic Attack:
                        if (chrChoice == 'm') {
                            //Animate sword slashing vertically:
                            int intAnimationCount = 0;
                            while (intAnimationCount < 25) { //Enemy Size is 200x200 pixels
                                //Keep regular battlefield entities:
                                con.drawImage(imgBattlefield, 0, 0);
                                con.drawImage(imgHero, 50, 100);
                                con.drawImage(enemy.getEnemyImage(), 450, 100);
                                con.drawImage(imgKatana, 420, 80 + 4 * intAnimationCount);
                                con.repaint();
                                //Show the entities on screen for a little while
                                con.sleep(100);
                                //'Erase' the screen using black rectangle
                                con.setDrawColor(Color.BLACK);
                                con.fillRect(0, 0, 960, 540);
                                //Rinse and repeat:
                                intAnimationCount++;
                            }

                            //Animate Sword Stabbing the Enemy:
                            intAnimationCount = 0; //Restart Count
                            while (intAnimationCount < 25) {
                                //Keep regular battlefield entities:
                                con.drawImage(imgBattlefield, 0, 0);
                                con.drawImage(imgHero, 50, 100);
                                con.drawImage(enemy.getEnemyImage(), 450, 100);
                                con.drawImage(imgKatana, 340 + 4 * intAnimationCount, 160);
                                con.repaint();
                                //Show the entities on screen for a little while
                                con.sleep(100);
                                //'Erase' the screen using black rectangle
                                con.setDrawColor(Color.BLACK);
                                con.fillRect(0, 0, 960, 540);
                                //Rinse and repeat:
                                intAnimationCount++;
                            }

                            //At the end of the animation, check for crit hit, update enemy stats:
                            int intDMGDealt = 0;
                            double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                            if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                intDMGDealt = (int) (hero.getCurrentDMG() + hero.getCurrentDMG() * dblHeroCritDMG);
                                renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.WHITE);
                                con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                            } else {
                                intDMGDealt = hero.getCurrentDMG();
                                renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.WHITE);
                                con.drawString("Damage: "+String.valueOf(intDMGDealt), 300, 120);
                            }
                            con.repaint();
                            con.sleep(1000);

                            //Attack Formula: When setting new HP, consider Hero DMG dealt and defense:
                            enemy.setNewHP(enemy.getCurrentHP() - intDMGDealt + (int) (0.5 * enemy.getCurrentDEF()));

                            //Accumulate Energy for the Ultimate:
                            intHeroEnergy += 50;

                            //Display new stats and return to the previous screen at end of turn:
                            Main.resetScreen(con);
                            Main.displayHeroStats(con, hero);
                            renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            displayEnemyStats(con, enemy);
                        }

                        //Animation and Stats of Hero Ultimate Attack:
                        else if (chrChoice == 't' && intHeroEnergy == 100) { //Change condition later after testing

                            //Animate ultimate: Make Kafka's symbol appear and drop lightning
                            int intAnimationCount = 0; //Restart Count
                            while (intAnimationCount < 25) {
                                //Keep regular battlefield entities:
                                con.drawImage(imgBattlefield, 0, 0);
                                con.drawImage(imgHero, 50, 100);
                                con.drawImage(enemy.getEnemyImage(), 450, 100);

                                //Draw the Symbol, animate lightning drop:
                                con.drawImage(con.loadImage("Symbol.png"), 450, 100);
                                con.drawImage(con.loadImage("Lightning.png"), 475, -130+5*intAnimationCount);
                                con.repaint();

                                //Show the entities on screen for a little while
                                con.sleep(100);
                                //'Erase' the screen using black rectangle
                                con.setDrawColor(Color.BLACK);
                                con.fillRect(0, 0, 960, 540);
                                //Rinse and repeat:
                                intAnimationCount++;
                            }

                            //At the end of the animation, check for crit hit, update enemy stats -- Deal 25% extra DMG for an ultimate
                            int intDMGDealt = 0;
                            double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                            if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                intDMGDealt = (int) (hero.getCurrentDMG() * 1.25 + hero.getCurrentDMG() * 1.25 * dblHeroCritDMG);
                                renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.WHITE);
                                con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                            } else {
                                intDMGDealt = (int) (hero.getCurrentDMG() * 1.25);
                                renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.WHITE);
                                con.drawString("Damage: "+String.valueOf(intDMGDealt), 300, 120);
                            }
                            con.repaint();
                            con.sleep(1000);

                            //Attack Formula: When setting new HP, consider Hero DMG dealt and defense:
                            enemy.setNewHP(enemy.getCurrentHP() - intDMGDealt + (int) (0.5 * enemy.getCurrentDEF()));

                            //Add a Damage OverTime (DOT) effect on the enemy's next turn:
                            blnHasLightningDOT = true;

                            //Lose all energy after ultimate is used:
                            intHeroEnergy = 0;

                            //Display new stats and return to the previous screen at end of turn:
                            Main.resetScreen(con);
                            Main.displayHeroStats(con, hero);
                            displayEnemyStats(con, enemy);
                        }

                        //If enemy's HP is 0 or below by the end of the Hero's turn, the enemy is dead:
                        if (enemy.getCurrentHP() <= 0) {
                            //Update enemies defeated:
                            hero.setNewEnemiesDefeated(hero.getEnemiesDefeated() + 1);
                            con.sleep(500);
                            winScreen(con, hero);
                            break;
                        }

                        //Switch turns:
                        intTurn++;

                    } else {
                        //Check for DOT, take extra DMG at beginning of the Enemy's turn if there is:
                        checkDOT(con, blnHasLightningDOT, imgBattlefield, imgHero, enemy, hero, 45);
                        blnHasLightningDOT = false; //Set back to "No DOTs"
                        //Reset screen, show new HP as a result of DOT:
                        Main.resetScreen(con);
                        renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                        Main.displayHeroStats(con, hero);
                        displayEnemyStats(con, enemy);
                        con.sleep(1000);

                        //Check for possible Enemy death from DOT:
                        if (enemy.getCurrentHP() <= 0) {
                            //Update enemies defeated:
                            hero.setNewEnemiesDefeated(hero.getEnemiesDefeated() + 1);
                            con.sleep(500);
                            winScreen(con, hero);
                            break;
                        }

                        //What happens when it is the enemy's turn:
                        int intAnimationCount = 0;
                        while (intAnimationCount < 50) { //Hero Size is 138x200 pixels
                            //Keep regular battlefield entities:
                            con.drawImage(imgBattlefield, 0, 0);
                            con.drawImage(imgHero, 50, 100);
                            con.drawImage(enemy.getEnemyImage(), 450, 100);
                            con.drawImage(imgSlashWave, 380 - 5 * intAnimationCount, 160);
                            con.setDrawColor(Color.WHITE);
                            con.drawString("Cloud Knight Patroller used Slash Wave!", 30, 415);
                            con.repaint();
                            //Show the entities on screen for a little while
                            con.sleep(100);
                            //'Erase' the screen using black rectangle
                            con.setDrawColor(Color.BLACK);
                            con.fillRect(0, 0, 960, 540);
                            //Rinse and repeat:
                            intAnimationCount++;
                        }

                        //Calculate Enemy DMG Dealt
                        int intDMGDealt = 0;
                        double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                        if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                            intDMGDealt = (int) (enemy.getCurrentDMG() + enemy.getCurrentDMG() * dblEnemyCritDMG);
                            renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            con.setDrawColor(Color.WHITE);
                            con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                        } else {
                            intDMGDealt = hero.getCurrentDMG();
                            renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            con.setDrawColor(Color.WHITE);
                            con.drawString("Damage: "+String.valueOf(intDMGDealt), 300, 120);
                        }
                        con.repaint();
                        con.sleep(1000);

                        //Apply the Attack Formula on the Hero now:
                        hero.setNewHP(hero.getCurrentHP() - intDMGDealt + (int)(0.5 * hero.getCurrentDEF()));

                        //Just to spice things up: Make the Enemy a bit stronger after every turn the enemy makes:
                        renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                        con.drawString("The enemy gained 1 more stack of 'Enraged'!", 100, 75);
                        enemy.setNewDMG(enemy.getCurrentDMG() + 3);
                        enemy.setNewDEF(enemy.getCurrentDEF() + 3);
                        con.repaint();
                        con.sleep(2000);

                        //Display new stats and return to the previous screen at end of turn:
                        Main.resetScreen(con);
                        renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                        Main.displayHeroStats(con, hero);
                        displayEnemyStats(con, enemy);

                        //Switch back to Hero's turn:
                        intTurn--;
                    }
                }

                if (blnWonBattle == true) {
                    break;
                }
                
            }
        }
    }
    
    public static void displayEnemyStats(Console con, Enemy objEnemy) {
        //Write the HUD Enemy Stats (below the Player stats):
        con.setDrawColor(Color.WHITE);
        con.drawString("ENEMY STATS: ", 650, 200);
        con.drawString("Health: "+objEnemy.getCurrentHP(), 660, 225);
        con.drawString("Attack: "+objEnemy.getCurrentDMG(), 660, 250);
        con.drawString("Defense: "+objEnemy.getCurrentDEF(), 660, 275);
        con.repaint();

        con.setDrawColor(Color.BLACK);
        con.drawRect(650, 200, 250, 400);
        con.repaint();
    }

    public static void renderBattleScreen(Console con, BufferedImage imgBattlefield, BufferedImage imgHero, Enemy enemy, int intHeroEnergy) {
        con.setDrawColor(Color.WHITE);
        con.drawImage(imgBattlefield, 0, 0);
        con.drawImage(imgHero, 50, 100);
        //Enemy x,y-coordinates change based on normal enemy or boss:
        if (enemy.getCurrentDMG() != 20) {
            //Note that the normal enemy's DMG will never hit 20 -- since it goes up by increments of two from Outrage stacks,
            //enemy's DMG will be 10, 13, 16, 19, 22 != 20
            con.drawImage(enemy.getEnemyImage(), 450, 100);
        } else {
            //For the Boss:
            con.drawImage(enemy.getEnemyImage(), 425, 75);
        }
        con.repaint();

        //Set the Hero's Basic Attack:
        con.drawRect(25, 400, 220, 100);
        con.drawString("Basic: ", 30, 415);
        con.drawString("(M)idnight Tumult", 30, 445);
        con.repaint();
        //Set the Hero's Ultimate:
        con.drawOval(360, 400, 150, 150);
        con.drawString("Ult:", 413, 400);
        con.drawString("(T)wilight", 375, 425);
        con.drawString( "Trill", 405, 450);
        con.repaint();
        //Reduce font size:
        //con.loadFont(null, 15);
        con.drawString("Energy: ", 393, 475);
        con.drawString(String.valueOf(intHeroEnergy)+"/100", 400, 500);
        con.repaint();
    }

    //Function to check for Lightning DOT Inflicted:
    public static void checkDOT(Console con, boolean blnHasLightningDOT, BufferedImage imgBattlefield, BufferedImage imgHero, Enemy enemy, Hero hero, int intEnemyMaxHP) {
        if (blnHasLightningDOT == true) {
            con.drawImage(imgBattlefield, 0, 0);
            con.drawImage(imgHero, 50, 100);
            con.drawImage(enemy.getEnemyImage(), 450, 100);
            if (enemy.getCurrentDMG() != 20) { //Normal Enemy
                con.setDrawColor(Color.WHITE);
            } else { //Boss
                con.setDrawColor(Color.BLACK);
            }
            con.drawString("The enemy takes " + String.valueOf((int)(0.3 * intEnemyMaxHP)) + " damage from 'Lightning DOT'!", 100, 140);
            //DOT deals 30% of the enemy's Max HP, ignores enemy DEF:
            enemy.setNewHP(enemy.getCurrentHP() - (int) (0.3 * intEnemyMaxHP));
            con.repaint();
            con.sleep(2500);
        }
    }

    //Function for the Win Screen, when the hero wins a battle:
    public static void winScreen(Console con, Hero hero) {
        Main.resetScreen(con);
        blnWonBattle = true;
        con.setDrawColor(Color.WHITE);
        con.drawString("Battle Won!", 325, 200);
        //Add items using the Hero class setter:
        if (hero.getEnemiesDefeated() == 1 || hero.getEnemiesDefeated() == 3 || hero.getEnemiesDefeated() == 5) {
            //Add a key item at 1, 3, 5 enemies defeated:
            hero.setNewItem("Key");
            con.drawString("Obtained 'Key'!", 300, 225);
            con.drawImage(con.loadImage("Key.png"), 225, 260);
            con.repaint();
            //Show for a little while:
            con.sleep(2500);

            con.drawString("Obtained 'Key'!", 300, 225);
            con.drawImage(con.loadImage("Key.png"), 225, 260);
            con.drawString("Press any key to continue", 250, 500);
            con.repaint();
            con.getKey();
        } else if (hero.getEnemiesDefeated() == 2) {
            //Add a weapon item at 2 enemies defeated:
            hero.setNewItem("Sword");
            con.drawString("Obtained 'Sword'!", 300, 225);
            con.drawImage(con.loadImage("Sword.png"), 300, 260);
            con.repaint();
            //Show for a little while:
            con.sleep(2500);

            con.drawString("Obtained 'Sword'!", 300, 225);
            con.drawImage(con.loadImage("Sword.png"), 300, 260);
            con.drawString("Press any key to continue", 250, 500);
            con.repaint();

            //Update Hero DMG stats:
            hero.setNewDMG(hero.getCurrentDMG() + 10);
            con.getKey();
        } else if (hero.getEnemiesDefeated() == 4) {
            //Add a weapon item at 4 enemies defeated:
            hero.setNewItem("Shield");
            con.drawString("Obtained 'Shield'!", 300, 225);
            con.drawImage(con.loadImage("Shield.png"), 300, 260);
            con.repaint();
            //Show for a little while:
            con.sleep(2500);

            con.drawString("Obtained 'Shield'!", 300, 225);
            con.drawImage(con.loadImage("Shield.png"), 300, 260);
            con.drawString("Press any key to continue", 250, 500);
            con.repaint();

            //Update Hero DEF Stats:
            hero.setNewDEF(hero.getCurrentDEF() + 10);
            con.getKey();
        }

        Main.resetScreen(con);
    }
}
