package de.coaster.commands;

import java.awt.Color;

import de.coaster.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RoleCreation implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		//  !createrole <Name> #12ff22
		
		Guild guild = channel.getGuild();
		String[] args = message.getContentDisplay().split(" ");
		int length = args.length;
		
		if(length > 1) {
			StringBuilder builder = new StringBuilder();
			
			if(args[length-1].startsWith("#") && length > 2) {
				for(int i = 1; i < length-1; i++) builder.append(args[i] + " ");
				String hexCode = args[length-1];
				
				String roleName = builder.toString().trim();
				channel.sendTyping().queue();
				
				guild.createRole().queue(role -> {
					Color color = Color.decode(hexCode);
					role.getManager().setName(roleName).setColor(color).setPermissions(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VOICE_CONNECT).queue();
					EmbedBuilder embed = new EmbedBuilder();
					embed.setDescription("Rolle " + roleName + " erstellt.");
					embed.setColor(color);
					channel.sendMessage(embed.build()).queue();
				});
			}
			else {
				for(int i = 1; i < length; i++) builder.append(args[i] + " ");
				
				String roleName = builder.toString().trim();
				channel.sendTyping().queue();
				
				guild.createRole().queue(role -> {
					Color color = new Color(255, 255, 255);
					role.getManager().setName(roleName).setColor(color).setMentionable(true).setPermissions(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VOICE_CONNECT).queue();
					EmbedBuilder embed = new EmbedBuilder();
					embed.setDescription("Rolle " + roleName + " erstellt.");
					embed.setColor(color);
					channel.sendMessage(embed.build()).queue();
				});
			}
		}
		else {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription("Bist du doof? !createrole <Name> <#Farbe>");
			channel.sendMessage(builder.build()).queue();
		}
	}
}
