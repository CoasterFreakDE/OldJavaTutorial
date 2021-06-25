package de.coaster.music;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.coaster.DiscordTutorial;
import de.coaster.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicUtil {

	public static void updateChannel(TextChannel channel) {
		ResultSet set = LiteSQL.onQuery("SELECT * FROM musicchannel WHERE guildid = " + channel.getGuild().getIdLong());
		
		try {
			if(set.next()) {
				LiteSQL.onUpdate("UPDATE musicchannel SET channelid = " + channel.getIdLong() + " WHERE guildid = " + channel.getGuild().getIdLong());
			}
			else {
				LiteSQL.onUpdate("INSERT INTO musicchannel(guildid, channelid) VALUES(" + channel.getGuild().getIdLong() + "," + channel.getIdLong() +  ")");
			}
		} catch(SQLException ex) { }
	}
	
	public static void sendEmbed(long guildid, EmbedBuilder builder) {
		TextChannel channel;
		if((channel = getMusicChannel(guildid)) != null) {
			channel.sendMessage(builder.build()).queue();
		}
	}
	
	public static TextChannel getMusicChannel(long guildid) {
		ResultSet set = LiteSQL.onQuery("SELECT * FROM musicchannel WHERE guildid = " + guildid);
		
		try {
			if(set.next()) {
				long channelid = set.getLong("channelid");
				
				Guild guild;
				if((guild = DiscordTutorial.INSTANCE.shardMan.getGuildById(guildid)) != null) {
					TextChannel channel;
					if((channel = guild.getTextChannelById(channelid)) != null) {
						return channel;
					}
				}
			}
		} catch(SQLException ex) { }
		return null;
	}
	
}
