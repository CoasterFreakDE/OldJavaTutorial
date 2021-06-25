package de.coaster.music.commands;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.coaster.DiscordTutorial;
import de.coaster.commands.types.ServerCommand;
import de.coaster.music.AudioLoadResult;
import de.coaster.music.MusicController;
import de.coaster.music.MusicUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		String[] args = message.getContentDisplay().split(" ");
		
		if(args.length > 1) {
			GuildVoiceState state;
			if((state = m.getVoiceState()) != null) {
				VoiceChannel vc;
				if((vc = state.getChannel()) != null) {
					MusicController controller = DiscordTutorial.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
					AudioPlayerManager apm = DiscordTutorial.INSTANCE.audioPlayerManager;
					AudioManager manager = vc.getGuild().getAudioManager();
					manager.openAudioConnection(vc);
					
					MusicUtil.updateChannel(channel);
					
					StringBuilder strBuilder = new StringBuilder();
					for(int i = 1; i < args.length; i++) strBuilder.append(args[i] + " ");
					
					String url = strBuilder.toString().trim();
					if(!url.startsWith("http")) {
						url = "ytsearch: " + url;
					}
					
					apm.loadItem(url, new AudioLoadResult(controller, url));
				}
				else {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Huch? Du bist wohl in keinem VoiceChannel.");
					builder.setColor(Color.decode("#f22613"));
					channel.sendMessage(builder.build()).queue();
				}
			}
			else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setDescription("Huch? Du bist wohl in keinem VoiceChannel.");
				builder.setColor(Color.decode("#f22613"));
				channel.sendMessage(builder.build()).queue();
			}
		}
		else {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription("Bitte benutze !play <url/search query>");
			builder.setColor(Color.decode("#f22613"));
			channel.sendMessage(builder.build()).queue();
		}
	}
}
