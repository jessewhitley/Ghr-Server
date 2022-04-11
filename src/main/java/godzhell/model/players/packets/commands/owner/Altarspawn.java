package godzhell.model.players.packets.commands.owner;

import godzhell.model.players.Player;
import godzhell.model.players.packets.commands.Command;

/**
 * Update the shops.
 * 
 * @author Emiel
 *
 */
public class Altarspawn extends Command {

	@Override
	public void execute(Player player, String input) {
		player.getSkotizo().skotizoSpecials();
	}
}
