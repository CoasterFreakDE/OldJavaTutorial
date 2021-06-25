package de.coaster.music;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.coaster.DiscordTutorial;
import de.coaster.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {

	@Override
	public void onPlayerPause(AudioPlayer player) {
		
	}
	
	@Override
	public void onPlayerResume(AudioPlayer player) {
		
	}
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		long guildid = DiscordTutorial.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());
		Guild guild = DiscordTutorial.INSTANCE.shardMan.getGuildById(guildid);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.decode("#00e640"));
		AudioTrackInfo info = track.getInfo();
		builder.setDescription(guild.getJDA().getEmoteById(599170445287096322l).getAsMention() + " Jetzt läuft: " + info.title);
		
		long sekunden = info.length/1000;
		long minuten = sekunden / 60;
		long stunden = minuten / 60;
		minuten %= 60;
		sekunden %= 60;
		
		String url = info.uri;
		builder.addField(info.author, "[" + info.title + "](" + url + ")", false);
		builder.addField("Länge", info.isStream ? ":red_circle: STREAM" : (stunden > 0 ? stunden + "h " : "") + minuten + "min " + sekunden + "s", true);
		
		if(url.startsWith("https://www.youtube.com/watch?v=")) {
			String videoID = url.replace("https://www.youtube.com/watch?v=", "");
			
			InputStream file;
			try {
				file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
				builder.setImage("attachment://thumbnail.png");
				
				ResultSet set = LiteSQL.onQuery("SELECT * FROM musicchannel WHERE guildid = " + guildid);
				
				try {
					if(set.next()) {
						long channelid = set.getLong("channelid");
						
						TextChannel channel;
						if((channel = guild.getTextChannelById(channelid)) != null) {
							channel.sendTyping().queue();
							channel.sendFile(file, "thumbnail.png").embed(builder.build()).queue();
						}
					}
				} catch(SQLException ex) { }
			}catch(IOException ex) { }
		}
		else {
			MusicUtil.sendEmbed(guildid, builder);
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		long guildid = DiscordTutorial.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());
		Guild guild = DiscordTutorial.INSTANCE.shardMan.getGuildById(guildid);
		
		if(endReason.mayStartNext) {
			MusicController controller = DiscordTutorial.INSTANCE.playerManager.getController(guildid);
			Queue queue = controller.getQueue();
			
			if(queue.next())
				return;
		}
		
		AudioManager manager = guild.getAudioManager();
		player.stopTrack();
		manager.closeAudioConnection();
	}
	
	
	
}
