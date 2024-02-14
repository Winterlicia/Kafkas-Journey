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
		Console con = new Console("RPG Game", 800, 600); //REMEMBER TO CHANGE THIS BACK TO 20X20 PIXELS

		//Initialize the map(s) 
		String[][] strMap = new String[20][20];
		TextInputFile map = new TextInputFile("map2.csv");

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

		//Initiate the Start Menu at the beginning:
		startMenu(con);

		//Load the map using the functions:
		strMap = loadMap(map);
		renderMap(con, strMap, imgGrass, imgTree, imgBuilding, imgWater, imgEnemy);

		//Initialize a new instance of the "Hero" object for the player:
		String[] strItems = new String[5];
		Hero hero = new Hero(imgHero, 19, 10, 50, 15, 10, 0, strItems);

		//Get rid of any text before running the main game loop:
		resetScreen(con);

		//Main game loop:
		while (true) {
			//Display the character, stats, etc. Should always be actively updating because of the while true loop.
			renderMap(con, strMap, imgGrass, imgTree, imgBuilding, imgWater, imgEnemy);
			displayHeroStats(con, hero);
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
			} else {
				System.out.println("Invalid character input");
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
				Enemy enemy = new Enemy(imgEnemyBattle, 40, 10, 10);

				//Call the battle functions (do later)
				Battle.battleListener(con, hero, enemy, imgBattlefield, imgHeroBattle);
				//Check if the player has won -- if yes, get rid of that enemy and replace it with a grass block:
				if (Battle.blnWonBattle == true) {
					strMap[hero.getCurrentRowPosition()][hero.getCurrentColPosition()] = "g";
					renderMap(con, strMap, imgGrass, imgTree, imgBuilding, imgWater, imgEnemy);
					Battle.blnWonBattle = false; //Set back to false, reset next battle
				} else {
					//If not, player should be dead -- we can just make sure of it here:
					deathMenu(con);
					break;
				}

				con.repaint();
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
	public static void renderMap(Console con, String[][] strMap, BufferedImage imgGrass, BufferedImage imgTree, BufferedImage imgBuilding, BufferedImage imgWater, BufferedImage imgEnemy) {

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

		con.setDrawColor(Color.BLACK);
		con.fillRect(650, 200, 250, 400);
		con.repaint();
	}

	//Function to display the Hero's Current Item List
	public static void displayHeroItems(Console con, String[] strItems) {
		con.setDrawColor(Color.WHITE);
		con.drawString("HERO ITEMS: ", 650, 400);
		for (int i = 0; i < 5; i++) {
			//If Array Index is null, stop printing items:
			if (strItems[i] == null) {
				break;
			} else {
				//Add array data to the HUD:
				con.drawString(String.valueOf(i+1)+". "+strItems[i], 660, 425+i*25);
			}
		}
		con.repaint();

		con.setDrawColor(Color.BLACK);
		con.fillRect(650, 200, 250, 400);
		con.repaint();
	}
	
	
	//MENUS:
	public static void startMenu(Console con) {
		con.drawString("Kafka's Battle", 300, 200);
		con.drawString("Press any key to continue", 250, 300);
		con.repaint();
		con.getKey();
	}

	public static void deathMenu(Console con) {
		//Load Death Screen:
		BufferedImage imgDeath = con.loadImage("DeathImage.jpg");
		con.setBackgroundColor(Color.BLACK);
		con.drawImage(imgDeath, 100, 100);
		con.repaint();
		con.sleep(3000);
		System.exit(0);
	}

	public static void resetScreen(Console con) {
		con.setDrawColor(Color.BLACK);
		con.fillRect(0, 0, 800, 600);
		con.repaint();
	}
	
}
