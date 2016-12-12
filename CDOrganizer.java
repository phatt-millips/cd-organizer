import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;

import org.omg.CORBA.SystemException;

public class CDOrganizer {
	private final String PATH_FILE_NAME = "src/Path.txt";
	private String SaveFileName;
	private int length;
	private CDLink Head;
	private CDLink Tail;
	private int artistLength = "Artist".length();
	private int titleLength = "Title".length(); 
	private int yearLength = "Year".length();
	private boolean saved = true;
	/**
	 * Blank CDOrganizer
	 */
	CDOrganizer(){
		Head = new CDLink();
		Tail = new CDLink();
		Head.next = Tail;
		length = 0;
	}
	/**
	 * Automatically places the first CD
	 * @param Artist Name, Album Title, Genre, Year
	 */
	CDOrganizer(String artist,String title,int genre,int year){
		Head = new CDLink();
		Tail = new CDLink();
		Head.next = new CDLink(artist,title,genre,year, Tail);
		length = 1;
		saved = false;
	}
	
	/**
	 * Creates the CD and places it in the appropriate alphabetical position
	 * @param Artist Name, Album Title, Genre, Year
	 */
	public void place(String artist, String title, int genre, int year){
		place(new CDLink(artist, title ,genre, year));
		saved = false;
	}
	
	/**
	 * Overloaded function to protect CDLink class
	 * Places CD's as the user inputs them using the compareTo function
	 * @param New CDLink
	 */
	private void place(CDLink NewLink){
		CDLink Current = Head.next;
		//trash matches
		while (Current != Tail && NewLink.artist.compareToIgnoreCase(Current.artist) > 0){
			Current = Current.next;
		}
		//places in correct position based on year
		while(Current != Tail && NewLink.artist.compareToIgnoreCase(Current.artist) == 0 && Current.year > NewLink.year){
			Current = Current.next;
		}
		insertBefore(Current, NewLink);
	}
	
	/**
	 * searchArtist() is designed to loop through the elements using the String function comparTo() until 
	 * it finds a CD that matches the user's input
	 *
	 * @param String: Artist's name 
	 * @return CDOrganizer List of found elements 
	 */
	public CDOrganizer searchArtist(String Artist){
		CDOrganizer RetVal = new CDOrganizer();
		CDLink Current = Head.next;
		//trash matches
		while (Current != Tail && Artist.compareToIgnoreCase(Current.artist) > 0){
			Current = Current.next;
		}
		//exact matches && partial match 
		while(Current != Tail && Artist.compareToIgnoreCase(Current.artist)== 0 || Current.artist.startsWith(Artist.toUpperCase())){
			RetVal.append(Current);
			Current = Current.next;
		}
		//no matches
		if (RetVal.length == 0){
			System.out.println("Artist not found");
		}
		return RetVal;
	}
	
	/**
	 * searchGenre() is similar to searchArtist exept it moves through the entire list matching the elements 
	 * with the genre number
	 * 
	 * @param Genre number
	 * @return CDOrganizer List of found elements
	 * @throws FileNotFoundException 
	 */
	public CDOrganizer searchGenre(int genre) throws FileNotFoundException{
		CDOrganizer RetVal = new CDOrganizer();
		CDLink Current = Head.next;
		while(Current != Tail){
			if (Current.genre == genre){
				RetVal.append(Current);
			}
			Current = Current.next;
		}
		return RetVal;
	}
	
	/**
	 * append() is used in the searchArtist() and searchGenre() methods to append to the end of the search list 
	 * to avoid resorting already sorted CDs
	 * 
	 * @param New CDLink
	 */
	private void append(CDLink NewLink){
		insertBefore(Tail, NewLink);
	}
	
	/**
	 * insertBefore() is the foundation of the Link based structure. It creates an new CDLink then places the already linked or Old
	 * CDLink inside the null link. Then replaces the Old CDLink skelton with the contents of the Link to be inserted. Then points the
	 * inserted link's next reference to the Old CDLink.
	 * Handles append method
	 * 
	 * @param Old CDlink, New CDLInk
	 */
	private void insertBefore(CDLink Old, CDLink New){
		length++;
		CDLink Temp = new CDLink(Old);
		Old.init(New.artist, New.title, New.genre, New.year, Temp);
		if (Old == Tail){
			Old.next = Tail = new CDLink();
		}
	}
	
	/**
	 * save() uses PrintWriter to interate through all elements printing them to a file 
	 * "." is used a delimiter for individual elements of a CD and "\n" for seperate CDs
	 * @throws FileNotFoundException
	 */
	public void save() throws FileNotFoundException{
		PrintWriter p = new PrintWriter(new File(SaveFileName));
		CDLink Current = Head.next;
		while(Current != Tail){
			p.write(Current.artist + "." + Current.title + "." + Current.year + "." + Current.genre + "\n");
			Current = Current.next;
		}
		p.write("~~~");
		p.close();
		System.out.println("******Saved******");
		saved = true;
	}
	
