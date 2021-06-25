package de.coaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.coaster.commands.StatschannelCommand;
import de.coaster.listener.CommandListener;
import de.coaster.listener.JoinListener;
import de.coaster.listener.ReactionListener;
import de.coaster.listener.VoiceListener;
import de.coaster.manage.DONOTOPEN;
import de.coaster.manage.LiteSQL;
import de.coaster.manage.SQLManager;
import de.coaster.music.PlayerManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DiscordTutorial {

	public static DiscordTutorial INSTANCE;
	
	public ShardManager shardMan;
	private CommandManager cmdMan;
	private Thread loop;
	public AudioPlayerManager audioPlayerManager;
	public PlayerManager playerManager;
	
	public static void main(String[] args)  {
		try {
			new DiscordTutorial();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public DiscordTutorial() throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		
		LiteSQL.connect();
		SQLManager.onCreate();
		
		DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
		
		builder.setToken(DONOTOPEN.token);
		
		builder.setActivity(Activity.playing("leben."));
		builder.setStatus(OnlineStatus.ONLINE);
		
		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();
		
		this.cmdMan = new CommandManager();
		
		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new JoinListener());
		
		shardMan = builder.build();
		System.out.println("Bot online.");
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
		
		
		shutdown();
		runLoop();
		
	}
	
	
	
	public void shutdown() {
		
		new Thread(() -> {
			
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				while((line = reader.readLine()) != null) {
					
					if(line.equalsIgnoreCase("exit")) {
						shutdown = true;
						if(shardMan != null) {
							StatschannelCommand.onShutdown();
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							LiteSQL.disconnect();
							System.out.println("Bot offline.");
						}
						
						if(loop != null) {
							loop.interrupt();
						}
						
						reader.close();
						break;
					}
					else if(line.equalsIgnoreCase("info")) {
						for(Guild guild : shardMan.getGuilds()) {
							System.out.println(guild.getName() + " " + guild.getIdLong());
						}
					}
					else {
						System.out.println("Use 'exit' to shutdown.");
					}
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
		}).start();
	}
	
	public boolean shutdown = false;
	public boolean hasStarted = false;
	
	public void runLoop() {
		this.loop = new Thread(() -> {
			
			long time = System.currentTimeMillis();
			
			while(!shutdown) {
				if(System.currentTimeMillis() >= time + 1000) {
					time = System.currentTimeMillis();
					onSecond();
				}
			}
		});
		this.loop.setName("Loop");
		this.loop.start();
	}
	
	
	String[] status = new String[] {"programmieren.", "Discord", "viele Spiele.", "%members online."};
	int[] colors = new int[] {0xff9478, 0xd2527f, 0x00b5cc, 0x19b5fe, 0x2ecc71, 0x23cba7, 0x00e640, 0x8c14fc, 0x9f5afd, 0x663399};
	int next = 30;
	
	public void onSecond() {
		//System.out.println("Next: " + next);
		
		if(next%5 == 0) {
			if(!hasStarted) {
				hasStarted = true;
				StatschannelCommand.onStartUP();
			}
			
			Random rand = new Random();
			
			int color = rand.nextInt(colors.length);
			for(Guild guild : shardMan.getGuilds()) {
				Role role = guild.getRoleById(608357590035857408l);
				role.getManager().setColor(colors[color]).queue();
			}
			
			
			int i = rand.nextInt(status.length);
			
			shardMan.getShards().forEach(jda -> {
				String text = status[i].replaceAll("%members", "" + jda.getUsers().size());
				
				jda.getPresence().setActivity(Activity.playing(text));
			});
			
			StatschannelCommand.checkStats();
			
			if(next == 0) {
				next = 60;
				
				onCheckTimeRanks();
			}
			else {
				next--;
			}
		}
		else {
			next--;
		}
	}
	
	public void onCheckTimeRanks() {
		try {
			ResultSet set = LiteSQL.onQueryRAW("SELECT userid, guildid FROM timeranks WHERE ((julianday(CURRENT_TIMESTAMP) - julianday(time)) * 1000) >= 1");
			List<Long> users = new ArrayList<>();
			//int count = 0;
			while(set.next()) {
				long userid = set.getLong("userid");
				long guildid = set.getLong("guildid");
				
				Guild guild = this.shardMan.getGuildById(guildid);
				guild.removeRoleFromMember(guild.getMemberById(userid), guild.getRoleById(582279555021012992l)).complete();
				System.out.println("Role entfernt.");
				
				users.add(userid);
				
				//count++;
			}
			
			for(long userid : users) {
				LiteSQL.onUpdate("DELETE FROM timeranks WHERE userid = " + userid);
			}
			
			//System.out.println(count + " Rolen entfernt.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public CommandManager getCmdMan() {
		return cmdMan;
	}
}
