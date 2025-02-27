package godzhell.model.players.packets.commands.owner;

import godzhell.model.players.Player;
import godzhell.model.players.packets.commands.Command;

public class Camera extends Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		
		c.sendMessage("Args: " + args.length + " arg0: " + args[0]);
		
		
		if (args.length <= 1) {
			c.getPA().resetCamera();
			c.sendMessage("Resetting camera.");
		}
		if(Integer.parseInt(args[0]) == 1337) {
			c.getPA().sendScreenFade("test", -1, 5);
			return;
		}
		c.getPA().stillCamera(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		c.sendMessage("Setting camera to: X: " + Integer.parseInt(args[0]) + ", "
				+ "Y: " + Integer.parseInt(args[1]) + ", "
				+ "Z: " + Integer.parseInt(args[2]) + ", "
				+ "SPEED: " + Integer.parseInt(args[3]) + ", "
				+ "ANGLE: " + Integer.parseInt(args[4]));
	}

}
