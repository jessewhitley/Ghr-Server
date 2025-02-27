package godzhell.model.players.packets;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import godzhell.Server;
import godzhell.model.content.help.HelpDatabase;
import godzhell.model.content.help.HelpRequest;
import godzhell.model.content.presets.Preset;
import godzhell.model.players.PacketType;
import godzhell.model.players.Player;
import godzhell.model.players.PlayerHandler;
import godzhell.model.players.Right;

public class InputField implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int id = player.inStream.readInteger();
		String text = player.inStream.readString();
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		//System.out.println("ID: " + id);
		switch (id) {
		
		case 38019: //Wogw donation amount
			player.wogwDonationAmount = Long.parseLong(text);
		//	player.getPA().sendFrame171(0, 38020);
		//	player.getPA().sendFrame126("Are you sure you want to contribute\\n" + Misc.getValueWithoutRepresentation(player.wogwDonationAmount) + " gp?", 38022);
			break;
		
			/**
			 * Player shop name
			 */
		case 28054:
			player.sendMessage("Setting player shop name to: " + text);
			break;
			
			/**
			 * Player shop description
			 */
		case 28055:
			player.sendMessage("Setting player shop description to: " + text);
			break;

		case 33205:
			player.getPunishmentPanel().setReason(text);
			break;

		case 33211:
			player.getPunishmentPanel().setDuration(Integer.parseInt(text));
			break;

		case 53536:
			if (text.length() > 16) {
				player.sendMessage("Custom title length can only be sixteen characters, no more.");
				return;
			}
			player.getTitles().setTemporaryCustomTitle(text);
			break;
			
		case 59806:
			Server.getDropManager().search(player, text);
			break;

		case 59527:
			if (text.length() < 25) {
				player.sendMessage("Your help request must contain 25 characters for the description.");
				return;
			}
			List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.HELPER)).collect(Collectors.toList());
			if (HelpDatabase.getDatabase().requestable(player)) {
				HelpDatabase.getDatabase().add(new HelpRequest(player.playerName, player.connectedFrom, text));
				if (staff.size() > 0) {
					PlayerHandler.sendMessage("[HelpDB] " + WordUtils.capitalize(player.playerName) + "" + " is requesting help, type ::helpdb to view their request.", staff);
					player.sendMessage("You request has been sent, please wait as a staff member gets back to you.");
				} else {
					player.sendMessage("There are no staff online to help you at this time, please be patient.");
				}
			}
			player.getPA().removeAllWindows();
			break;

		case 32002:
			Preset preset = player.getPresets().getCurrent();
			if (preset == null) {
				player.sendMessage("You must select a preset before changing the name.");
				return;
			}
			preset.setAlias(text);
			player.getPresets().refreshMenus(preset.getMenuSlot(), preset.getMenuSlot() + 1);
			break;

		case 58063:
			if (player.getPA().viewingOtherBank) {
				player.getPA().resetOtherBank();
				return;
			}
			if (player.isBanking) {
				player.getBank().getBankSearch().setText(text);
				player.getBank().setLastSearch(System.currentTimeMillis());
				if (text.length() > 2) {
					player.getBank().getBankSearch().updateItems();
					player.getBank().setCurrentBankTab(player.getBank().getBankSearch().getTab());
					player.getItems().resetBank();
					player.getBank().getBankSearch().setSearching(true);
				} else {
					if (player.getBank().getBankSearch().isSearching())
						player.getBank().getBankSearch().reset();
					player.getBank().getBankSearch().setSearching(false);
				}
			}
			break;


		default:
			break;
		}
	}

}
