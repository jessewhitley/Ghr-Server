package godzhell.model.players.packets.commands.admin;

import godzhell.model.content.dialogue.impl.EmptyDialogue;
import godzhell.model.players.Player;
import godzhell.model.players.packets.commands.Command;

/**
 * Empty the inventory of the player.
 * 
 * @author Emiel
 */
public class Empty extends Command {

	@Override
	public void execute(Player c, String input) {
c.start(new EmptyDialogue());
	}
}
