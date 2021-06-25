package de.coaster.commands;


import java.util.List;

import de.coaster.commands.types.ServerCommand;
import de.coaster.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactRolesCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		//args[0] args[1]   args[2]     args[3] args[4]
 		//!reactrole #channel 0223948232352  :ok:     @Rolle
		
		String[] args = message.getContentDisplay().split(" ");
		
		

			List<TextChannel> channels = message.getMentionedChannels();
			List<Role> roles = message.getMentionedRoles();
			List<Emote> emotes = message.getEmotes();
			
			
			if(!channels.isEmpty() && !roles.isEmpty()) {
				TextChannel tc = channels.get(0);
				Role role = roles.get(0);
				String messageIDString = args[2];

				
				try {
					long messageID = Long.parseLong(messageIDString);
					
					if(!emotes.isEmpty()) {
						Emote emote = emotes.get(0);
						tc.addReactionById(messageID, emote).queue();
						LiteSQL.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, rollenid) VALUES(" +
							channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote.getId() + "', " + role.getIdLong() + ")");
					}
					else {
						String emote = args[3];
						tc.addReactionById(messageID, emote).queue();
						LiteSQL.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, rollenid) VALUES(" +
								channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote + "', " + role.getIdLong() + ")");
					}
				} catch (NumberFormatException e) { 
					e.printStackTrace();
				}
			}
			else {
				channel.sendMessage("Bitte benutze `!reactrole #channel messageID :emote: @Rolle`").queue();
			}
	}

}
