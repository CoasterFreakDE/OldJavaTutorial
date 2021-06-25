package de.coaster.games;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class CounterGame {

	public static String PREFIX = ":banana: Next Number: ";
	
	public static void countUpdate(TextChannel channel, Message message) {
		
		int zahl = -1;
		
		try {
			zahl = Integer.parseInt(message.getContentDisplay());
		} catch(NumberFormatException ex) { 
			message.delete().queue();
			return;
		}
		
		//Nächste Zahl: 1
		String title = channel.getTopic();
		
		if(title != null && title.startsWith(PREFIX)) {
			String nextS = title.replaceFirst(PREFIX, "");
			int next = -1;
			
			try {
				next = Integer.parseInt(nextS);
			} catch(NumberFormatException ex) {
				setupChannel(channel);
				return;
			}
			
			if(zahl != next) {
				message.delete().queue();
				return;
			}
			
			channel.getManager().setTopic(PREFIX + (next+1)).queue();
		}
		else {
			setupChannel(channel);
		}
	}
	
	
	public static void setupChannel(TextChannel channel) {
		channel.getHistory();
		channel.purgeMessages(MessageHistory.getHistoryFromBeginning(channel).complete().getRetrievedHistory());
		
		channel.getManager().setTopic(PREFIX + "1").queue();
	}
	
}
