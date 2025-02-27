package godzhell.model.objects.functions;

import godzhell.Server;
import godzhell.clip.Region;
import godzhell.event.CycleEvent;
import godzhell.event.CycleEventContainer;
import godzhell.event.CycleEventHandler;
import godzhell.model.players.Player;
import godzhell.world.objects.GlobalObject;

/**
 * @author darkside1222
 */

public class FlourMill {

	public final static int EMPTY_POT = 1931, POT_OF_FLOUR = 1933,
			GRAIN = 1947, EMPTY_FLOUR_BIN = 1781, FULL_FLOUR_BIN = 1782;
	/**
	 * Limits the amount of flour. (RS-Limit = 30)
	 */
	public static int LIMIT = 30;

	/**
	 * Item on object.(Use grain on hopper)
	 * 
	 * @param c
	 */
	public static void grainOnHopper(Player c, int objectID, int itemId) {
		if (itemId == GRAIN) {
			if(!c.getItems().playerHasItem(GRAIN)) {
				c.sendMessage("You don't have any Wheat to put in the hopper.");
				return;
			}
			// Grain amount - flour amount. Prevents putting more than 30
			if (c.grain == LIMIT - c.flourAmount || c.flourAmount == LIMIT) {
				c.sendMessage(
						"You can't put anymore grain into the hopper.");
				return;
			}
			c.startAnimation(832);
			c.getItems().deleteItem2(GRAIN, 1);
			c.grain++;// + 1
			c.sendMessage(
					"You put the grain in the hopper.");
		} else {
			c.sendMessage("Nothing interesting happens.");
		}
	}
	public static void grainOnHopperAtHome(Player c, int objectID, int itemId) {
		if (itemId == GRAIN) {
			if(!c.getItems().playerHasItem(GRAIN)) {
				c.sendMessage("You don't have any Wheat to put in the hopper.");
				return;
			}
			// Grain amount - flour amount. Prevents putting more than 30
			if (c.grain == LIMIT - c.flourAmount || c.flourAmount == LIMIT) {
				c.sendMessage(
						"You can't put anymore grain into the hopper.");
				return;
			}
			c.startAnimation(832);
			c.getItems().deleteItem2(GRAIN, 1);
			c.grain++;// + 1
			c.sendMessage(
					"You put the grain in the hopper.");
		} else {
			c.sendMessage("Nothing interesting happens.");
		}
	}