	/**
	 * load() uses Scanner to iterate through all saved items on the file and appends them to the Organizer
	 * @throws FileNotFoundException
	 */
	public void load() throws FileNotFoundException {
		Scanner s = new Scanner(new File(SaveFileName));
		String Line = s.nextLine();
		while(!Line.contains("~~~")){
			String[] LineContent = Line.split("\\.");
			append(new CDLink(LineContent[0],LineContent[1],Integer.parseInt(LineContent[2]),Integer.parseInt(LineContent[3])));
			Line = s.nextLine();
		}
		s.close();
	}
	
	/**
	 * delete() removes a CD and prints its contents or that a CD was not found
	 * @param Artist
	 * @param Title
	 * @param genre
	 * @param year
	 */
	public void delete(String Artist, String Title, int genre, int year){
		CDLink deletedCD = delete(new CDLink(Artist,Title,genre,year));
		if (deletedCD != Tail){
			System.out.println(deletedCD + " Deleted");
		}
		else {
			System.out.println("CD Not Found");
		}
		saved = false;
	}
	
	/**
	 * delete() reduces the length and points the previous CD's next around the deleted item and sets the deleted CD's next to null
	 * so the garbage collector can collect
	 * @param CD
	 * @return CD
	 */
	private CDLink delete(CDLink CD){
		length--;
		CDLink Current = prev(CD);
		if (Current.next != null){
			CD = Current.next;
			Current.next = CD.next;
			CD.next = null;
			return CD;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Returns the prevous CD
	 * @param CD
	 * @return CD
	 */
	private CDLink prev(CDLink CD){
		CDLink Current = Head;
		while(Current.next != Tail && !Current.next.equals(CD)){
			Current = Current.next;
		}
		return Current;

	}
	
	public int getLength() { return length; }
	
	public boolean getSaved() { return saved; }

	@Override
	public String toString(){
		StringBuilder RetVal = new StringBuilder();
		if (Head.next == Tail){
			RetVal.append("No CDs Found");
		}
		else{
			RetVal.append("ARTIST");
			for (int i = 1; i <= artistLength - "Artist".length() + 2; i++){
				RetVal.append(" ");
			}
			RetVal.append("TITLE");
			for (int i = 1; i <= titleLength - "Title".length() + 2; i++){
				RetVal.append(" ");
			}
			RetVal.append("YEAR");
			for (int i = 1; i <= 2; i++) {
				RetVal.append(" ");
			}
			RetVal.append("GENRE\n");
			
			CDLink Temp = Head;
			int i = 0;
			while (i < length && Temp != Tail){
				Temp = Temp.next;
				RetVal.append(Temp.toString() + "\n");
				i++;
			}
		}
		return RetVal.toString();
	}
	
	private class CDLink { 
		private CDLink next;
		private String artist;
		private String title;
		private int genre;
		private int year;

		CDLink(String artist, String title, int genre, int year, CDLink next){
			init(artist.trim().toUpperCase(),title.trim().toUpperCase(),genre,year,next);
		}
		
		CDLink(String artist, String title, int genre, int year){
			init(artist.trim().toUpperCase(),title.trim().toUpperCase(),genre,year,null);
		}
		
		CDLink(CDLink CD){
			init(CD.artist,CD.title,CD.genre,CD.year,CD.next);
		}
		
		CDLink(){
			init(null,null,0,0,null);
		}
		
		public boolean equals(CDLink CD){
			return artist.equalsIgnoreCase(CD.artist) && title.equalsIgnoreCase(CD.title) && genre == CD.genre && year == CD.year;
		}
		
		private void init(String artist, String title, int year, int genre, CDLink next){
			this.artist = artist;
			this.title = title;
			this.genre = genre;
			this.year = year;
			this.next = next;
			if (artist != null && artist != null){
				if (artist.length() > artistLength)
					artistLength = artist.length();
				if (title.length() > titleLength)
					titleLength = title.length();
			}
		}
		
		@Override
		public String toString(){
			StringBuilder RetVal = new StringBuilder();
			int spacing1 = artistLength - artist.length() + 2;
			int spacing2 = titleLength - title.length() + 2;
			int spacing3 = yearLength - String.valueOf(year).length() + 2;
			RetVal.append(artist);
			for (int i = 1; i <= spacing1; i++){
				RetVal.append(" ");
			}
			RetVal.append(title);
			for (int i = 1; i <= spacing2; i++){
				RetVal.append(" ");
			}
			RetVal.append(year);
			for (int i = 1; i <= spacing3; i++){
				RetVal.append(" ");
			}
			RetVal.append(genre);
			return RetVal.toString();
		}
	}
}