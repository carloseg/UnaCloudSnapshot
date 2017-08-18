package co.edu.uniandes.vm;

import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.Util;

public class CopyOfVM {
	
	
	
	/**
	 * Este metodo inicia la VM especficada. Si el nombre de la VM no corresponde con
	 * una VM registrada en el hipervisor, este metodo no hace nada y retorna cadena vacia
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String turnOn(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "startvm"
				+ Constants.SPACE + vmname 
				);
		return output;
	}
	
	/**
	 * Este metodo apaga la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String turnOffACPI(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "acpipowerbutton"
				);
		return output;
	}

	/**
	 * Este metodo apaga la VM especficada guardando el estado.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String turnOff(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "savestate"
				);
		return output;
	}
	

	/**
	 * Este metodo retorna informacion de la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String getInfo(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "showvminfo"
				+ Constants.SPACE + vmname 
				);
		return output;
	}

	/**
	 * Este metodo retorna la direccion IP de la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	// Se asume que la VM tiene una sola interfaz de red con una sola direccion ip
	public static String getIP(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "guestproperty"
				+ Constants.SPACE + "get"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "/VirtualBox/GuestInfo/Net/0/V4/IP" 
				);
		// Retira el prefijo "Value: " y el sufijo "\r\n" (2 caracteres).
		String ip = output.substring(7,output.length()-2);
		return ip;
	}
	
	/**
	 * Este metodo detiene temporalmente la ejecucion de la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String pause(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "pause"
				);
		return output;
	}

	/**
	 * Este metodo reanuda la ejecucion de la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String resume(String vmname) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "resume"
				);
		return output;
	}

	/**
	 * Este metodo toma un snapshot de la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 * @param snapshotName Es el nombre del snapshot.
	 */	
	public static String takeSnapshot(String vmname, String snapshotName) {
		// Validar los parámetros asignados
		String output =
			Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
					+ Constants.SPACE + "snapshot" 
					+ Constants.SPACE + vmname
					+ Constants.SPACE + "take"
					+ Constants.SPACE + snapshotName
					);
		return output;
	}
	
	
	
	
	
	
	
	
	/////////// ============ OTROS SERVICIOS ============ /////////// 
	
	/**
	 * Este metodo exporta la VM especficada.
	 * El archivo tiene el mismo nombre con la extension .ova
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String exportVM(String vmname, String filename) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "export"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "-o"
				+ Constants.SPACE + filename 
				);
		// -o para especificar el nombre del archivo con la MV exportada.
		return output;
	}
	
	/**
	 * Este metodo importa la VM especficada.
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String importVM(String vmname, String filename) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "import"
				+ Constants.SPACE + filename 
				);
		return output;
	}
	
	/**
	 * Este metodo registra la VM especficada.
	 * Registrar la VM es importante para la importación correcta
	 * @param vmname Es el nombre de la VM.
	 */	
	public static String register(String vmname, String filepath) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "registervm"
				+ Constants.SPACE + filepath 
				);
		// La ruta del archivo .vbox debe ser completa.
		return output;
	}

	/**
	 * Este metodo clona la VM especficada.
	 * Para clonar una MV, debe estar apagada o tener guardado el estado.
	 * No se puede clonar si la MV está corriendo o si está pausada.
	 * Clonación completa. Estado actual de la MV.
	 * No reinicializar la dirección MAC de las tarjetas de red.
	 * @param vmname Es el nombre de la VM.
	 * @param vmname02 Es el nombre de la nueva VM.
	 */	
	public static String clone(String vmname, String vmname02) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + "clonevm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "--name"
				+ Constants.SPACE + vmname02
				+ Constants.SPACE + "--mode"
				+ Constants.SPACE + "machine"
//				+ Constants.SPACE + "--options"
//				+ Constants.SPACE + "keepallmacs"
				+ Constants.SPACE + "--register"
				);
		//--name: Para especificar el nombre de la nueva MV.
		//--mode machine: La clonación es con el estado actual de la MV sin snapshots.
		//--options keepallmacs: No reinicia las direcciones MAC de las NICs.
		//--register: Registra automáticamente la nueva MV en este VirtualBox.
		return output;
	}
	
	/**
	 * Este metodo le cambia el nombre a la VM especficada.
	 * Para cambiar el nombre una MV, debe estar apagada o tener guardado el estado.
	 * No se puede cambiar el nombre si la MV está corriendo o si está pausada.
	 * @param vmname Es el nombre de la VM.
	 * @param vmname02 Es el nuevo nombre de la VM.
	 */	
	public static String rename(String vmname, String vmname02) {
		// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "--name"
				+ Constants.SPACE + vmname02
				);
		return output;
	}

	public static String modify(String vmname, int cores, int ram) {
	// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
			+ Constants.SPACE + "modifyvm"
			+ Constants.SPACE + vmname 
			+ Constants.SPACE + "--cpus" 
			+ Constants.SPACE + cores 
			+ Constants.SPACE + "--memory" 
			+ Constants.SPACE + ram
			);
		return output;
	}
	
	public static String enableNicTraceMode(String vmname, int nic, String filename) {
	// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
			+ Constants.SPACE + "modifyvm"
			+ Constants.SPACE + vmname 
			+ Constants.SPACE + "--nictrace" + nic 
			+ Constants.SPACE + "on" 
			+ Constants.SPACE + "--nictracefile" + nic 
			+ Constants.SPACE + filename
			);
		return output;
	}

	public static String disbleNicTraceMode(String vmname, int nic) {
	// Validar los parámetros asignados
		String output =
		Util.execute(Constants.VIRTUAL_BOX_HOME + "VBoxManage"
			+ Constants.SPACE + "modifyvm"
			+ Constants.SPACE + vmname 
			+ Constants.SPACE + "--nictrace" + nic 
			+ Constants.SPACE + "off" 
			);
		return output;
	}

}
