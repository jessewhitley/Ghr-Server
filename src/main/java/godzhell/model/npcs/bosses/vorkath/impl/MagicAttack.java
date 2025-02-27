package godzhell.model.npcs.bosses.vorkath.impl;

import godzhell.model.npcs.NPCHandler;
import godzhell.model.npcs.bosses.vorkath.VorkathAttack;
import godzhell.model.players.Player;
import godzhell.model.players.combat.CombatType;

public class MagicAttack implements VorkathAttack{
	@Override
	public void execute(Player player) {
		if(player.debugMessage) {
			player.sendMessage("Attacking with magicattack");
		}
		NPCHandler.startAnimation(getAnimation(), NPCHandler.npcs[player.getVorkath().getIndex()]);
		
		int nX = NPCHandler.npcs[player.getVorkath().getIndex()].getX() + NPCHandler.offset(player.getVorkath().getIndex());
		int nY = NPCHandler.npcs[player.getVorkath().getIndex()].getY() + NPCHandler.offset(player.getVorkath().getIndex());
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nX - pX) * -1;
		int offY = (nY - pY) * -1;
		int centerX = nX + NPCHandler.npcs[player.getVorkath().getIndex()].getSize() / 2;
		int centerY = nY + NPCHandler.npcs[player.getVorkath().getIndex()].getSize() / 2;
		player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, 85,
				getProjectileId(), 60,
				15, -player.getIndex() - 1, 65,
				0);
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.DRAGON_FIRE;
	}

	@Override
	public int getProjectileId() {
		return 1479;
	}

	@Override
	public int getEndProjectileId() {
		return 1480;
	}

	@Override
	public int getAnimation() {
		return 7952;
	}

	@Override
	public void executeAfterhit(Player player) {
		player.gfx(getEndProjectileId(), 65);
	}

	@Override
	public boolean appliesDamage(Player player) {
		if(player.prayerActive[16]) {
			return false;
		}
		
		return true;
	}

	@Override
	public int getMaxHit() {
		return 72;
	}

}
