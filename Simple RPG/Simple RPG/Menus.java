import arc.*;
import java.awt.*;

public class Menus {
	
	//Global variables:
	public static char chrSelection;
	
	public static void main(String[] args) {
		Console con = new Console("Menus", 800, 600);
		
		Main.deathMenu(con);
		
	}
	
	//Without Map Selection:
	public static void startMenu(Console con) {
		con.drawString("Kafka's Battle", 200, 200);
		con.drawString("Press any key to continue", 125, 300);
		con.getKey();
		con.setBackgroundColor(new Color(0,0,0));
	}
	
	/*
	//The Start Menu will be a map selection:
	public static void startMenu(Console con) {
		con.drawString("Kafka's Battle", 200, 200);
		con.drawString("Select a map of your choice -- 1 or 2: ", 50, 300);
		
		boolean blnValidSelection = false;
		
		while (blnValidSelection != true) {
			chrSelection = con.getChar();
			if (chrSelection == '1' || chrSelection == '2') {
				blnValidSelection = true;
			}
		}
		
		System.out.println("TEST: "+chrSelection);
		con.setBackgroundColor(new Color(0,0,0));
	}
	*/
	
	public static char returnMapSelection(char chrSelection) {
		return chrSelection;
	}
}
