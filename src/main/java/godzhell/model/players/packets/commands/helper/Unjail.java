package godzhell.model.players.packets.commands.helper;
import java.util.Optional;

import godzhell.Config;
import godzhell.Server;
import godzhell.ServerState;
import godzhell.model.players.Player;
import godzhell.model.players.PlayerHandler;
import godzhell.model.players.Right;
import godzhell.model.players.packets.commands.Command;

/**
 * Unjails a given player.
 * 
 * @author Emiel
 */
public class Unjail extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);

		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			if (!c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
				if (c2.inClanWars() || c2.inClanWarsSafe()) {
					c.sendMessage("This player is currently in pk district.");
					return;
				}
			}
			c2.getPA().movePlayer(3093, 3493, 0);
			c2.jailEnd = 0;
			c2.isStuck = false;
			c2.sendMessage("You have been unjailed by " + c.playerName + ". You can teleport out now.");
			c.sendMessage("Successfully unjailed " + c2.playerName + ".");
			if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY) {
				// TODO: Log handling
			}
		} else {
			c.sendMessage(input + " is not online. Only online players can be unjailed.");
		}
	}
}
