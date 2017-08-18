package co.edu.uniandes.tests;

/**
 * This application tests the name server services.
 * 
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

public class Query {
	
	public static void main(String args[]) {
		System.out.println(NamesUtil.nameQueryList(0, "localhost", 5353));
		
		NamesUtil.nameServerRingQuit("localhost", 5353);
	}
}