	/**
	 * When player operates the lever at lumby.
	 * 
	 * @param c
	 */
	public static void hopperControl(final Player c) {
		if (c.grain > 0) {
			if (c.flourAmount == LIMIT) {
				c.sendMessage(
						"There is currently too much grain in the hopper.");
				return;
			}
			c.sendMessage(
					"You operate the hopper. The grain slides down the chute.");
			c.startAnimation(832);
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.heightLevel == 2) {
						return;
					}
					if (c.heightLevel == 0) {
						Server.getGlobalObjects().add(new GlobalObject(FULL_FLOUR_BIN, 3166, 3306, 0, 0, 10));
						Region.addObject(FULL_FLOUR_BIN, 3166, 3306, 0, 10, 0);
						container.stop();
					}
				}
				@Override
					public void stop() {
						
					}
			}, 1);
			c.flourAmount += c.grain;
			if (c.flourAmount > LIMIT) {
				c.flourAmount = LIMIT;// Flour amount returns to limit.
			}
			c.grain = 0;
		} else {
			c.startAnimation(832);
			c.sendMessage(
					"You operate the hopper. Nothing interesting happens.");
		}
	}
	public static void hopperControlAtHome(final Player c) {
		if (c.grain > 0) {
			if (c.flourAmount == LIMIT) {
				c.sendMessage(
						"There is currently too much grain in the hopper.");
				return;
			}
			c.sendMessage(
					"You operate the hopper. The grain slides down the chute.");
			c.startAnimation(832);
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.heightLevel == 2) {
						return;
					}
					if (c.heightLevel == 0) {
						Server.getGlobalObjects().add(new GlobalObject(FULL_FLOUR_BIN, 1616, 2840, 0, 0, 10));
						Region.addObject(FULL_FLOUR_BIN, 1616, 2840, 0, 10, 0);
						container.stop();
					}
				}
				@Override
					public void stop() {
						
					}
			}, 1);
			c.flourAmount += c.grain;
			if (c.flourAmount > LIMIT) {
				c.flourAmount = LIMIT;// Flour amount returns to limit.
			}
			c.grain = 0;
		} else {
			c.startAnimation(832);
			c.sendMessage(
					"You operate the hopper. Nothing interesting happens.");
		}
	}
	/**
	 * When player operates the lever at lumby.
	 * 
	 * @param c
	 */
	public static void hopperControlatHome(final Player c) {
		if (c.grain > 0) {
			if (c.flourAmount == LIMIT) {
				c.sendMessage(
						"There is currently too much grain in the hopper.");
				return;
			}
			c.sendMessage(
					"You operate the hopper. The grain slides down the chute.");
			c.startAnimation(832);
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.heightLevel == 2) {
						return;
					}
					if (c.heightLevel == 0) {
						Server.getGlobalObjects().add(new GlobalObject(FULL_FLOUR_BIN, 1616, 2840, 0, 0, 10));
						Region.addObject(FULL_FLOUR_BIN, 1616, 2840, 0, 10, 0);
						container.stop();
					}
				}
				@Override
					public void stop() {
						
					}
			}, 1);
			c.flourAmount += c.grain;
			if (c.flourAmount > LIMIT) {
				c.flourAmount = LIMIT;// Flour amount returns to limit.
			}
			c.grain = 0;
		} else {
			c.startAnimation(832);
			c.sendMessage(
					"You operate the hopper. Nothing interesting happens.");
		}
	}

	/**
	 * Emptys the flour bin...
	 * 
	 * @param c
	 */
	public static void emptyFlourBin(Player c) {
		if (c.getItems().playerHasItem(EMPTY_POT, 1)
				&& c.flourAmount > 0) {
			c.getItems().deleteItem(EMPTY_POT, 1);
			c.getItems().addItem(POT_OF_FLOUR, 1);
			c.sendMessage(
					"You fill a pot with flour from the bin.");
			c.flourAmount--;
			if (c.flourAmount < 0) {
				c.flourAmount = 0;
			}
			if (c.flourAmount == 0) {
				Server.getGlobalObjects().add(new GlobalObject(EMPTY_FLOUR_BIN, 3166, 3306, 0, 0, 10));
				Region.addObject(EMPTY_FLOUR_BIN, 3166, 3306, 0, 10, 0);
				c.sendMessage(
						"The flour bin is now empty.");
			}
		} else {
			c.sendMessage(
					"You don't have an empty pot to fill flour with.");
		}
	}
	public static void emptyFlourBinatHome(Player c) {
		if (c.getItems().playerHasItem(EMPTY_POT, 1)
				&& c.flourAmount > 0) {
			c.getItems().deleteItem(EMPTY_POT, 1);
			c.getItems().addItem(POT_OF_FLOUR, 1);
			c.sendMessage(
					"You fill a pot with flour from the bin.");
			c.flourAmount--;
			if (c.flourAmount < 0) {
				c.flourAmount = 0;
			}
			if (c.flourAmount == 0) {
				Server.getGlobalObjects().add(new GlobalObject(EMPTY_FLOUR_BIN, 1616, 2840, 0, 0, 10));
				Region.addObject(EMPTY_FLOUR_BIN, 1616, 2840, 0, 10, 0);
				c.sendMessage(
						"The flour bin is now empty.");
			}
		} else {
			c.sendMessage(
					"You don't have an empty pot to fill flour with.");
		}
	}
}