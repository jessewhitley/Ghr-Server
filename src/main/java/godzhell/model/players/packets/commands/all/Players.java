package godzhell.model.players.packets.commands.all;

import godzhell.Config;
import godzhell.model.players.Player;
import godzhell.model.players.PlayerHandler;
import godzhell.model.players.packets.commands.Command;


public class Players extends Command {
	
	@Override
	public void execute(Player c, String input) {
		for (int i = 35488; i < 35665; i++) {
			c.getPA().sendFrame126("", i);
			c.getPlayerAssistant().sendFrame126("", 35486);
		}
			c.sendMessage("@cr10@ There are currently: [ @blu@" + PlayerHandler.getPlayerCount() + " @bla@] Players Online. ");
			c.getPA().sendFrame126(Config.SERVER_NAME+" - Online Players", 35486);
			c.getPA().sendFrame126("Online players(" + PlayerHandler.getPlayerCount()+ "):", 35488);
			int line = 35489;
			for (int i = 1; i < Config.MAX_PLAYERS; i++) {
				Player p = c.getClient(i);
				if (!c.validClient(i))
					continue;
				if (p.playerName != null) {
					String title = "";
					
					title += "level-" + p.combatLevel;
					String extra = "(" + (i) + ") ";
					if(c.getRights().getPrimary().isOwner()) {
						c.getPA().sendFrame126("@dre@" + extra + p.playerName + " ("+ title + ") is at " + p.absX + ", "+ p.absY, line);
					} else {
						c.getPA().sendFrame126("@dre@" + extra + p.playerName + " ("+ title + ")", line);
					}
					line++;
				}
			}
			c.getPA().showInterface(35483);
			c.flushOutStream();
		}
	
	
}
