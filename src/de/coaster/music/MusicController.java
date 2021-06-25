package de.coaster.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.coaster.DiscordTutorial;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {

	
	private Guild guild;
	private AudioPlayer player;
	private Queue queue;
	
	public MusicController(Guild guild) {
		this.guild = guild;
		this.player = DiscordTutorial.INSTANCE.audioPlayerManager.createPlayer();
		this.queue = new Queue(this);
		
		this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
		this.player.addListener(new TrackScheduler());
		this.player.setVolume(10);
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
	
	public Queue getQueue() {
		return queue;
	}
	
}
