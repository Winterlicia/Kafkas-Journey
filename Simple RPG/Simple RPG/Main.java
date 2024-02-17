import arc.*;

import java.awt.*;
import java.util.Arrays;
import java.awt.image.BufferedImage;

/* NOTES:
 * Could maybe integrate items into the game by making it so that the user would gain items after killing certain number of enemies
 * Using above idea, it is possible to make a boss battle fight that can only be won after the user kills enough enemies for the items to help
 * 
 * 
 */ 

public class Main {
	public static void main(String[] args) {
		Console con = new Console("Kafka's Journey", 800, 600); //REMEMBER TO CHANGE THIS BACK TO 20X20 PIXELS

		//Initialize the map(s)
		String[][] strMap = new String[20][20];
		TextInputFile map = new TextInputFile("map.csv");
		int intLevel = 1; //Level 1 = Activate Regular Map, Level 2 = Activate Boss Map

		//Initialize the images to be used:
		BufferedImage imgGrass = con.loadImage("Grass.jpg");
		BufferedImage imgTree = con.loadImage("TreeTest.png");
		BufferedImage imgWater = con.loadImage("Water.png");
		BufferedImage imgBuilding = con.loadImage("Building.png");
		BufferedImage imgHero = con.loadImage("Hero.png");
		BufferedImage imgEnemy = con.loadImage("EnemySmall.png");
		BufferedImage imgBattlefield = con.loadImage("Battlefield.png");
		BufferedImage imgHeroBattle = con.loadImage("HeroBattle.png");
		BufferedImage imgEnemyBattle = con.loadImage("EnemyLarge.png");
		BufferedImage imgDoor = con.loadImage("Door.png");

		//Initiate the Start Menu at the beginning:
		startMenu(con);

		//Load the map using the functions:
		strMap = loadMap(map);
		renderMap(con, strMap, imgGrass, imgTree, imgBuilding, imgWater, imgEnemy, imgDoor);

		//Initialize a new instance of the "Hero" object for the player:
		String[] strItems = new String[5];
		Hero hero = new Hero(imgHero, 19, 10, 50, 15, 10, 0, strItems);

		//Get rid of any text before running the main game loop:
		resetScreen(con);

		//Main game loop:
		while (intLevel == 1) {
			resetScreen(con);
			//Display the character, stats, etc. Should always be actively updating because of the while true loop.
			renderMap(con, strMap, imgGrass, imgTree, imgBuilding, imgWater, imgEnemy, imgDoor);
			displayHeroStats(con, hero);
			displayHeroItems(con, hero.getItemList());
			//Note that row = y-axis, col = x-axis
			con.drawImage(hero.getHeroImage(), hero.getCurrentColPosition() * 30, hero.getCurrentRowPosition() * 30);
			con.repaint();

			//Get the current key being pressed and control movement of the hero with it:
			char chrCurrentKey = con.getChar();

			//Move the hero based on the realtime character input:
			//Going Up: Check y-axis - 1 in canMove function
			if (chrCurrentKey == 'w' && hero.canMove(hero.getCurrentRowPosition() - 1, hero.getCurrentColPosition(), strMap, "vertical")) {
				hero.setNewRowPosition(hero.getCurrentRowPosition() - 1);
			}

			//Going Left: Check x-axis - 1 in canMove function
			else if (chrCurrentKey == 'a' && hero.canMove(hero.getCurrentRowPosition(), hero.getCurrentColPosition() - 1, strMap, "horizontal")) {
				hero.setNewColPosition(hero.getCurrentColPosition() - 1);
			}

			//Going Down: Check y-axis + 1 in canMove function
			else if (chrCurrentKey == 's' && hero.canMove(hero.getCurrentRowPosition() + 1, hero.getCurrentColPosition(), strMap, "vertical")) {
				hero.setNewRowPosition(hero.getCurrentRowPosition() + 1);
			}

			//Going Right: Check x-axis + 1 in canMove function
			else if (chrCurrentKey == 'd' && hero.canMove(hero.getCurrentRowPosition(), hero.getCurrentColPosition() + 1, strMap, "horizontal")) {
				hero.setNewColPosition(hero.getCurrentColPosition() + 1);
			}

			//Check if the player hits water -- in this case, set the hero's HP to 0.
			if (strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("w")) {
				hero.setNewHP(0);
			}

			//Check if the player enters a building -- in this case, increase the hero's HP by 10.
			if (strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("b")) {
				//Cap the HP at 50:
				if (hero.getCurrentHP() >= 50) {
					hero.setNewHP(50);
				} else {
					hero.setNewHP(hero.getCurrentHP() + 10);
					//Repeat check to ensure HP cap, e.g: exceptions like HP = 43:
					if (hero.getCurrentHP() >= 50) {
						hero.setNewHP(50);
					}
				}
			}

			//Check when the player encounters an enemy:
			if (strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("e")) {
				//Initialize a new instance of the "Enemy" object for the enemy, everytime "e" is called:
				Enemy enemy = new Enemy(imgEnemyBattle, 45, 10, 10);

				//Call the battle functions (do later)
				Battle.battleListener(con, hero, enemy, imgBattlefield, imgHeroBattle);
				//Check if the player has won -- if yes, get rid of that enemy and replace it with a grass block:
				if (Battle.blnWonBattle == true) {
					strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()] = "g";
					Battle.blnWonBattle = false; //Set back to false, reset next battle
				} else {
					//If it gets to here, then the player has retreated.
					//Need to fix and issue where the player's image continues to be in the tile after retreating -- refresh the tile:
					con.setDrawColor(Color.BLACK);
					con.drawRect(hero.getCurrentColPosition()*30, hero.getCurrentRowPosition()*30, 30, 30);
					con.repaint();

					con.drawImage(imgEnemy,hero.getCurrentColPosition()*30, hero.getCurrentRowPosition()*30);
				}
			}

			//Activate a mystery door when the Hero collects all three keys (when he defeats all five enemies):
			if (hero.getEnemiesDefeated() == 5) {
				//Clear Hero's keys to open the door:
				hero.strItems[0] = "";
				hero.strItems[2] = "";
				hero.strItems[4] = "";
				//Rearrange item list now that Keys are gone:
				hero.strItems[0] = "Sword";
				hero.strItems[1] = "Shield";
				for (int i = 2; i < 4; i++) {
					hero.strItems[i] = null;
				}

				//Open up the tree-blocked area in the middle of the map and add doors to the map:
				strMap[8][9] = "g";
				strMap[8][10] = "g";
				strMap[11][9] = "d";
				strMap[11][10] = "d";

				//Inform the Player of the new door:
				resetScreen(con);
				con.setDrawColor(Color.WHITE);
				con.drawString("A mysterious door opened.", 250, 200);
				con.drawString("As Kafka approaches the door, it becomes colder and colder.", 30, 225);
				con.sleep(3000);

				con.drawString("Press any key to continue", 250, 400);
				con.repaint();
				con.getKey();

				//Set enemies defeated back to zero to ensure this if statement does not trigger again:
				hero.setNewEnemiesDefeated(0);
			}

			//Event listener for the door -- break and start a new game loop for the boss room:
			if (strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("d")) {
				intLevel = 2;
				break;
			}

			//If the player dies, the game is over. (Death when hero HP = 0):
			if (hero.getCurrentHP() <= 0) {
				deathMenu(con);
			}

		}

