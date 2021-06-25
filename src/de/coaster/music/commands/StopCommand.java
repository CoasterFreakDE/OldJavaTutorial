package de.coaster.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.coaster.DiscordTutorial;
import de.coaster.commands.types.ServerCommand;
import de.coaster.music.MusicController;
import de.coaster.music.MusicUtil;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		GuildVoiceState state;
		if((state = m.getVoiceState()) != null) {
			VoiceChannel vc;
			if((vc = state.getChannel()) != null) {
				MusicController controller = DiscordTutorial.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
				AudioManager manager = vc.getGuild().getAudioManager();
				AudioPlayer player = controller.getPlayer();
				MusicUtil.updateChannel(channel);
				player.stopTrack();
				manager.closeAudioConnection();
				message.addReaction("U+1F44C").queue();
			}
		}	
	}
}
