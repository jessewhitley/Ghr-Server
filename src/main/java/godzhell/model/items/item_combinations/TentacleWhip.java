package godzhell.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import godzhell.model.items.GameItem;
import godzhell.model.items.ItemCombination;
import godzhell.model.players.Player;

public class TentacleWhip extends ItemCombination {

	public TentacleWhip(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(super.outcome.getId(), super.outcome.getAmount());
		player.getDH().sendItemStatement("You combined the items and created the Tentacle Whip.", 12006);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Once the tentacle is combined with the abyssal whip", "there is no going back. The whip will be lost forever.",
				"You will however receive the tentacle.");
	}

}
