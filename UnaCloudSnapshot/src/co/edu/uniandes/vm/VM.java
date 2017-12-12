package co.edu.uniandes.vm;
/**
 * This class contains useful methods to use VBOXManage and the VMs
 * 
 * @author Carlos Eduardo G�mez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/
import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.Util;

public class VM {
	
	private Configuration configuration;
	
	
	public VM(Configuration conf) {
		configuration = conf;
	}
	
	/**
	 * This method turns on an specified VM. If the VM name does not correspond to an already
	 * registered VM in the hypervisor, this method does nothing and returns an empty string.
	 * @param vmname 
	 */	
	public String turnOn(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "startvm"
				+ Constants.SPACE + vmname 
				);
		return output;
	}
	
	/**
	 * This method turns off the specified VM. It does not safe the state of the VM
	 * @param vmname
	 */	
	public String turnOffACPI(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "acpipowerbutton"
				);
		return output;
	}

	/**
	 * This method turns off the specified VM. First, it safe the state of the VM
	 * @param vmname
	 */	
	public String turnOff(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "savestate"
				);
		return output;
	}
	

	/**
	 * This method returns info of the specified VM
	 * @param vmname
	 */	
	public String getInfo(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "showvminfo"
				+ Constants.SPACE + vmname 
				);
		return output;
	}

	/**
	 * This method returns the IP of the specified VM
	 * @param vmname 
	 */	
	public String getIP(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "guestproperty"
				+ Constants.SPACE + "get"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "/VirtualBox/GuestInfo/Net/0/V4/IP" 
				);
		String ip = output.substring(7,output.length()-2);
		return ip;
	}
	
	/**
	 * This method pauses the execution of the specified VM
	 * @param vmname 
	 */	
	public String pause(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "pause"
				);
		return output;
	}

	/**
	 * This method resumes the execution of the specified VM
	 * @param vmname 
	 */	
	public String resume(String vmname) {
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				+ Constants.SPACE + "controlvm"
				+ Constants.SPACE + vmname 
				+ Constants.SPACE + "resume"
				);
		return output;
	}

	/**
	 * This method takes an snapshot of the specified VM
	 * @param vmname VM name.
	 * @param snapshotName .
	 */	
	public String takeSnapshot(String vmname, String snapshotName) {
		String output =
			Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
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
	public String exportVM(String vmname, String filename) {
		// Validar los parámetros asignados
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
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
	public String importVM(String vmname, String filename) {
		// Validar los parámetros asignados
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
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
	public String register(String vmname, String filepath) {
		// Validar los parámetros asignados
		String output =
		Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
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
