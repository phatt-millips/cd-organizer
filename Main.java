import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	final static String GENRE_FILE_NAME = "src/genre.txt";
	static CDOrganizer MainOrganizer = new CDOrganizer();

	public static void main(String[] args) throws FileNotFoundException{
		MainOrganizer.load();
		testCases();
		System.out.println("CD Organizer -- Enter your choice");
		Scanner kbd = new Scanner(System.in);
		Selection(kbd);
		kbd.close();
	}

	public static void Selection(Scanner kbd) throws FileNotFoundException {
		int answer = promptExpectingInt(mainMenu(), kbd);
		boolean looping = true;
		while (looping){
			switch(answer){
			case 1: // Enter a new CD
				EnterNewCD(kbd);
				break;
			case 2: // Prints CD
				System.out.println(MainOrganizer);
				break;
			case 3: // Search
				Search(kbd,answer);
				break;
			case 4: // Delete
				Delete(kbd);
				break;
			case 5: // Save
				MainOrganizer.save();
				break;
			case 9: 
				if (!MainOrganizer.getSaved()){
					String closingAnswer = promptExpectingString("Would you like to save before quiting: (Y/N)", kbd).toUpperCase().trim();
					while (!closingAnswer.equals("Y") && !closingAnswer.equals("N")){
						System.out.println(closingAnswer);
						closingAnswer = promptExpectingString("Would you like to save before quiting: (Y/N)", kbd).toUpperCase().trim();
					}
					if (closingAnswer.matches("Y")){
						MainOrganizer.save();
					}
				}
				looping = false;
				break;
			default :
				System.out.println("Invalid operation");
			}
			if (looping){
				answer = promptExpectingInt(mainMenu(), kbd);
			}
		}
	}

	// Returns the Main Menu as a String formated with new lines
	public static String mainMenu(){
		return "1:Enter a New CD \n"
				+ "2:View all CDs \n"
				+ "3:Search for a CD \n"
				+ "4:Delete CD \n"
				+ "5:Save \n"
				+ "9:Exit Program \n";
	}

	// Returns the Search Menu as a String formated with new lines
	public static String searchMenu(){
		return "1:Artist \n"
				+ "2:Genre \n";
	}

	//Returns the Genre Menu as a String formatted with new lines
	public static String genreMenu() throws FileNotFoundException{
		StringBuilder RetVal = new StringBuilder();
		Scanner reader = new Scanner(new File(GENRE_FILE_NAME));
		int lengthOfFile = Integer.parseInt(reader.nextLine());
		RetVal.append("# Genre\n");
		for (int i = 0; i < lengthOfFile; i++){
			RetVal.append(reader.nextLine() + "\n");
		}
		reader.close();
		return RetVal.toString();
	}

	//Option Number 1
	public static void EnterNewCD(Scanner kbd) throws FileNotFoundException{
		String Artist = promptExpectingString("Artist: ", kbd);
		String Title = promptExpectingString("Title: ", kbd);
		int year = promptExpectingInt("Year: ", kbd);
		int genre = promptExpectingInt(genreMenu() + "Genre: ", kbd);
		MainOrganizer.place(Artist, Title, genre, year);
		System.out.println(MainOrganizer);
	}

	//Option Number 3
	public static void Search(Scanner kbd, int answer) throws FileNotFoundException{
		answer = promptExpectingInt(searchMenu(), kbd);
		while(answer != 1 && answer != 2){
			System.out.println("Ivalid operation");
			answer = promptExpectingInt(searchMenu(), kbd);
		}
		switch (answer){
		case 1:
			System.out.println("All or part of the Artist's name");
			System.out.println(MainOrganizer.searchArtist(promptExpectingString("Artist: ", kbd)));
			break;
		case 2:
			genreMenu();
			System.out.println(MainOrganizer.searchGenre(promptExpectingInt("Genre: ", kbd)));
			break;
		}

	}

	//Option Number 4
	public static void Delete(Scanner kbd){
		String Artist = promptExpectingString("Artist: ", kbd);
		String Title = promptExpectingString("Title: ", kbd);
		int genre = promptExpectingInt("Genre: ", kbd);
		int year = promptExpectingInt("Year: ", kbd);

		MainOrganizer.delete(Artist, Title, genre, year);
	}

	//Prompts, Catches Number Format Exeptions but only one time (not sure how to loop this) and returns int
	public static int promptExpectingInt(String Prompt, Scanner kbd){
		System.out.print(Prompt);
		int retVal;
		String answer = kbd.nextLine();
		try {
			retVal = Integer.parseInt(answer);
		}
		catch (NumberFormatException e){
			System.out.println("Invalid Response");
			System.out.print(Prompt);
			answer = kbd.nextLine();
			retVal = Integer.parseInt(answer);
		}
		return retVal;
	}

	//Prompts and Returns String
	public static String promptExpectingString(String Prompt, Scanner kbd){
		System.out.print(Prompt);
		return kbd.nextLine();
	}

	//Used for Testing purposes
	public static void testCases(){
		MainOrganizer.place("a", "a", 1, 1);
		MainOrganizer.place("aa", "aa", 2, 2);
		MainOrganizer.place("ab", "a", 3, 2);
		MainOrganizer.place("b", "b", 5, 3);
		MainOrganizer.place("b", "b", 5, 7);
		MainOrganizer.place("c", "c", 2, 5);
		MainOrganizer.place("ca", "ca", 3, 3);
		MainOrganizer.place("cd", "cd", 3, 3);
		MainOrganizer.place("a", "a", 1, 4);
		MainOrganizer.place("j", "j", 1, 1);
		MainOrganizer.place("1", "a", 1, 1);
		MainOrganizer.place("A A", "a", 1, 2);
		MainOrganizer.place("a", "a", 1, 2);
		MainOrganizer.place("a", "a", 1, 3);
		MainOrganizer.place("a", "a", 1, 4);
		MainOrganizer.place("a", "a", 1, 5);
		MainOrganizer.place("a", "a", 1, 1);
	}
}
