package ethos.model.players.packets.commands.admin;

import ethos.model.players.Player;
import ethos.model.players.packets.commands.Command;
import ethos.model.content.skills.construction.House;

public class Pohenter extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.getHouse() == null) {
			c.sendMessage("You do not have a house loaded.");
			return;
		}

		House house = c.getHouse();
		house.setBuildMode(false);
		house.enter(c);
	}

}
