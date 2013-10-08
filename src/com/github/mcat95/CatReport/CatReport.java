package com.github.mcat95.CatReport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CatReport extends JavaPlugin implements Listener {
	public List<String> Strings;
	public void onEnable() {
		this.saveDefaultConfig();
		//Registro de Eventos
		getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("CatReport is working ;D");
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		    this.getLogger().info("Stats sent");
		} catch (IOException e) {		   
		}
		Strings = this.getConfig().getStringList("Reportes");
	}
	
	@EventHandler	
	public void playerLogin(final PlayerJoinEvent event) {
		int secs = this.getConfig().getInt("secs");
		if(event.getPlayer().hasPermission("Catreport.op")){
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					if(Strings.size()!=0){
						event.getPlayer().sendMessage(ChatColor.GOLD+getString("pending"));
					}else{
						event.getPlayer().sendMessage(ChatColor.GOLD+getString("done"));
					}					
				}		
				
			},secs*20L);			
		}
		
	}
	public String getString(String string) {
		return this.getConfig().getString(string);
	}
	public static String pegar(String[] array, String glue) {
		String glued = "";
		for(int i = 0; i < array.length;i++) {
		glued += array[i];
		if(i != array.length-1)
		glued += glue;
		}
		return glued;
		}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("test")){
			sender.sendMessage("test");
		}
		if(cmd.getName().equalsIgnoreCase("report")){
			if(args.length>0 & sender instanceof Player){
				String cadena = pegar(args, " ");
				Player player = (Player) sender;
				Location l = player.getLocation();
				int lx = (int) l.getX();
				int ly = (int) l.getY();
				int lz = (int) l.getZ();
				Calendar cal = Calendar.getInstance();
		    	cal.getTime();
		    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
		    	String ad = "Reportado por: "+sender.getName()+" a las "+sdf.format(cal.getTime())+". Mensaje:"+cadena+" -X="+lx+" -Y="+ly+" -Z="+lz+" -M="+l.getWorld().getName();
				Strings.add(ad);
				this.getConfig().set("Reportes", Strings);
				this.saveConfig();
				int i = 0;
				for(String S:Strings){
					if(S.equals(ad)){
					break;
					}else{
					i++;
					}
				}				
				sender.sendMessage(ChatColor.GREEN+getString("thanks"));
				for(Player playerr : this.getServer().getOnlinePlayers()){
					if(playerr.hasPermission("Catreport.op") & Strings.size()!=0){
						String mes = getString("send_notif");
						mes = mes.replace("(n)", (i+1)+"");
						playerr.sendMessage(ChatColor.GOLD+mes);
					}
				}
				return true;
			}
			sender.sendMessage("Uso del comando: /report <menssaje>");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("check")){
			if(sender.hasPermission("Catreport.check")){				
			
			if(args.length==0){
			int i=0;
			if(Strings.size() == 0){
				sender.sendMessage(ChatColor.GREEN+getString("no_reports"));
				return true;
			}
			for(String cadena:Strings){
				i++;
				int x = cadena.lastIndexOf("-X=");
				int Imensaje = cadena.indexOf("Mensaje:");
				int Ifecha = cadena.indexOf("a las");
				int Iusuario = cadena.indexOf("por:");					
				String usuario = cadena.substring(Iusuario+5,Ifecha-1);
				String fecha = cadena.substring(Ifecha+7,Imensaje-2);
				String mensaje = cadena.substring(Imensaje+8,x-1);
				usuario = ChatColor.GREEN+usuario+ChatColor.RESET;
				fecha = ChatColor.GREEN+fecha+ChatColor.RESET;
				mensaje = ChatColor.BLUE+mensaje+ChatColor.RESET;
				String salida;
				salida = "["+i+"]: "+fecha+" -> "+usuario+" -> "+mensaje;
				if(salida.length()>65){
					 salida = salida.substring(0, 64)+"...";
				}				
				sender.sendMessage(salida);
				salida ="";
				mensaje = "";
				if(i>40){					
					sender.sendMessage(ChatColor.RED+getString("end_list"));
					return true;
				}
			}
			sender.sendMessage("For more info type /check <id>");
			return true;
			}else{
			if(args.length==1){
				int i=0;
				for(String cadena:Strings){
					i++;				
					if (i == Integer.parseInt(args[0])){
					int x = cadena.lastIndexOf("-X=");
					int Imensaje = cadena.indexOf("Mensaje:");
					int Ifecha = cadena.indexOf("a las");
					int Iusuario = cadena.indexOf("por:");					
					String usuario = cadena.substring(Iusuario+5,Ifecha-1);
					String fecha = cadena.substring(Ifecha+7,Imensaje-2);
					String mensaje = cadena.substring(Imensaje+8,x-1);
					String[] salida = new String[5];
					salida[0] = ChatColor.RED+"----Report "+i+"----"+ChatColor.WHITE;
					salida[1] = "-Send by: "+ChatColor.GREEN+usuario+ChatColor.RESET;
					salida[2] = "-Date: "+ChatColor.GREEN+fecha+ChatColor.RESET;
					salida[3] = "-Menssaje: "+ChatColor.BLUE+mensaje+ChatColor.RESET;
					salida[4] = "-To go to the report location type: /go "+i+"";
					sender.sendMessage(salida);
					return true;
					}
				}
				sender.sendMessage(ChatColor.RED+getString("not_found"));
				return true;
			}
			}
			}else{
				sender.sendMessage(ChatColor.RED+getString("no_perms"));
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("delete")){
			if(sender.hasPermission("Catreport.delete")){
			if(args.length == 1){
			int i=0;
			for(@SuppressWarnings("unused") String cadena:Strings){
				i++;				
				if (i == Integer.parseInt(args[0])){
				Strings.remove(i-1);
				sender.sendMessage(ChatColor.GREEN+getString("delete"));
				this.getConfig().set("Reportes", Strings);
				this.saveConfig();
				return true;
				}
			}
			}
			sender.sendMessage(ChatColor.RED+getString("not_found"));
			return true;
			}else{
				sender.sendMessage(ChatColor.RED+getString("no_perms"));
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("go")){
			if(sender.hasPermission("Catreport.go")){
			if(args.length==1 & sender instanceof Player){
				Player player = (Player) sender;
				int i=0;
				for(String cadena:Strings){
					i++;				
					if (i == Integer.parseInt(args[0])){
					int x = cadena.lastIndexOf("-X=");
					int y = cadena.lastIndexOf("-Y=");
					int z = cadena.lastIndexOf("-Z=");
					int m = cadena.lastIndexOf("-M=");
				
					String x1 =cadena.substring(x+3,y-1);
					String x2 =cadena.substring(y+3,z-1);
					String x3 =cadena.substring(z+3,m-1);
					String mundo = cadena.substring(m+3);
					World mundow = this.getServer().getWorld(mundo);
					int x1i = Integer.parseInt(x1);
					int x2i = Integer.parseInt(x2);
					int x3i = Integer.parseInt(x3);
					Location loc = new Location(mundow,x1i,x2i,x3i);
					player.teleport(loc);
					return true;
					}
				}
			}
		}else{
			sender.sendMessage(ChatColor.RED+getString("no_perms"));
			return true;
		}		 
		}
		return false;
	}
	
}
