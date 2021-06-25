package de.coaster.commands;

import de.coaster.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PreviewCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		
		
		//!preview `fdfsx` 
		
		String mess = message.getContentRaw().substring(9);
		
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setDescription(mess);
		builder.setColor(0xeb974e);
		
		message.delete().queue();
		channel.sendMessage(builder.build()).queue();
	}

}
