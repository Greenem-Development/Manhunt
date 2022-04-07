package org.greenem.plugins.manhunt.worldManagement;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.data.References.runFileString;
import static org.greenem.plugins.manhunt.data.References.javaString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.manhunt.Main.Main;
import org.greenem.plugins.manhunt.data.Names;

public class WorldResetting {
	public static void resetWorld(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender || (sender instanceof Player && ((Player) sender).isOp())) {
			if(!new File(serverDirectory, runFileString).exists()) {
				sender.sendMessage(ChatColor.RED + "You haven't specified a valid server start script file name in the config! "
						+ "(at server/plugins/configs/ManhuntAndWorldReset/" + Names.mainConfigName + ")");
				return;
			}
				
			File pluginsDir = new File(Bukkit.getWorldContainer(), "plugins");
			File filesForResetingWorldDirectory = new File(pluginsDir, "FilesForResetingWorld");
			
			File prev = new File(filesForResetingWorldDirectory, "ResetWorldAndRestart.jar");
			if(prev.exists()) {
				prev.delete();
			}
			
			copyResetWorldJarFile();
			
			AtomicReference<Process> ps = new AtomicReference<>();
			Thread jarThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						//ProcessBuilder pb = new ProcessBuilder("cmd.exe /c \"java -jar ResetWorldAndRestart.jar\"");
						//pb.directory(filesForResetingWorldDirectory);
						//ps.set(pb.start());
						ps.set(Runtime.getRuntime().exec("cmd.exe /c \"pushd \"" + filesForResetingWorldDirectory + "\" && start cmd /c " + javaString + " -jar ResetWorldAndRestart.jar\""));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			jarThread.start();
			
			try {
				Thread.currentThread().sleep(500); //TODO: GOOD TIMINGS (and above)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Runtime.getRuntime().halt(0);
			
			//Bukkit.shutdown();
		}
		else {
			sender.sendMessage(ChatColor.RED + "You don't have enough permissions to do this");
		}
	}
	
	public static void copyResetWorldJarFile() {
		File pluginsDir = new File(serverDirectory, "plugins");
		File filesForResetingWorldDirectory = new File(pluginsDir, "FilesForResetingWorld");
		filesForResetingWorldDirectory.mkdirs();
		
		File f = new File(filesForResetingWorldDirectory, "ResetWorldAndRestart.jar");
		if (!f.exists()) {
			InputStream is = Main.getInstance().getClass().getResourceAsStream("/extraFiles/ResetWorldAndRestart.jar");
			OutputStream os = null;
			try {
				os = new FileOutputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			byte[] buffer = new byte[4096];
			int length;
			try {
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