	//Boss Map Game Loop:

		//Initialize required variables:
		String[][] strBossMap = new String[20][20];
		TextInputFile bossmap = new TextInputFile("bossmap.csv");
		strBossMap = loadMap(bossmap);
		System.out.println(Arrays.deepToString(strBossMap));

		BufferedImage imgIce = con.loadImage("Ice.png"); //The ice will become the grass
		BufferedImage imgFrozenTree = con.loadImage("FrozenTree.png");
		BufferedImage imgBossSmall = con.loadImage("BossSmall.png"); //Used for Map
		BufferedImage imgBossLarge = con.loadImage("BossLarge.png"); //Used for Boss Battle
		BufferedImage imgMountainBattlefield = con.loadImage("MountainBattlefield.jpg"); //New background for boss battle

		//"Level" up the hero
		resetScreen(con);
		con.setDrawColor(Color.WHITE);
		con.drawString("Entered a new area. Gained 15 Max HP!", 200, 225);
		con.repaint();
		con.sleep(2000);
		hero.setNewHP(hero.getCurrentHP() + 15);

		//Reset to Hero's Starting Position:
		hero.setNewRowPosition(19);
		hero.setNewColPosition(9);

		//Get rid of any text before running the Boss game loop:
		resetScreen(con);

		while (intLevel == 2) {
			//Render Map with new entities:
			renderMap(con, strBossMap, imgIce, imgFrozenTree, imgBuilding, imgWater, imgBossSmall, imgDoor);
			displayHeroStats(con, hero);
			displayHeroItems(con, hero.getItemList());
			//Note that row = y-axis, col = x-axis
			con.drawImage(hero.getHeroImage(), hero.getCurrentColPosition() * 30, hero.getCurrentRowPosition() * 30);
			con.repaint();

			//Get the current key being pressed and control movement of the hero with it:
			char chrCurrentKey = con.getChar();

			//Move the hero based on the realtime character input:
			//Going Up: Check y-axis - 1 in canMove function
			if (chrCurrentKey == 'w' && hero.canMove(hero.getCurrentRowPosition() - 1, hero.getCurrentColPosition(), strMap, "vertical")) {
				hero.setNewRowPosition(hero.getCurrentRowPosition() - 1);
			}

			//Going Left: Check x-axis - 1 in canMove function
			else if (chrCurrentKey == 'a' && hero.canMove(hero.getCurrentRowPosition(), hero.getCurrentColPosition() - 1, strMap, "horizontal")) {
				hero.setNewColPosition(hero.getCurrentColPosition() - 1);
			}

			//Going Down: Check y-axis + 1 in canMove function
			else if (chrCurrentKey == 's' && hero.canMove(hero.getCurrentRowPosition() + 1, hero.getCurrentColPosition(), strMap, "vertical")) {
				hero.setNewRowPosition(hero.getCurrentRowPosition() + 1);
			}

			//Going Right: Check x-axis + 1 in canMove function
			else if (chrCurrentKey == 'd' && hero.canMove(hero.getCurrentRowPosition(), hero.getCurrentColPosition() + 1, strMap, "horizontal")) {
				hero.setNewColPosition(hero.getCurrentColPosition() + 1);
			}

			//Check for Boss Battle:
			if (strBossMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("e")) {
				Enemy boss = new Enemy(imgBossLarge, 150, 20, 15);

				Boss.bossListener(con, hero, boss, imgMountainBattlefield, imgHeroBattle);

				if (Boss.blnWonBattle == true) {
					winGameMenu(con);
				}
			}

			//Check for item pickups:
			if (strBossMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("hp")) {
				//What happens when Flask of Crimson Tears is acquired:
				strBossMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()] = "g";

				//Notification message:
				resetScreen(con);
				con.setDrawColor(Color.WHITE);
				con.drawString("Obtained 'Flask of Crimson Tears'. Gained 20 Max HP!", 100, 225);
				con.repaint();
				con.sleep(2000);
				hero.setNewHP(hero.getCurrentHP() + 20);
				for (int i = 0; i < hero.strItems.length; i++) {
					if (hero.strItems[i] == null) {
						hero.strItems[i] = "Crimson";
						break;
					}
				}
			}

