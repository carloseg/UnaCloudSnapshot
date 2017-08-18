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
		System.out.println(NamesUtil.nameServerReset("localhost", 5300));

		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		
		System.out.println(NamesUtil.nameQuery("serv#0", 0, "localhost", 5300));
		System.out.println(NamesUtil.nameQuery("serv#1", 0, "localhost", 5300));
		System.out.println(NamesUtil.nameQuery("serv#2", 0, "localhost", 5300));
		
		System.out.println(NamesUtil.nameQueryList(2, "localhost", 5300));
		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5300));

		System.out.println();
		
//		System.out.println(NamesUtil.nameServerReset("localhost", 5300));
//		
//		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5300));
//		System.out.println(NamesUtil.nameQuery("serv#0", 1, "localhost", 5300));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
//		System.out.println(NamesUtil.nameInsert("localhost", "localhost", 5300));
		System.out.println(NamesUtil.nameQuerySize(0, "localhost", 5300));
//		System.out.println(NamesUtil.nameQuery("serv#0", 0, "localhost", 5300));

		
		//////// Ojo mensajes en Name Server
		
		System.out.println("================");
		
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameEndTime(0, "localhost", 5300));
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameEndTime(1, "localhost", 5300));
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameEndTime(2, "localhost", 5300));
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameEndTime(3, "localhost", 5300));
		Util.pauseMS(500);
		System.out.println(NamesUtil.nameEndTime(4, "localhost", 5300));

//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(0, "localhost", 5300, 2));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(1, "localhost", 5300, 2));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(2, "localhost", 5300, 2));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(3, "localhost", 5300, 2));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(4, "localhost", 5300, 2));
//
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(0, "localhost", 5300, 3));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(1, "localhost", 5300, 3));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(2, "localhost", 5300, 3));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(3, "localhost", 5300, 3));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(4, "localhost", 5300, 3));
//
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(0, "localhost", 5300, 4));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(1, "localhost", 5300, 4));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(2, "localhost", 5300, 4));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(3, "localhost", 5300, 4));
//		Util.pauseMS(500);
//		System.out.println(NamesUtil.nameEndTime(4, "localhost", 5300, 4));
	}
}
