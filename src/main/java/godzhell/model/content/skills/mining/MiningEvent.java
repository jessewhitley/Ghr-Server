package godzhell.model.content.skills.mining;

import org.apache.commons.lang3.RandomUtils;

import godzhell.Config;
import godzhell.Server;
import godzhell.event.Event;
import godzhell.model.content.ShootingStar;
import godzhell.model.content.SkillcapePerks;
import godzhell.model.content.achievement.AchievementType;
import godzhell.model.content.achievement.Achievements;
import godzhell.model.content.achievement_diary.desert.DesertDiaryEntry;
import godzhell.model.content.achievement_diary.falador.FaladorDiaryEntry;
import godzhell.model.content.achievement_diary.fremennik.FremennikDiaryEntry;
import godzhell.model.content.achievement_diary.karamja.KaramjaDiaryEntry;
import godzhell.model.content.achievement_diary.lumbridge_draynor.LumbridgeDraynorDiaryEntry;
import godzhell.model.content.achievement_diary.varrock.VarrockDiaryEntry;
import godzhell.model.content.achievement_diary.wilderness.WildernessDiaryEntry;
import godzhell.model.content.dailytasks.DailyTasks;
import godzhell.model.content.dailytasks.DailyTasks.PossibleTasks;
import godzhell.model.npcs.NPC;
import godzhell.model.players.Boundary;
import godzhell.model.players.Player;
import godzhell.model.players.PlayerHandler;
import godzhell.model.players.mode.ModeType;
import godzhell.model.content.skills.Skill;
import godzhell.model.content.skills.smithing.Smelting;
import godzhell.util.Location3D;
import godzhell.util.Misc;
import godzhell.world.objects.GlobalObject;

/**
 * Represents a singular event that is executed when a player attempts to mine.
 * 
 * @author Jason MacKeigan
 * @date Feb 18, 2015, 6:17:11 PM
 */
public class MiningEvent extends Event<Player> {
	
	public static int[] prospectorOutfit = { 12013, 12014, 12015, 12016 };

	/**
	 * The amount of cycles that must pass before the animation is updated
	 */
	private final int ANIMATION_CYCLE_DELAY = 3;

	/**
	 * The value in cycles of the last animation
	 */
	private int lastAnimation;

	/**
	 * The pickaxe being used to mine
	 */
	private final Pickaxe pickaxe;

	/**
	 * The mineral being mined
	 */
	private final Mineral mineral;

	/**
	 * The object that we are mning
	 */
	private int objectId;

	/**
	 * The location of the object we're mining
	 */
	private Location3D location;

	/**
	 * The npc the player is mining, if any
	 */
	private NPC npc;

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * 
	 * @param attachment the player this is created for
	 * @param objectId the id value of the object being mined from
	 * @param location the location of the object being mined from
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, int objectId, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.objectId = objectId;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * 
	 * @param attachment the player this is created for
	 * @param npc the npc being from from
	 * @param location the location of the npc
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, NPC npc, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.npc = npc;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	@Override
	public void update() {
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(pickaxe.getItemId()) && !attachment.getItems().isWearingItem(pickaxe.getItemId())) {
			attachment.sendMessage("That is strange! The pickaxe could not be found.");
			stop();
			return;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.getDH().sendStatement("You have no more free slots.");
			stop();
			return;
		}
		
		if (RandomUtils.nextInt(1, 100) == 1 && attachment.getInterfaceEvent().isExecutable()) {
			attachment.getInterfaceEvent().execute();
			stop();
			return;
		}
		if (objectId > 0) {
			if (Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ()) && mineral.isDepletable()) {
				//attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		} else {
			if (npc == null || npc.isDead) {
				attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		}
		if (super.getElapsedTicks() - lastAnimation > ANIMATION_CYCLE_DELAY) {
			attachment.startAnimation(pickaxe.getAnimation());
			lastAnimation = super.getElapsedTicks();
		}
	}

	@Override
	public void execute() {
		double osrsExperience = 0;
		double regExperience = 0;
		int pieces = 0;
		for (int i = 0; i < prospectorOutfit.length; i++) {
			if (attachment.getItems().isWearingItem(prospectorOutfit[i])) {
				pieces += 6;
			}
		}
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			stop();
			return;
		}
		if (mineral.isDepletable()) {
				if (objectId > 0) {
					Server.getGlobalObjects().add(new GlobalObject(Mineral.EMPTY_VEIN, location.getX(), location.getY(),
							location.getZ(), 1, 10, mineral.getRespawnRate(), objectId));
				} else {
					npc.isDead = true;
					npc.actionTimer = 0;
					npc.needRespawn = false;
				}
		}
		if (objectId == 41223) {
			if(ShootingStar.CRASHED_STAR == null || ShootingStar.CRASHED_STAR.getStarObject().getPickAmount() >= ShootingStar.MAXIMUM_MINING_AMOUNT) {
				attachment.getSkilling().stop();
				return;
			} else {
				ShootingStar.CRASHED_STAR.getStarObject().incrementPickAmount();
			}
		}
		attachment.turnPlayerTo(location.getX(), location.getY());
		Achievements.increase(attachment, AchievementType.MINE, 1);
		
