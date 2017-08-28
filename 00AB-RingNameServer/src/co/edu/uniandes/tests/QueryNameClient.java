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

public class QueryNameClient {
	
	public static void main(String args[]) {
		System.out.println(NamesUtil.nameServerReset("localhost", 5353));

		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
		
		System.out.println(NamesUtil.nameQuery("serv#0", 0, "localhost", 5353));
		System.out.println(NamesUtil.nameQuery("serv#1", 0, "localhost", 5353));
		System.out.println(NamesUtil.nameQuery("serv#2", 0, "localhost", 5353));
		
		System.out.println(NamesUtil.nameRingInitialTime(0, "localhost", 5353));
		System.out.println(NamesUtil.nameRingInitialTime(1, "localhost", 5353));
		System.out.println(NamesUtil.nameRingInitialTime(2, "localhost", 5353));
		System.out.println(NamesUtil.nameRingInitialTime(3, "localhost", 5353));
		System.out.println(NamesUtil.nameRingInitialTime(4, "localhost", 5353));
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameRingEndTime(0, "localhost", 5353));

		Util.pauseMS(500);
		System.out.println(NamesUtil.nameRingEndTime(1, "localhost", 5353));
		
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameRingEndTime(2, "localhost", 5353));
		
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameRingEndTime(3, "localhost", 5353));

		Util.pauseMS(500);
		System.out.println(NamesUtil.nameRingEndTime(4, "localhost", 5353));

//		System.out.println(NamesUtil.nameServerRingQueryTime("localhost", 5353));
		
//		System.out.println(NamesUtil.nameQueryList(2, "localhost", 5353));
//		System.out.println(NamesUtil.nameQueryNextNode(3, "localhost", 5353));
//		System.out.println(NamesUtil.nameQueryNextNode(4, "localhost", 5353));
//		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5353));
//
//		System.out.println();
//		
//		System.out.println(NamesUtil.nameServerReset("localhost", 5353));
//		
//		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5353));
//		System.out.println(NamesUtil.nameQuery("serv#0", 0, "localhost", 5353));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5353));
//		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5353));
//		System.out.println(NamesUtil.nameQuery("serv#0", 0, "localhost", 5353));
		
		
	}
}
