package godzhell.model.players.packets.itemoptions;

import godzhell.Server;
import godzhell.definitions.ItemID;
import godzhell.model.content.DiceHandler;
import godzhell.model.content.LootingBag.LootingBag;
import godzhell.model.content.Packs;
import godzhell.model.content.RunePouch;
import godzhell.model.content.achievement.AchievementType;
import godzhell.model.content.achievement.Achievements;
import godzhell.model.content.barrows.Barrows;
import godzhell.model.content.skills.hunter.Hunter;
import godzhell.model.content.skills.hunter.trap.impl.BirdSnare;
import godzhell.model.content.skills.hunter.trap.impl.BoxTrap;
import godzhell.model.content.skills.prayer.Bone;
import godzhell.model.content.skills.prayer.Prayer;
import godzhell.model.content.skills.runecrafting.Pouches;
import godzhell.model.content.skills.runecrafting.Pouches.Pouch;
import godzhell.model.content.teleportation.TeleportTablets;
import godzhell.model.content.trails.MasterClue;
import godzhell.model.content.trails.RewardLevel;
import godzhell.model.items.ItemAssistant;
import godzhell.model.multiplayer_session.MultiplayerSessionType;
import godzhell.model.multiplayer_session.duel.DuelSession;
import godzhell.model.multiplayer_session.duel.DuelSessionRules.Rule;
import godzhell.model.players.*;
import godzhell.model.players.combat.Hitmark;
import godzhell.model.players.combat.magic.NonCombatSpells;
import godzhell.util.Misc;
import godzhell.world.objects.GlobalObject;

import java.util.Objects;
import java.util.Optional;

import static godzhell.model.content.DiceHandler.DICING_AREA;

/**
 * Clicking an item, bury bone, eat food etc
 **/
public class ItemOptionOne implements PacketType {
    /**
     * The last planted flower
     */
    private int lastPlantedFlower;

    /**
     /**
     * If the entity is in the midst of planting mithril seeds
     */
    public static boolean plantingMithrilSeeds = false;

    /**
     * The position the mithril seed was planted
     */
    private static final int[] position = new int[2];

    /**
     * Chances of getting to array flower1
     */
    private int flowerChance = Misc.random(100);


    /**
     * Last plant for the timing of the flower
     */
    private long lastPlant = 0;
    /**
     * Array containing flower object ids without black and white flowers
     */
    private int flower[] = {
            2980, 2981, 2982,
            2983, 2984, 2985,
            2986
    };

    /**
     * Array containing flower object ids along with black and white flowers
     */
    private int flower1[] = {
            2980, 2981, 2982,
            2983, 2984, 2985,
            2986, 2987, 2988
    };

    /**
     * Draws a random flower object from the flower arrays
     * @return
     * 			the flower object chosen
     */
    private int randomFlower() {
        if (flowerChance > 29) {
            return flower[(int) (Math.random() * flower.length)];
        }
        return flower[(int) (Math.random() * flower1.length)];
    }

