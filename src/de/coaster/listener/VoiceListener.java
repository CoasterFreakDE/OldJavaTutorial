package de.coaster.listener;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceListener extends ListenerAdapter {

	public List<Long> tempchannels;
	
	public VoiceListener() {
		this.tempchannels = new ArrayList<>();
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		onJoin(event.getChannelJoined(), event.getEntity());
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		onLeave(event.getChannelLeft());
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		onLeave(event.getChannelLeft());
		onJoin(event.getChannelJoined(), event.getEntity());
	}
	
	public void onJoin(VoiceChannel joined, Member memb) {
		if(joined.getIdLong() == 576016415597789194l) {
			Category cat = joined.getParent();
			VoiceChannel vc = cat.createVoiceChannel("⏳ | " + memb.getEffectiveName()).complete();			
			vc.getManager().setUserLimit(joined.getUserLimit()).queue();
			vc.getGuild().moveVoiceMember(memb, vc).queue();
			
			this.tempchannels.add(vc.getIdLong());
		}
	}
	
	public void onLeave(VoiceChannel channel) {
		if(channel.getMembers().size() <= 0) {
			if(this.tempchannels.contains(channel.getIdLong())) {
				this.tempchannels.remove(channel.getIdLong());
				channel.delete().queue();
			}
		}
	}
}
