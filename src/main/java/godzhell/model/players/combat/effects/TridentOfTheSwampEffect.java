package godzhell.model.players.combat.effects;

import java.util.Optional;

import godzhell.model.entity.HealthStatus;
import godzhell.model.npcs.NPC;
import godzhell.model.players.Player;
import godzhell.model.players.combat.Damage;
import godzhell.model.players.combat.DamageEffect;
import godzhell.util.Misc;

public class TridentOfTheSwampEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, damage.getAmount(), Optional.of(attacker));
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, damage.getAmount(), Optional.of(attacker));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return operator.getItems().isWearingItem(12899) && Misc.random(3) == 0;
	}

}