    public void plantMithrilSeed(Player c) {
        if (System.currentTimeMillis() - c.diceDelay >= 2000) { // time between plants.
            position[0] = c.absX;
            position[1] = c.absY;
            lastPlantedFlower = randomFlower();
            if (!Server.getGlobalObjects().anyExists(position[0], position[1], c.getHeight())) { // Checks if there are NO objects under the player
                GlobalObject object1 = new GlobalObject(lastPlantedFlower, position[0], position[1], c.getHeight(), 3, 10, 120, -1);
                Server.getGlobalObjects().add(object1);
                c.getPA().walkTo(1, 0);
                c.turnPlayerTo(position[0] - 1, position[1]);
                c.sendMessage("You planted a flower!");
                plantingMithrilSeeds = true;
                c.getItems().deleteItem(299, c.getItems().getItemSlot(299), 1);
                c.diceDelay = System.currentTimeMillis();
            } else if (Server.getGlobalObjects().anyExists(position[0], position[1], c.getHeight())) { // Checks if there are objects under the player
                c.sendMessage("You can plant on top of this. Please Move");
            } else {
                c.sendMessage("You must wait 2 seconds to plant another flower."); // Message for when you attempt to plant before the timer is finished
            }
        }
    }
    public static void plantMithrilSeedRigged(Player c,int flowerId) {
        position[0] = c.absX;
        position[1] = c.absY;
        GlobalObject object1 = new GlobalObject(flowerId, position[0], position[1], c.getHeight(), 3, 10, 120, -1);
        Server.getGlobalObjects().add(object1);
        c.getPA().walkTo(1, 0);
        c.turnPlayerTo(position[0] -1, position[1]);
        c.sendMessage("You planted a flower!");
        plantingMithrilSeeds = true;
    }
    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        c.getInStream().readSignedShortLittleEndianA();
        int itemSlot = c.getInStream().readSignedWordA();
        int itemId = c.getInStream().readSignedWordBigEndian();
        if(c.debugMessage) {
        	c.sendMessage("ItemOption1: Item: " + itemId + " Name: " + ItemAssistant.getItemName(itemId));
        }
        if (itemSlot >= c.playerItems.length || itemSlot < 0) {
            return;
        }
        if (itemId != c.playerItems[itemSlot] - 1) {
            return;
        }
        if (c.isDead || c.getHealth().getAmount() <= 0) {
            return;
        }
        if (Server.getHolidayController().clickItem(c, itemId)) {
            return;
        }
        if (c.getInterfaceEvent().isActive()) {
            c.sendMessage("Please finish what you're doing.");
            return;
        }
        c.lastClickedItem = itemId;
        c.getHerblore().clean(itemId);
        if (c.getFood().isFood(itemId)) {
            c.getFood().eat(itemId, itemSlot);
        } else if (c.getPotions().isPotion(itemId)) {
            c.getPotions().handlePotion(itemId, itemSlot);
        }
        Optional<Bone> bone = Prayer.isOperableBone(itemId);
        if (bone.isPresent()) {
            c.getPrayer().bury(bone.get());
            return;
        }
        TeleportTablets.operate(c, itemId);
        Packs.openPack(c, itemId);
        if (LootingBag.isLootingBag(c, itemId)) {
            c.getLootingBag().openWithdrawalMode();
            return;
        }
        if (RunePouch.isRunePouch(c, itemId)) {
            c.getRunePouch().openRunePouch();
            return;
        }
        switch (itemId) {
        case 6:
			c.getCannon().setup();
			break;   
            case 2697:
                if (c.getItems().playerHasItem(2697, 1)) {
                    c.getItems().deleteItem(2697, 1);
                    c.gfx100(263);
                    c.amDonated += 10;
                    c.sendMessage("Thank you for donating! 10$ has been added to your total credit.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 299:
                if(!Boundary.isIn(c, DICING_AREA)) {
                    c.sendMessage("You can not be in edge when planting mithril seeds.");
                    return;
                }
                if(System.currentTimeMillis() - c.lastPlant < 250) {
                    return;
                }
                c.lastPlant = System.currentTimeMillis();
                plantMithrilSeed(c);
                break;
            case 2698:
                if (c.getItems().playerHasItem(2698, 1)) {
                    c.getItems().deleteItem(2698, 1);
                    c.gfx100(263);
                    c.amDonated += 50;
                    c.sendMessage("Thank you for donating! 50$ has been added to your total credit.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
            case 29293:
                if (c.getItems().playerHasItem(29293, 1)) {
                    c.getItems().deleteItem(29293, 1);
                    c.gfx100(263);
                    c.amDonated += 300;
                    c.sendMessage("Thank you for donating! 300$ has been added to your total credit.");
                    c.getRights().add(Right.RAINBOW_DONATOR);
                    c.getRights().updatePrimary();
                    c.getPA().closeAllWindows();
                    return;
                }
            case 2699:
                if (c.getItems().playerHasItem(2699, 1)) {
                    c.getItems().deleteItem(2699, 1);
                    c.gfx100(263);
                    c.amDonated += 150;
                    c.sendMessage("Thank you for donating! 150$ has been added to your total credit.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 12929:
				if(c.getItems().playerHasItem(12929)) {
					c.getItems().deleteItem(12929, 1);
					c.getItems().addItemUnderAnyCircumstance(12934, 20000);
					c.sendMessage("You successfully dismantle the Serpentine Helmet for 20,000 zulrah scales.");
				}
			break;
            case 2700:
                if (c.getItems().playerHasItem(2700, 1)) {
                    c.getItems().deleteItem(2700, 1);
                    c.gfx100(263);
                    c.amDonated += 300;
                    c.sendMessage("Thank you for donating! 300$ has been added to your total credit.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                }
                return;
            case 10834: //Ancient loot bag
                    if (c.getItems().playerHasItem(10834)) {
                        c.getGalvekBag().open();
                        return;
                    }
            case 13346:
                if (c.getItems().playerHasItem(13346)) {
                    c.getUltraMysteryBox().openInterface();
                    return;
                }
                break;
            case 21347:
                c.boltTips = true;
                c.arrowTips = false;
                c.javelinHeads = false;
                c.sendMessage("Your Amethyst method is now Bolt Tips!");
                break;
            case 20724:
                DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
                if (Objects.nonNull(session)) {
                    if (session.getRules().contains(Rule.NO_DRINKS)) {
                        c.sendMessage("Using the imbued heart with 'No Drinks' option is forbidden.");
                        return;
                    }
                }
                if (System.currentTimeMillis() - c.lastHeart < 420000) {
                    c.sendMessage("You must wait 7 minutes between each use.");
                } else {
                    c.getPA().imbuedHeart();
                    c.lastHeart = System.currentTimeMillis();
                }
                break;
            case 10006:
                Hunter.lay(c, new BirdSnare(c));
                break;
            case 10008:
            	c.startAnimation(5208);
                Hunter.lay(c, new BoxTrap(c));
                break;
            case 13249:
                if (!c.getSlayer().isCerberusRoute()) {
                    c.sendMessage("You have no clue how to navigate in here, you should find a slayer master to learn.");
                    return;
                }
                c.getPA().startTeleport(1310, 1237, 0, "keymaster", false);
                //AgilityHandler.delayFade(c, "", 1310, 1237, 0, "You teleport into the cave", "and end up at the main room.", 3);
                c.getItems().deleteItem(13249, 1);
                break;

            case 13226:
                c.getHerbSack().fillSack();
                break;

            case 12020:
                c.getGemBag().fillBag();
                break;

            case 5509:
                Pouches.fill(c, Pouch.forId(itemId), itemId, 0);
                break;
            case 5510:
                Pouches.fill(c, Pouch.forId(itemId), itemId, 1);
                break;
            case 5512:
                Pouches.fill(c, Pouch.forId(itemId), itemId, 2);
                break;

            case 952: //Spade
                int x = c.absX;
                int y = c.absY;
                if (Boundary.isIn(c, Barrows.GRAVEYARD)) {
                    c.getBarrows().digDown();
                }
                if (x == 3005 && y == 3376 || x == 2999 && y == 3375 || x == 2996 && y == 3377) {
          
                		c.getPA().movePlayer(1760, 5163, 0);
                        return;
                 
             }
                break;
            case 28826:
                if (c.getItems().playerHasItem(28826)) {
                    c.getUltraMysteryBox().openInterface();
                    return;
                }
                break;
            case 29326:
                if(c.getItems().playerHasItem(29326)){
                    c.getDrCapeMysteryBox().openInterface();
                    return;
                }
                break;
            case 28822:
                if (c.getItems().playerHasItem(28822)) {
                    c.getUncommonMysteryBox().openInterface();
                    return;
                }
                break;
            case 28823:
                if (c.getItems().playerHasItem(28823)) {
                    c.getCommonMysteryBox().openInterface();
                    return;
                }
                break;
            case 28824:
                if (c.getItems().playerHasItem(28824)) {
                    c.getRareMysteryBox().openInterface();
                    return;
                }
                break;
            case 28825:
                if (c.getItems().playerHasItem(28825)) {
                    c.getSuperRareMysteryBox().openInterface();
                    return;
                }
                break;
        }

        if (itemId == 2678) {
            c.getDH().sendDialogues(657, -1);
            return;
        }
        if (itemId == 8015 || itemId == 8014) {
            NonCombatSpells.attemptDate(c, itemId);
        }
        
        if (itemId == 9553) {
            c.getPotions().eatChoc(9553, -1, itemSlot, 1, true);
        }
        if (itemId == 12846) {
            c.getDH().sendDialogues(578, -1);
        }
        if (itemId == 12938) {
            if (c.getZulrahEvent().getInstancedZulrah() != null) {
                c.sendMessage("It seems your currently in the zulrah instance, relog if this is false.");
                return;
            }
            c.getDH().sendDialogues(625, -1);
            return;
        }
        /*if (itemId == 405) {
			if (c.getItems().freeSlots() < 2) {
				c.sendMessage("You need at least 2 free slots to open this.");
				return;
			}
			c.getItems().deleteItem2(itemId, 1);
			c.getItems().addItem(995, 10000 + Misc.random(90000));
			c.getItems().addItem(1624, 1 + Misc.random(9));
			c.sendMessage("You open the casket and find some treasure inside.");
		}*/
        if (itemId == 4155) {
            if (!c.getSlayer().getTask().isPresent()) {
                c.sendMessage("You do not have a task, please talk with a slayer master!");
                return;
            }
            c.sendMessage("I currently have @blu@" + c.getSlayer().getTaskAmount() + " " + c.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
            c.getPA().closeAllWindows();
        }
        if (itemId == 2839) {
            if (c.getSlayer().isHelmetCreatable() == true) {
                c.sendMessage("You have already learned this recipe. You have no more use for this scroll.");
                return;
            }
            if (c.getItems().playerHasItem(2839)) {
                c.getSlayer().setHelmetCreatable(true);
                c.sendMessage("You have learned the slayer helmet recipe. You can now assemble it");
                c.sendMessage("using a Black Mask, Facemask, Nose peg, Spiny helmet and Earmuffs.");
                c.getItems().deleteItem(2839, 1);
            }
        }
        if (itemId == DiceHandler.DICE_BAG) {
            DiceHandler.selectDice(c, itemId);
        }
        if (itemId > DiceHandler.DICE_BAG && itemId <= 15100) {
            DiceHandler.rollDice(c);
        }
        if (itemId == 2697) {
            c.sendMessage("You can't redeem this scroll on yourself!");
            c.sendMessage("Use this on another player to transfer $10 to their amount donated");
            c.sendMessage("This does NOT give the other player donator points.");
			/*if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				c.getDH().sendDialogues(4000, -1);
			}*/
        }
        if (itemId == 2698) {
            c.sendMessage("You can't redeem this scroll on yourself!");
            c.sendMessage("Use this on another player to transfer $50 to their amount donated");
            c.sendMessage("This does NOT give the other player donator points.");
			/*if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				c.getDH().sendDialogues(4000, -1);
			}*/
        }
        if (itemId == 2699) {
            c.sendMessage("You can't redeem this scroll on yourself!");
            c.sendMessage("Use this on another player to transfer $150 to their amount donated");
            c.sendMessage("This does NOT give the other player donator points.");
			/*if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				c.getDH().sendDialogues(4000, -1);
			}*/
        }
        if (itemId == 2700) {
            c.sendMessage("You can't redeem this scroll on yourself!");
            c.sendMessage("Use this on another player to transfer $300 to their amount donated");
            c.sendMessage("This does NOT give the other player donator points.");
			/*if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				c.getDH().sendDialogues(4000, -1);
			}*/
        }
        if (itemId == 2701) {
            if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
                return;
            }
            if (c.getItems().playerHasItem(2701, 1)) {
                c.getDH().sendDialogues(4004, -1);
            }
        }
        if (itemId == 7509) {
            if (c.inWild() || c.inDuelArena() || Boundary.isIn(c, Boundary.DUEL_ARENA)) {
                c.sendMessage("You cannot do this here.");
                return;
            }
            if (c.getHealth().getStatus().isPoisoned() || c.getHealth().getStatus().isVenomed()) {
                c.sendMessage("You are effected by venom or poison, you should cure this first.");
                return;
            }
            if (c.getHealth().getAmount() <= 1) {
                c.sendMessage("I better not do that.");
                return;
            }
            c.forcedChat("Ow! I nearly broke a tooth!");
            c.startAnimation(829);
            // c.getHealth().reduce(1);
            c.appendDamage(1, Hitmark.HIT);
            return;
        }
        if (itemId == 10269) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10269, 1)) {
                c.getItems().addItem(995, 30000);
                c.getItems().deleteItem(10269, 1);
            }
        }
        if(itemId == 1464) {
            c.getPA().exchangeItems(PlayerAssistant.PointExchange.VOTE_POINTS, 1464, 1);
        }
        if (itemId == 10271) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10271, 1)) {
                c.getItems().addItem(995, 10000);
                c.getItems().deleteItem(10271, 1);
            }
        }
        if (itemId == 10273) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10273, 1)) {
                c.getItems().addItem(995, 14000);
                c.getItems().deleteItem(10273, 1);
            }
        }
        if (itemId == 10275) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10275, 1)) {
                c.getItems().addItem(995, 18000);
                c.getItems().deleteItem(10275, 1);
            }
        }
        if (itemId == 10277) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10277, 1)) {
                c.getItems().addItem(995, 22000);
                c.getItems().deleteItem(10277, 1);
            }
        }
        if (itemId == 10279) {
            if (c.inWild() || c.inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10279, 1)) {
                c.getItems().addItem(995, 26000);
                c.getItems().deleteItem(10279, 1);
            }
        }
		/* Mystery box */
        if (itemId == 6199)
            if (c.getItems().playerHasItem(6199)) {
                c.getMysteryBox().openInterface();
                return;
            }
        if (itemId == 11739)
            if (c.getItems().playerHasItem(11739)) {
                c.getHourlyRewardBox().open();
                return;
            }

        if (itemId == 405) //Pvm Casket
            if (c.getItems().playerHasItem(405)) {
                c.getPvmCasket().open();
                return;
            }
        if (itemId == 21307) //Pursuit Crate
            if (c.getItems().playerHasItem(21307)) {
                c.getWildyCrate().open();
                return;
            }
        if (itemId == 20703) //Daily Gear Box
            if (c.getItems().playerHasItem(20703)) {
                c.getDailyGearBox().open();
                return;
            }
        if (itemId == 20791) //Daily Skilling Box
            if (c.getItems().playerHasItem(20791)) {
                c.getDailySkillBox().open();
                return;
            }
		/*if (itemId == 7310) //Skill Casket
			if (c.getItems().playerHasItem(7310)) {
				c.getSkillCasket().open();
				return;
			}*/
        if (itemId == 6542)
            if (c.getItems().playerHasItem(6542)) {
                c.getChristmasPresent().open();
                return;
            }
        if (itemId == ItemID.REWARD_CASKET_EASY) { // Easy Clue Scroll Casket
            c.getItems().deleteItem(itemId, 1);
            c.getTrails().addRewards(RewardLevel.EASY);
            c.setEasyClueCounter(c.getEasyClueCounter() + 1);
            c.sendMessage("@blu@You have completed " + c.getEasyClueCounter() + " easy Treasure Trails.");
        }
        if (itemId == ItemID.REWARD_CASKET_MEDIUM) { // Medium Clue Scroll Casket
            c.getItems().deleteItem(itemId, 1);
            c.getTrails().addRewards(RewardLevel.MEDIUM);
            c.setMediumClueCounter(c.getMediumClueCounter() + 1);
            c.sendMessage("@blu@You have completed " + c.getMediumClueCounter() + " medium Treasure Trails.");
        }
        if (itemId == ItemID.REWARD_CASKET_HARD) { // Hard Clue Scroll Casket
            c.getItems().deleteItem(itemId, 1);
            c.getTrails().addRewards(RewardLevel.HARD);
            c.setHardClueCounter(c.getHardClueCounter() + 1);
            c.sendMessage("@blu@You have completed " + c.getHardClueCounter() + " hard Treasure Trails.");
        }
        if (itemId == ItemID.REWARD_CASKET_MASTER) { // Master Clue Scroll Casket
            if (c.getItems().playerHasItem(ItemID.REWARD_CASKET_MASTER)) {
                c.getItems().deleteItem(itemId, 1);
                c.getTrails().addRewards(RewardLevel.MASTER);
                c.setMasterClueCounter(c.getMasterClueCounter() + 1);
                c.sendMessage("@blu@You have completed " + c.getMasterClueCounter() + " master Treasure Trails.");
                if (Misc.random(200) == 2 && c.getItems().getItemCount(19730, true) == 0 && c.summonId != 19730) {
                    PlayerHandler.executeGlobalMessage("[<col=CC0000>Clue</col>] @cr20@ <col=255>" + c.playerName
                            + "</col> hit the jackpot and got a <col=CC0000>Bloodhound</col> pet!");
                    c.getItems().addItemUnderAnyCircumstance(19730, 1);
                }
            }
        }
        if (itemId == 2677) {
            Achievements.increase(c, AchievementType.CLUES, 1);
            c.getItems().deleteItem(itemId, 1);
            c.getItems().addItem(ItemID.REWARD_CASKET_EASY, 1);
            c.sendMessage("You've received a easy clue scroll casket.");
        }
        if (itemId == 2801) {
            Achievements.increase(c, AchievementType.CLUES, 1);
            c.getItems().deleteItem(itemId, 1);
            c.getItems().addItem(ItemID.REWARD_CASKET_MEDIUM, 1);
            c.sendMessage("You've receivedd a medium clue scroll casket.");
        }
        if (itemId == 2722) {
            Achievements.increase(c, AchievementType.CLUES, 1);
            c.getItems().deleteItem(itemId, 1);
            c.getItems().addItem(ItemID.REWARD_CASKET_HARD, 1);
            c.sendMessage("You've received a hard clue scroll casket.");
        }
        /**
         * Master clue scroll
         */
        if (itemId == 19835) {
            MasterClue.progressScroll(c);
        }
        if (itemId == 2528) {
            c.usingLamp = true;
            c.normalLamp = false;
            c.antiqueLamp = true;
            c.sendMessage("You rub the lamp...");
            c.getPA().showInterface(2808);
        }
		/*
		 * if (itemId == 4447) { c.usingLamp = true; c.antiqueLamp = true; c.normalLamp = false; c.sendMessage("You rub the antique lamp of 13 million experience..." );
		 * c.getPA().showInterface(2808); } if (itemId == 2528) { c.usingLamp = true; c.normalLamp = true; c.antiqueLamp = false; c.sendMessage(
		 * "You rub the lamp of 1 million experience..."); c.getPA().showInterface(2808); }
		 */

    }

}