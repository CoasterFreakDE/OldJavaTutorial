package de.coaster.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import de.coaster.DiscordTutorial;
import de.coaster.commands.types.ServerCommand;
import de.coaster.manage.LiteSQL;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;

public class StatschannelCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		if(m.hasPermission(Permission.ADMINISTRATOR)) {
			String[] args = message.getContentDisplay().split(" ");
		
				Guild guild = channel.getGuild();
				ResultSet set = LiteSQL.onQuery("SELECT * FROM statchannels WHERE guildid = " + guild.getIdLong());
				
				try {
					if(!set.next()) {
						Category category = guild.createCategory("Statistiken").complete();
						category.getManager().setPosition(-1).queue();
						
						PermissionOverride override = new PermissionOverrideActionImpl(category.getJDA(), category, category.getGuild().getPublicRole()).complete();

						
						category.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
						
						
						LiteSQL.onUpdate("INSERT INTO statchannels(guildid, categoryid) VALUES(" + guild.getIdLong() + ", " + category.getIdLong() + ")");
						
						fillCategory(category);
					}
					else {
						long categoryid = set.getLong("categoryid");
						channel.sendMessage("Kategorie geupdated.").queue();
						Category cat = guild.getCategoryById(categoryid);
						
						
						if(args.length == 2) {
							if(args[1].equalsIgnoreCase("delete")) {
								LiteSQL.onUpdate("DELETE FROM statchannels WHERE guildid = " + guild.getIdLong());
								
								cat.getChannels().forEach(chan -> {
									chan.delete().complete();
								});
								cat.delete().queue();
								
								
								return;
							}
						}
						
						
						
						
						
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();
						});
						
						fillCategory(cat);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
	
	public static void fillCategory(Category cat) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.YYYY");
		cat.createVoiceChannel("🕗 Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
		cat.createVoiceChannel("📅 Datum: " + df2.format(Calendar.getInstance().getTime())).queue();
		
		List<Member> members = cat.getGuild().getMembers();
		cat.createVoiceChannel("📈 Server Mitglieder: " + members.size()).queue();
		int online = 0;
		
		for(Member memb : members) {
			if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
				if(!memb.getUser().isBot()) {
					online++;
				}
			}
		}
		cat.createVoiceChannel("🔘 Online User: " + online).queue();
		cat.createVoiceChannel("✅ BOT ONLINE").queue();
		
		PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), cat, cat.getGuild().getPublicRole()).complete();
		
		
		//System.out.println("OVerride: " + (override == null ? "NULL" : override.toString()));
		
		cat.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
	}

	public static void sync(Category cat) {
		cat.getChannels().forEach(chan -> {
			chan.getManager().sync().queue();
		});
	}
	
	public static void updateCategory(Category cat) {
		if(cat.getChannels().size() == 5) {
			sync(cat);
			List<GuildChannel> channels = cat.getChannels();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.YYYY");
			
			channels.get(0).getManager().setName("🕗 Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
			channels.get(1).getManager().setName("📅 Datum: " + df2.format(Calendar.getInstance().getTime())).queue();
			List<Member> members = cat.getGuild().getMembers();
			int online = 0;
			
			for(Member memb : members) {
				if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
					if(!memb.getUser().isBot()) {
						online++;
					}
				}
			}
			channels.get(2).getManager().setName("📈 Server Mitglieder: " + members.size()).queue();
			channels.get(3).getManager().setName("🔘 Online User: " + online).queue();
		}
	}
	
	public static void checkStats() {
		DiscordTutorial.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					StatschannelCommand.updateCategory(guild.getCategoryById(catid));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
	
	public static void onStartUP() {
		DiscordTutorial.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					Category cat = guild.getCategoryById(catid);
					
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					
					fillCategory(cat);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
	
	public static void onShutdown() {
		DiscordTutorial.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					Category cat = guild.getCategoryById(catid);
					
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					
					VoiceChannel offline = cat.createVoiceChannel("🔴 BOT OFFLINE").complete();
					PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), offline, cat.getGuild().getPublicRole()).complete();
					
					offline.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
}
