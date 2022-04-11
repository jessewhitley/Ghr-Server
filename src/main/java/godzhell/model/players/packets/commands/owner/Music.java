package godzhell.model.players.packets.commands.owner;

import godzhell.model.players.Player;
import godzhell.model.players.packets.commands.Command;

public class Music extends Command {


	@Override
	public void execute(Player player, String input) {
		int id = Integer.parseInt(input);
		player.getPA().playMusic(id);
		

	}

}
