package de.coaster.commands;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import de.coaster.commands.types.ServerCommand;
import de.coaster.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MonsterBreeding implements ServerCommand {

	
	class Monster {
		String monster1;
		String monster2;
		String result;
	}
	
	
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
        int i = 0;
        String monstername = message.getContentDisplay().replace("ml!breed ", "");
        List<Monster> monsters = new ArrayList<>();
        
        
        ResultSet rs = LiteSQL.onQuery("SELECT * FROM 'MONSTERZUCHT' WHERE monsterresultname = '"+monstername+"';");
        try {
            while ( rs.next() ) {
                  int id = rs.getInt("id");
                  String monster1name = rs.getString("monster1name");
                  String  monster2name = rs.getString("monster2name");
                  String  monsterresultname = rs.getString("monsterresultname");
                  i++;
                  
                  //System.out.println(i + "ID" +id+ ": " +monster1name+ " + " +monster2name+ " = " +monsterresultname);
                      
                  Monster mon = new Monster();
                  mon.monster1 = monster1name;
                  mon.monster2 = monster2name;
                  mon.result = monsterresultname;
                  monsters.add(mon);
                  
                  

                  if (i == 0) {
                      System.out.println("error");
                  } else {
                      System.out.println(i + "ID" +id+ ": " +monster1name+ " + " +monster2name+ " = " +monsterresultname);
                  }
            }
        } catch (Exception e) { }
        
        EmbedBuilder builder = new EmbedBuilder();
        String description = "Monster1 | Monster2 | Result \n";
        for(Monster monster : monsters) {
        	description += monster.monster1 + " | " + monster.monster2 + " | " + monster.result + " \n";
        }
        
        builder.setDescription(description);
        channel.sendMessage(builder.build()).queue();
	}
}
