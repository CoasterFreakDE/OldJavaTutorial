package de.coaster.commands;

import java.time.OffsetDateTime;

import de.coaster.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription("**Hilfe zum Bot** \n"
				+ "*Was brauchst du denn für hilfe, wir haben doch noch gar nichts xD*\n"
				+ "*Was brauchst du denn für hilfe, wir haben doch noch gar nichts xD*\n"
				+ "**!clear** - *Löscht die letzen x Nachrichten*");
		builder.setColor(0x23cba7);
		builder.setFooter("Powered by us.");
		builder.setTimestamp(OffsetDateTime.now());
		
		
		m.getUser().openPrivateChannel().queue((ch) -> {
			ch.sendMessage(builder.build()).queue();
		});
		
		
	}

	
	
}
