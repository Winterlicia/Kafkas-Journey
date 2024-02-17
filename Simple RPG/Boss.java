import arc.Console;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Boss {
    //"Static-Global" variables, accessible in different classes
    public static boolean blnWonBattle = false;
    public static boolean blnCeruleanAcquired = false;
    
    public static void bossListener(Console con, Hero hero, Enemy enemy, BufferedImage imgBattlefield, BufferedImage imgHero) {
        //Cutscene:
        Main.resetScreen(con);
        con.setDrawColor(Color.WHITE);
        con.drawString("It's time.", 325, 200);
        con.drawString("Destroy Yanqing, Commander of the Cloud Knights.", 110, 225);
        con.drawString("Destroy the Ice Aeon.", 275, 250);
        con.repaint();
        con.sleep(2000);

        con.drawString("Press any key to continue", 250, 300);
        con.repaint();
        con.getKey();

        //Initialize "NonStatic-Global" variables, required throughout the entire function:
        boolean blnValidSelection = false;
        boolean blnHasLightningDOT = false;
        boolean blnIsFrozen = false;
        int intTurn = 0;
        int intHeroEnergy = 0;
        int intBossEnergy = 0;
        BufferedImage imgFrozenSwordHorizontal = con.loadImage("FrozenSwordHorizontal.png");
        BufferedImage imgFrozenSwordVertical = con.loadImage("FrozenSwordVertical.png");

        //Clear the console:
        con.setDrawColor(Color.BLACK);
        con.fillRect(0, 0, 800, 600);
        //Draw the images on the console:
        con.drawImage(imgBattlefield, 0, 0);
        con.drawImage(imgHero, 50, 100);
        con.drawImage(enemy.getEnemyImage(), 425, 75);
        con.repaint();

        //Set up the Battle button. For the Boss Battle, don't give the Player a chance to retreat:
        con.setDrawColor(Color.WHITE);
        con.drawRect(25, 400, 200, 100);
        con.drawString("(B)attle", 75, 425);
        con.repaint();

        //Set Max Energy if the Cerulean flask has been acquired
        if (blnCeruleanAcquired == true) {
            intHeroEnergy = 100;
        }

        //Battle Turn Loop (for Boss Battle):.
        while (true) {
            //Make the player choose 'b' (Battle)
            char chrSelect = con.getChar();
            while (blnValidSelection != true) {
                chrSelect = con.getChar();
                if (chrSelect == 'b') {
                    blnValidSelection = true;
                }
            }

            //Loop the battle:
            while (true) {

                if (chrSelect == 'b') {
                    //Reset the screen to prepare for battle:
                    Main.resetScreen(con);

                    //Display both hero and enemy stats:
                    Main.displayHeroStats(con, hero);
                    Battle.displayEnemyStats(con, enemy);

                    //Initialize variables specific for the battle:
                    final double dblCritRate = 0.3;
                    final double dblHeroCritDMG = 0.7;
                    final double dblBossCritDMG = 0.65;

                    //Display Hero's skill and ultimate for the battle:
                    Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);

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

                        //Check if the Hero is frozen -- if so, skip this turn:
                        if (blnIsFrozen == true) {
                            con.setDrawColor(Color.BLACK);
                            con.drawString("Kafka can't move! Inflicted by Frozen!", 10, 75);
                            con.repaint();
                            con.sleep(2000);

                            //Reset things:
                            Main.resetScreen(con);
                            Main.displayHeroStats(con, hero);
                            Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            Battle.displayEnemyStats(con, enemy);
                            intTurn = 1;
                        }


                        if (blnIsFrozen == false) {

                            //Get the user's input for a skill or ult (only when Hero has 100 Energy):
                            blnValidSelection = false;
                            char chrChoice = con.getChar();
                            while (blnValidSelection != true) {
                                chrChoice = con.getChar();
                                if (chrChoice == 'm' || (chrChoice == 't' && intHeroEnergy == 100)) {
                                    blnValidSelection = true;
                                }
                            }

                            //Animation and Stats of Hero Basic Attack:
                            if (chrChoice == 'm') {
                                BufferedImage imgKatana = con.loadImage("Katana.png");

                                //Animate sword slashing vertically:
                                int intAnimationCount = 0;
                                while (intAnimationCount < 25) { //Enemy Size is 200x200 pixels
                                    //Keep regular battlefield entities:
                                    con.drawImage(imgBattlefield, 0, 0);
                                    con.drawImage(imgHero, 50, 100);
                                    con.drawImage(enemy.getEnemyImage(), 425, 75);
                                    con.drawImage(imgKatana, 420, 80 + 4 * intAnimationCount);
                                    con.repaint();
                                    //Show the entities on screen for a little while
                                    con.sleep(100);
                                    //'Erase' the screen using black rectangle
                                    con.setDrawColor(Color.BLACK);
                                    con.fillRect(0, 0, 960, 600);
                                    //Rinse and repeat:
                                    intAnimationCount++;
                                }

                                //Animate Sword Stabbing the Enemy:
                                intAnimationCount = 0; //Restart Count
                                while (intAnimationCount < 25) {
                                    //Keep regular battlefield entities:
                                    con.drawImage(imgBattlefield, 0, 0);
                                    con.drawImage(imgHero, 50, 100);
                                    con.drawImage(enemy.getEnemyImage(), 425, 75);

                                    con.drawImage(imgKatana, 340 + 4 * intAnimationCount, 160);
                                    con.repaint();
                                    //Show the entities on screen for a little while
                                    con.sleep(100);
                                    //'Erase' the screen using black rectangle
                                    con.setDrawColor(Color.BLACK);
                                    con.fillRect(0, 0, 960, 600);
                                    //Rinse and repeat:
                                    intAnimationCount++;
                                }

                                //At the end of the animation, check for crit hit, update enemy stats:
                                int intDMGDealt = 0;
                                double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                                if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                    intDMGDealt = (int) (hero.getCurrentDMG() + hero.getCurrentDMG() * dblHeroCritDMG);
                                    Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                    con.setDrawColor(Color.BLACK);
                                    con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                                } else {
                                    intDMGDealt = hero.getCurrentDMG();
                                    Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                    con.setDrawColor(Color.BLACK);
                                    con.drawString("Damage: " + String.valueOf(intDMGDealt), 300, 120);
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
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                Battle.displayEnemyStats(con, enemy);
                            }

                            //Animation and Stats of Hero Ultimate Attack:
                            else if (chrChoice == 't' && intHeroEnergy == 100) { //Change condition later after testing
                                BufferedImage imgSymbol = con.loadImage("Symbol.png");
                                BufferedImage imgLightning = con.loadImage("Lightning.png");

                                //Animate ultimate: Make Kafka's symbol appear and drop lightning
                                int intAnimationCount = 0; //Restart Count
                                while (intAnimationCount < 25) {
                                    //Keep regular battlefield entities:
                                    con.drawImage(imgBattlefield, 0, 0);
                                    con.drawImage(imgHero, 50, 100);
                                    con.drawImage(enemy.getEnemyImage(), 425, 75);

                                    //Draw the Symbol, animate lightning drop:
                                    con.drawImage(imgSymbol, 450, 100);
                                    con.drawImage(imgLightning, 475, -130 + 5 * intAnimationCount);
                                    con.repaint();

                                    //Show the entities on screen for a little while
                                    con.sleep(100);
                                    //'Erase' the screen using black rectangle
                                    con.setDrawColor(Color.BLACK);
                                    con.fillRect(0, 0, 960, 600);
                                    //Rinse and repeat:
                                    intAnimationCount++;
                                }

                                //At the end of the animation, check for crit hit, update enemy stats -- Deal 25% extra DMG for an ultimate
                                int intDMGDealt = 0;
                                double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                                if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                    intDMGDealt = (int) (hero.getCurrentDMG() * 1.25 + hero.getCurrentDMG() * 1.25 * dblHeroCritDMG);
                                    Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                    con.setDrawColor(Color.BLACK);
                                    con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                                } else {
                                    intDMGDealt = (int) (hero.getCurrentDMG() * 1.25);
                                    Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                    con.setDrawColor(Color.BLACK);
                                    con.drawString("Damage: " + String.valueOf(intDMGDealt), 300, 120);
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
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                Battle.displayEnemyStats(con, enemy);
                            }

                            //If Boss's HP is 0 or below by the end of the Hero's turn, the Hero has won. Return to Main program and set a Win Game screen:
                            if (enemy.getCurrentHP() <= 0) {
                                con.sleep(1500);
                                blnWonBattle = true;
                                break;
                            }

                            //Switch turns:
                            intTurn++;
                        }

                    } else {
                        //Reset Freeze Status Effect when it gets back to Boss Turn:
                        if (blnIsFrozen = true) {
                            blnIsFrozen = false;
                        }

                        //Check for DOT, take extra DMG at beginning of the Enemy's turn if there is:
                        Battle.checkDOT(con, blnHasLightningDOT, imgBattlefield, imgHero, enemy, hero, 100);
                        blnHasLightningDOT = false;
                        //Reset screen, show new HP as a result of DOT:
                        Main.resetScreen(con);
                        Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                        Main.displayHeroStats(con, hero);
                        Battle.displayEnemyStats(con, enemy);
                        con.sleep(1000);

                        //Check for possible Enemy death from DOT:
                        if (enemy.getCurrentHP() <= 0) {
                            con.sleep(1500);
                            blnWonBattle = true;
                            break;
                        }

                        //Add Boss Skill Animation -- Throw a horizontal sword at Hero
                        if (intBossEnergy != 150) {

                            int intAnimationCount = 0;
                            while (intAnimationCount < 50) {
                                //Keep regular battlefield entities:
                                con.drawImage(imgBattlefield, 0, 0);
                                con.drawImage(imgHero, 50, 100);
                                con.drawImage(enemy.getEnemyImage(), 425, 75);

                                con.drawImage(imgFrozenSwordHorizontal, 380 - 5 * intAnimationCount, 160);
                                con.setDrawColor(Color.WHITE);
                                con.drawString("Yanqing used 'Frost Thorn'!", 60, 415);
                                con.repaint();
                                //Show entities for a little while
                                con.sleep(100);
                                //'Erase' the screen using black rectangle
                                con.setDrawColor(Color.BLACK);
                                con.fillRect(0, 0, 960, 600);
                                intAnimationCount++;
                            }

                            //Calculate Boss DMG Dealt
                            int intDMGDealt = 0;
                            double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                            if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                intDMGDealt = (int) (enemy.getCurrentDMG() + enemy.getCurrentDMG() * dblBossCritDMG);
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.BLACK);
                                con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                            } else {
                                intDMGDealt = hero.getCurrentDMG();
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.BLACK);
                                con.drawString("Damage: " + String.valueOf(intDMGDealt), 300, 120);
                            }
                            con.repaint();
                            con.sleep(1000);

                            //Apply the Attack Formula on the Hero now:
                            hero.setNewHP(hero.getCurrentHP() - intDMGDealt + (int) (0.5 * hero.getCurrentDEF()));

                            intBossEnergy += 50; //Accumulate energy at the end of turn

                            //Display new stats and return to the previous screen at end of turn:
                            Main.resetScreen(con);
                            Main.displayHeroStats(con, hero);
                            Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            Battle.displayEnemyStats(con, enemy);
                        }

                        //Add Boss Ultimate Animation (intEnemyEnergy = 150)
                        else if (intBossEnergy == 150) {
                            BufferedImage imgGlacier = con.loadImage("Glacier.png");

                            int intAnimationCount = 0;
                            while (intAnimationCount < 30) {
                                //Keep regular battlefield entities:
                                con.drawImage(imgBattlefield, 0, 0);
                                con.drawImage(imgHero, 50, 100);
                                con.drawImage(enemy.getEnemyImage(), 425, 75);

                                //Animate the dropping sword
                                con.drawImage(imgFrozenSwordVertical, 100, -80 + 5 * intAnimationCount);
                                con.setDrawColor(Color.WHITE);
                                con.drawString("Yanqing used 'Amidst the One True Sword'!", 30, 415);
                                con.repaint();

                                //Show the entities on screen for a little while
                                con.sleep(100);
                                //'Erase' the screen using black rectangle
                                con.setDrawColor(Color.BLACK);
                                con.fillRect(0, 0, 800, 600);
                                //Rinse and repeat:
                                intAnimationCount++;
                            }
                            //Draw the Glacier formation at the end of the Sword Drop:
                            con.drawImage(imgBattlefield, 0, 0);
                            con.drawImage(imgHero, 50, 100);
                            con.drawImage(enemy.getEnemyImage(), 425, 75);
                            con.drawImage(imgGlacier, 25, 147);
                            con.repaint();
                            con.sleep(1000);

                            //Calculate Boss DMG Dealt
                            int intDMGDealt = 0;
                            double dblCritCheck = Math.round(Math.random() * 10) / 10.0;
                            //For Boss ultimate, deal 10% more damage:
                            if (dblCritCheck <= dblCritRate) { //3 in 10 chance of getting crit hit
                                intDMGDealt = (int) (enemy.getCurrentDMG() * 1.10 + enemy.getCurrentDMG() * 1.10 * dblBossCritDMG);
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.BLACK);
                                con.drawString(String.valueOf(intDMGDealt) + " Critical Hit!", 270, 120);
                            } else {
                                intDMGDealt = hero.getCurrentDMG();
                                Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                                con.setDrawColor(Color.BLACK);
                                con.drawString("Damage: " + String.valueOf(intDMGDealt), 300, 120);
                            }
                            con.repaint();
                            con.sleep(1000);

                            //Set new Hero HP at end of turn:
                            hero.setNewHP(hero.getCurrentHP() - intDMGDealt + (int) (0.5 * hero.getCurrentDEF()));

                            //Set frozen status effect:
                            blnIsFrozen = true;
                            //Reset Energy:
                            intBossEnergy = 0;

                            //Re-render battle screen:
                            Main.resetScreen(con);
                            Main.displayHeroStats(con, hero);
                            Battle.renderBattleScreen(con, imgBattlefield, imgHero, enemy, intHeroEnergy);
                            Battle.displayEnemyStats(con, enemy);
                        }

                        //Reduce back to Player turn
                        intTurn--;
                    }
                }

                //Break from while(true) loop #1:
                if (blnWonBattle == true) {
                    break;
                }

            }

            //Break from while(true) loop #2:
            if (blnWonBattle == true) {
                break;
            }

        }
    }
    
}
