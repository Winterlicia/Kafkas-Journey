import java.awt.image.BufferedImage;

public class Hero {
	//Hero's ATTRIBUTES: image, board position, stats
	BufferedImage imgHero;
	int intRowPos = 19;
	int intColPos = 10;
	int intHP = 50;
	int intDMG = 15;
	int intDEF = 10;
	int intEnemiesDefeated = 0;
	//Initialize array of items:
	String[] strItems = new String[5];

	//Initialize the Object's (Hero) constructor with its parameters:
	public Hero(BufferedImage imgHero, int intRowPos, int intColPos, int intHP, int intDMG, int intDEF, int intEnemiesDefeated, String[] strItems) {
		this.imgHero = imgHero;
		this.intRowPos = intRowPos;
		this.intColPos = intColPos;
		this.intHP = intHP;
		this.intDMG = intDMG;
		this.intDEF = intDEF;
		this.intEnemiesDefeated = intEnemiesDefeated;
		this.strItems = strItems;
	}

	//Below are Getters and Setters of the object:
	// 1) Setters allow us to continuously update the Hero's attributes.
	// 2) Getters allow us to take the Hero's current attributes to control the game's mechanics.
	public BufferedImage getHeroImage() {
		return this.imgHero;
	}
	
	public void setHeroImage(BufferedImage imgHero) {
		this.imgHero = imgHero;
	}
	
	public int getCurrentRowPosition() {
		return this.intRowPos;
	}
	
	public void setNewRowPosition(int intRowPos) {
		this.intRowPos = intRowPos;
	}
	
	public int getCurrentColPosition() {
		return this.intColPos;
	}
	
	public void setNewColPosition(int intColPos) {
		this.intColPos = intColPos;
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

	public int getEnemiesDefeated() { return this.intEnemiesDefeated; }

	public void setNewEnemiesDefeated(int intEnemiesDefeated) { this.intEnemiesDefeated = intEnemiesDefeated; }

	public String[] getItemList() {
		return this.strItems;
	}

	//Add items based on the current items that the hero has:
	public void setNewItem(String strAddItem) {
		strItems[this.intEnemiesDefeated-1] = strAddItem;
	}
	
	//Other Functions:
	//Function to check if the player can move -- NOTE THAT IT MUST BE CHECKED BEFOREHAND:
	public boolean canMove(int intRow, int intCol, String[][] strCheckMap, String strDirection) {
		
		//Prevent the player from going out of boundaries:
		if ((intRow+1 >= 20 || intRow-1 <= -1) && strDirection.equals("vertical")) {
			return false;
		} else if ((intCol + 1 >= 20 || intCol-1 <= -1) && strDirection.equals("horizontal")) {
			return false;
		}
		
		//Prevent the player from moving into a tree:
		else if (strCheckMap[intRow][intCol].equals("t")) {
			return false;
		}
		
		//If all other conditions are checked, and do not return false, then return true -- the player can move.
		else {
			return true;
		}
	}
}
