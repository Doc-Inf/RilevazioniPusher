package view;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

public class AppLogger {
	
	private static PrintWriter out;
	private static DebugMode debugMode;
	private static JTextArea logArea;
	
	static {	
			debugMode= DebugMode.CONSOLE_FILE; 
			try {
				out = new PrintWriter(new FileOutputStream(DateTimeFormatter.ofPattern("dd_MM_yyyy").format(LocalDate.now()) + ".txt"),true);
				out.println("---------------------------------------------" + LocalDateTime.now() + "---------------------------------------------");
				out.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	private AppLogger(){
		
	}
	
	public static void setDebugMode(DebugMode value) {
		AppLogger.debugMode = value;
	}
		
	public static void log(Object message) {
		switch(debugMode) {
			case CONSOLE:{
				System.out.println(message);
				break;
			}
			case FILE:{
				writeFile(message);			
				break;
			}
			case GUI:{
				logArea.append(message+"\n");
				break;
			}
			case CONSOLE_FILE:{
				System.out.println(message);
				writeFile(message);		
				break;
			}
			case CONSOLE_GUI:{
				System.out.println(message);
				logArea.append(message+"\n");
				break;
			}
			case FILE_GUI:{
				logArea.append(message+"\n");
				writeFile(message);		
				break;
			}
			case CONSOLE_GUI_FILE:{
				System.out.println(message);
				logArea.append(message+"\n");
				writeFile(message);		
				break;
			}	
		}	
	}
	
	public static void log(String thread, Object message) {
		message = thread + " - " + message;
		switch(debugMode) {
			case CONSOLE:{
				System.out.println(message);
				break;
			}
			case FILE:{
				writeFile(message);		
				break;
			}
			case GUI:{
				logArea.append(message+"\n");
				break;
			}
			case CONSOLE_FILE:{
				System.out.println(message);
				writeFile(message);		
				break;
			}
			case CONSOLE_GUI:{
				System.out.println(message);
				logArea.append(message+"\n");
				break;
			}
			case FILE_GUI:{
				logArea.append(message+"\n");
				writeFile(message);		
				break;
			}
			case CONSOLE_GUI_FILE:{
				System.out.println(message);
				logArea.append(message+"\n");
				writeFile(message);		
				break;
			}	
		}	
		
	}
	
	private static void writeFile(Object message) {
		out.println(message);
		out.flush();					
	}
	
	public static void setLogArea(JTextArea logArea) {
		AppLogger.logArea = logArea;
	}

	public enum DebugMode{
		CONSOLE,FILE,GUI,CONSOLE_FILE,CONSOLE_GUI,CONSOLE_GUI_FILE, FILE_GUI;
	}
	
}
