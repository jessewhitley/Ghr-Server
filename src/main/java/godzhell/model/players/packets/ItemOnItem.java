package godzhell.model.players.packets;

/**
 * @author Ryan / Lmctruck30
 */

import java.util.Objects;

import godzhell.Server;
import godzhell.model.items.UseItem;
import godzhell.model.multiplayer_session.MultiplayerSessionFinalizeType;
import godzhell.model.multiplayer_session.MultiplayerSessionStage;
import godzhell.model.multiplayer_session.MultiplayerSessionType;
import godzhell.model.multiplayer_session.duel.DuelSession;
import godzhell.model.players.PacketType;
import godzhell.model.players.Player;

public class ItemOnItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int usedWithSlot = c.getInStream().readUnsignedWord();
		int itemUsedSlot = c.getInStream().readUnsignedWordA();
		if (usedWithSlot > c.playerItems.length - 1 || usedWithSlot < 0 || itemUsedSlot > c.playerItems.length - 1 || itemUsedSlot < 0) {
			return;
		}
		int useWith = c.playerItems[usedWithSlot] - 1;
		int itemUsed = c.playerItems[itemUsedSlot] - 1;
		if (useWith == -1 || itemUsed == -1) {
			return;
		}
		if (!c.getItems().playerHasItem(useWith, 1) || !c.getItems().playerHasItem(itemUsed, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		c.getSkilling().stop();
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		UseItem.ItemonItem(c, itemUsed, useWith, itemUsedSlot, usedWithSlot);
	}

}
