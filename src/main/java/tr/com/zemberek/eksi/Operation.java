package tr.com.zemberek.eksi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Operation 
{
    public static void main( String[] args )
    {
    	String operationSelect = "-1";
    	Scanner scanIn = new Scanner(System.in);
    	List<String> allInMemory = new ArrayList<String>();
        do {
        	System.out.println("Çık      -> Press 0");
        	System.out.println("Txt oku ve Memory Al -> Press 1");
        	
        	
        	operationSelect = scanIn.nextLine();
        	if (operationSelect.equals("1")) {
        		allInMemory = readTxtToMemory();
        	}
        } while(!operationSelect.equals("0"));
        
        scanIn.close();
    }
    
    private static List<String> readTxtToMemory() {
    	BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("eksi.txt"));
		} catch (FileNotFoundException e1) {
			System.err.println("Dosya okunurken hata oluştu.");
		}
    	String line;
    	List<String> allInMemory = new ArrayList<String>();
    	try {
			while((line = in.readLine()) != null) {
				allInMemory.add(line);
			    System.out.println(line);
			}
		} catch (IOException e) {
			System.err.println("Txt okurken hata oluştu");
		}
    	try {
			in.close();
		} catch (IOException e) {
			System.err.println("Dosya kapatılırken hata oluştu");
		}
    	
    	return allInMemory;
    }
}
