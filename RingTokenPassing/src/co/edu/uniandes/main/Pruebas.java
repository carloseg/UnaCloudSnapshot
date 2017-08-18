package co.edu.uniandes.main;

import co.edu.uniandes.util.Constants;

public class Pruebas {

	public static void main(String[] args) {
		String answer = "";

		// messages processing
		/*
		 * ----------> INSERT The name server receives the ip address of
		 * the process. The name server assigns a processId and insert a
		 * register about the process.
		 */
		
		String line="All_INITIAL_TIME";
		
		if (line.trim().startsWith("INSERT")) {
			System.out.println("insert(line)");
		} else if (line.trim().startsWith("QUERY_LIST")) {
			System.out.println("queryList(line)");
		} else if (line.trim().startsWith("QUERY_NEXT")) {
			System.out.println("queryNext(line)");
		} else if (line.trim().startsWith("QUERY_SIZE")) {
			System.out.println("querySize(line)");
		} else if (line.trim().startsWith("RESET")) {
			System.out.println("reset()");
		} else if (line.trim().startsWith("INITIAL_TIME")) {
			System.out.println("initialTime(line)");
		} else if (line.trim().startsWith("END_TIME")) {
			System.out.println("endTime(line)");
		} else if (line.trim().startsWith("QUERY_TIME")) {
			System.out.println("queryTime()");
		} else if (line.startsWith("QUERY" + Constants.SPACE)) {
			System.out.println("query(line)");
		} else if (line.trim().startsWith("All_INITIAL_TIME")) {
			//System.out.println("Procesando solicitud de establecer el tiempo inicial");
			System.out.println("allInitialTime(line)");
		}
	}

}
