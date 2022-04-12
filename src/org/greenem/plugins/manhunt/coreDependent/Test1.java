package org.greenem.plugins.manhunt.coreDependent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Test1 {
	public static void authors(CommandSender sender) {
        String discordLinkText = "(no link)";
        String curseforgeLinkText = "https://www.curseforge.com/minecraft/bukkit-plugins/advanced-manhunt";
        String line = "-------------------------------------------------------------------------------";
        String endLine = "\n" + ChatColor.RED + line;

        try {
            TextComponent dfgfg = new TextComponent();
        } catch (NoClassDefFoundError e) {
            System.out.println("Catch");
        }

        TextComponent enrireMessage = null;
        String simpleText = null;

        try {
            enrireMessage = new TextComponent();
        } catch (NoClassDefFoundError e) {
            simpleText = "";
        }

        String part1 = ChatColor.RED + line + ChatColor.RESET + "\n";
        part1 += ChatColor.WHITE + "" + ChatColor.BOLD + "Info about Advanced Manhunt plugin" + ChatColor.RESET + "\n";
        part1 += ChatColor.WHITE + "Author: " + ChatColor.RED + "" + ChatColor.BOLD + "Greenem" + ChatColor.RESET + "\n";
        part1 += ChatColor.GRAY + "Social links: " + ChatColor.RESET;
        part1 += ChatColor.WHITE + "Discord server: " + ChatColor.RESET;

        String discordPartText = ChatColor.GRAY + "" + ChatColor.BOLD + discordLinkText;
        TextComponent discordLink = null;
        if(simpleText==null) {
            discordLink = new TextComponent(discordPartText);
//          discordLink.setClickEvent(new ClickEvent( ClickEvent.Action.OPEN_URL, discordLinkText));
          }

          String part2 = ChatColor.GRAY + "\n" + "Download links: " + ChatColor.RESET + "\n";
          part2 += ChatColor.WHITE + "CurseForge: " + ChatColor.RESET;

          String curseforgePartText = ChatColor.GRAY + "" + ChatColor.BOLD + curseforgeLinkText;
          TextComponent curseforgeLink = null;
          if(simpleText==null) {
              curseforgeLink = new TextComponent(curseforgePartText);
              curseforgeLink.setClickEvent(new ClickEvent( ClickEvent.Action.OPEN_URL, curseforgeLinkText));
          }
          if(simpleText==null) {
              enrireMessage.addExtra(part1);
              enrireMessage.addExtra(discordLink);
              enrireMessage.addExtra(part2);
              enrireMessage.addExtra(curseforgeLink);
              enrireMessage.addExtra(endLine); // can add something if needed
              sender.spigot().sendMessage(enrireMessage); //does this work on bukkit?
          } else {
              sender.sendMessage(part1 + discordLinkText + part2 + curseforgeLinkText +
                      "\n" + ChatColor.GRAY + "Your server doesnt' support clickable text in chat" + endLine);
          }

//          clickable.setHoverEvent(new HoverEvent()); // to do later, show that player's name
      }
}