			if (strBossMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()].equals("fp")) {
				//What happens when Flask of Cerulean Tears is acquired:
				strBossMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()] = "g";

				//Notification message:
				resetScreen(con);
				con.setDrawColor(Color.WHITE);
				con.drawString("Obtained 'Flask of Cerulean Tears'.", 180, 225);
				con.drawString("Gained Full Energy for next battle!", 180, 250);
				con.repaint();
				con.sleep(2000);
				Boss.blnCeruleanAcquired = true;
				for (int i = 0; i < hero.strItems.length; i++) {
					if (hero.strItems[i] == null) {
						hero.strItems[i] = "Cerulean";
						break;
					}
				}
			}

			//If the player dies, the game is over. (Death when hero HP = 0):
			if (hero.getCurrentHP() <= 0) {
				deathMenu(con);
				break;
			}

		}

	}

	//Function to load the map from the .csv file data:
	public static String[][] loadMap(TextInputFile mapFile) {

		String[] strSplit;
		String[][] strMap = new String[20][20];

		//Initialize the values of the map based on the csv file.
		for (int row = 0; row < 20; row++) {
			String strLine = mapFile.readLine();
			strSplit = strLine.split(",");

			for (int col = 0; col < 20; col++) {
				strMap[row][col] = strSplit[col];
				System.out.println(Arrays.deepToString(strMap));
			}
		}

		mapFile.close();
		return strMap;
	}

	//Function to render the map, adding the grass, tree, building, water blocks, etc:
	public static void renderMap(Console con, String[][] strMap, BufferedImage imgGrass, BufferedImage imgTree, BufferedImage imgBuilding, BufferedImage imgWater, BufferedImage imgEnemy, BufferedImage imgDoor) {
		BufferedImage imgCrimsonTears = con.loadImage("CrimsonTears.png");
		BufferedImage imgCeruleanTears = con.loadImage("CeruleanTears.png");

		//Initialize the map's UI with drawImage function:
		for (int row = 0; row < 20; row++) {
			for (int col = 0; col < 20; col++) {

				//Render the images to 20x20 pixels
				switch (strMap[row][col]) {
					case "g":
						con.drawImage(imgGrass, col * 30, row * 30);
						break;
					case "t":
						con.drawImage(imgTree, col * 30, row * 30);
						break;
					case "w":
						con.drawImage(imgWater, col * 30, row * 30);
						break;
					case "b":
						con.drawImage(imgBuilding, col * 30, row * 30);
						break;
					case "e":
						con.drawImage(imgEnemy, col * 30, row * 30);
						break;
					case "d":
						con.drawImage(imgDoor, col * 30, row * 30);
						break;
					case "hp":
						con.drawImage(imgCrimsonTears, col * 30, row * 30);
						break;
					case "fp":
						con.drawImage(imgCeruleanTears, col * 30, row * 30);
						break;
				}

			}
		}
		con.repaint();

	}

	//Function to display the Hero's stats:
	public static void displayHeroStats(Console con, Hero objHero) {
		//Write the HUD Player Stats:
		con.setDrawColor(Color.WHITE);
		con.drawString("HERO STATS: ", 650, 0);
		con.drawString("Health: "+objHero.getCurrentHP(), 660, 25);
		con.drawString("Attack: "+objHero.getCurrentDMG(), 660, 50);
		con.drawString("Defense: "+objHero.getCurrentDEF(), 660, 75);
		con.repaint();

		/*
		con.setDrawColor(Color.BLACK);
		con.fillRect(650, 0, 250, 100);
		con.repaint();
		 */
	}

	//Function to display the Hero's Current Item List
	public static void displayHeroItems(Console con, String[] strItems) {
		System.out.println("TEST: "+Arrays.deepToString(strItems));
		con.setDrawColor(Color.WHITE);
		con.drawString("HERO ITEMS: ", 650, 200);
		con.repaint();

		for (int i = 0; i < 5; i++) {
			//If Array Index is null, stop printing items:
			if (strItems[i] != null) {
				//Add array data to the HUD:
				con.drawString(String.valueOf(i + 1) + ". " + strItems[i], 660, 225 + i * 25);
				con.repaint();
			}
		}

		/*
		con.setDrawColor(Color.BLACK);
		con.fillRect(650, 200, 250, 400);
		con.repaint();
		 */
	}


	//MENUS:
	public static void startMenu(Console con) {
		con.drawString("Kafka's Journey", 300, 200);
		con.drawString("Press any key to continue", 250, 300);
		con.repaint();
		con.getKey();

		//Add story:
		resetScreen(con);
		con.setDrawColor(Color.WHITE);
		con.drawString("Known as one of the most feared Stellaron Hunters,", 100, 200);
		con.drawString("Kafka roams across the planets", 225, 225);
		con.drawString("killing the Stellaron Aeons that terrorize the universe.", 45, 250);
		con.drawString("Kafka now travels to the Xianzhou Luofu, a planet ", 100, 275);
		con.drawString("inhabited by the Ice Aeon that threatens to ", 125, 300);
		con.drawString("freeze the entirety of civilization.", 200, 325);
		con.drawString("Kafka's mission now becomes clear: ", 210, 375);
		con.drawString("Save the Xianzhou Luofu. Destroy Yanqing, the Ice Aeon.", 50, 400);
		con.repaint();
		con.sleep(3000);

		con.drawString("Press any key to continue", 250, 450);
		con.repaint();
		con.getKey();
	}

	//Function to load death screen upon Player death, and cut the System/game
	public static void deathMenu(Console con) {
		//Load Death Screen:
		BufferedImage imgDeath = con.loadImage("DeathImage.jpg");
		con.setBackgroundColor(Color.BLACK);
		con.drawImage(imgDeath, 100, 100);
		con.repaint();
		con.sleep(3000);
		System.exit(0);
	}

	public static void winGameMenu(Console con) {
		resetScreen(con);
		con.setDrawColor(Color.WHITE);
		con.drawString("The ice begins to thaw.", 250, 200);
		con.drawString("Kafka watches the remaining shards of Yanqing's ice swords,", 30, 225);
		con.drawString("as they melt away into the river that begins to flow again.", 30, 250);
		con.drawString("She looks out in the distance watching the Xianzhou", 97, 275);
		con.drawString("continue to live in peace, ", 230, 300);
		con.drawString("away from the terrors of the Ice Aeon.", 200, 325);
		con.drawString("\"Just another day for the Stellaron Hunters,\" she says,", 50,350);
		con.drawString("as she begins to set off for her next mission.", 125, 375);
		con.repaint();
		con.sleep(3000);

		con.drawString("Press any key to end game", 250, 450);
		con.repaint();
		con.getKey();
		System.exit(0);
	}

	public static void resetScreen(Console con) {
		con.setDrawColor(Color.BLACK);
		con.fillRect(0, 0, 800, 600);
		con.repaint();
	}

}
