package de.coaster.commands;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.coaster.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClientInfo implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		
		channel.sendTyping().queue();
		List<Member> ment = message.getMentionedMembers();
		
		if(ment.size() > 0) {
			for(Member u : ment) {
				onInfo(m, u, channel);
			}
		}
	}
	
	public void onInfo(Member requester, Member u, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by " + requester.getAsMention());
		builder.setColor(0xfef160);
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("User: " + u.getAsMention() + "\n");
		strBuilder.append("ClientID: " + u.getId() + "\n");
		strBuilder.append("TimeJoined: " + u.getTimeJoined() + "\n");
		strBuilder.append("TimeCreated: " + u.getTimeCreated() + "\n");
		
		strBuilder.append(" \n *Rollen:* \n");
		
		StringBuilder roleBuilder = new StringBuilder();
		for(Role role : u.getRoles()) {
			roleBuilder.append(role.getAsMention() + " ");
		}
		strBuilder.append(roleBuilder.toString().trim() + "\n");
		
		
		builder.setDescription(strBuilder);
		
		channel.sendMessage(builder.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
	}
}