		if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
			if (Misc.random(20) == 5) {
				int randomAmount = Misc.random(3) + 1;
				attachment.sendMessage("You received " + randomAmount + " coins while mining!");
				attachment.getItems().addItem(995, randomAmount);
			}
		}
		
		/**
		 * Experience calculation
		 */
		osrsExperience = mineral.getExperience() + mineral.getExperience() / 10 * pieces;
		regExperience = mineral.getExperience() * Config.MINING_EXPERIENCE + mineral.getExperience() * Config.MINING_EXPERIENCE / 10 * pieces;
		if(!Boundary.isIn(attachment, Boundary.MOTHERLODE_MINE)) {
			attachment.getPA().addSkillXP((int) (attachment.getMode().getType().equals(ModeType.OSRS) ? osrsExperience * 2 : regExperience), Skill.MINING.getId(), true);
		} else {
			attachment.getPA().addSkillXP((int) (mineral.getExperience()), Skill.MINING.getId(), true);
		}
		switch (mineral) {
		case ADAMANT:
			break;
		case COAL:
			if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
				attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.MINE_COAL_FREM);
			}
			break;
		case COPPER:
			break;
		case ESSENCE:
			attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_ESSENCE);
			break;
		case GEM:
			if (Boundary.isIn(attachment, Boundary.CRAFTING_GUILD_BOUNDARY)) {
				attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.MINE_GEM_FAL);
			}
			DailyTasks.increase(attachment, PossibleTasks.GEM_ROCK);
			break;
		case GOLD:
			if (Boundary.isIn(attachment, Boundary.TZHAAR_CITY_BOUNDARY)) {
				attachment.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.MINE_GOLD_KAR);
			}
			break;
		case IRON:
			if (attachment.inWild()) {
				attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_IRON_WILD);
			}
			if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
				attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_IRON);
			}
			if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
				attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.MINE_IRON_LUM);
			}
			break;
		case MITHRIL:
			if (attachment.inWild()) {
				attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_MITHRIL_WILD);
			}
			if (Boundary.isIn(attachment, Boundary.CRAFTING_GUILD_BOUNDARY)) {
				attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.MINE_MITHRIL);
			}
			break;
		case RUNE:
			DailyTasks.increase(attachment, PossibleTasks.RUNITE_ORE);
			break;
		case TIN:
			break;
		case CLAY:
			attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.MINE_CLAY);
			break;
		default:
			break;
		
		}
		int amount = SkillcapePerks.MINING.isWearing(attachment) || (SkillcapePerks.isWearingMaxCape(attachment) && attachment.getMiningEffect()) && Misc.random(2) == 1 ? 2 :
					attachment.getRechargeItems().hasItem(13104) && Misc.random(9) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13105) && Misc.random(8) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13106) && Misc.random(6) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13107) && Misc.random(4) == 2 ? 2 : 1;
		if (!(mineral.getBarName().contains("star"))) {
			attachment.getItems().addItem(mineral.getMineralReturn().generate(), amount);
			attachment.sendMessage("You manage to mine some "+mineral.name().toLowerCase()+" ore.");
		} else {
			if(ShootingStar.MAXIMUM_MINING_AMOUNT == 0){
				attachment.sendMessage("The Star run out of star dust.");
				ShootingStar.despawn(false);
				ShootingStar.MAXIMUM_MINING_AMOUNT = 250;
				stop();
				return;
			}
			attachment.getItems().addItem(25527, 1);
			ShootingStar.MAXIMUM_MINING_AMOUNT -= 1;
			attachment.sendMessage(""+ShootingStar.MAXIMUM_MINING_AMOUNT);
			if (Misc.random(200) == 5) {
				attachment.sendMessage("You received Star fragment while mining the star!");
				attachment.getItems().addItem(25547, 1);
			}
		}
		if (mineral.getBarName() != "none") {
			if (Misc.random(3) == 2) {
				if (attachment.getItems().isWearingItem(13243) || attachment.getItems().playerHasItem(13243)) {
					Smelting.startSmelting(attachment, mineral.getBarName(), "ONE", "INFERNAL");
					return;
				}
			}
		}
		
		if (Misc.random(mineral.getPetChance() / 2) == 10) {
			switch (Misc.random(1)) {
			case 0:
				attachment.getItems().addItem(20358, 1);
				break;
				
			case 1:
				attachment.getItems().addItem(20360, 1);
				break;
			}
			attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
		}
		if (Misc.random(mineral.getPetChance()) == 10) {
			attachment.getItems().addItem(20362, 1);
			attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
		}

		if (Misc.random(mineral.getPetChance()) == 2 && attachment.getItems().getItemCount(13321, false) == 0
				&& attachment.summonId != 7439) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.playerName
					+ "</col> mined a rock and formed the <col=CC0000>Rock golem</col> pet!");
			attachment.getItems().addItemUnderAnyCircumstance(13321, 1);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null) {
			return;
		}
		attachment.stopAnimation();
	}
}
