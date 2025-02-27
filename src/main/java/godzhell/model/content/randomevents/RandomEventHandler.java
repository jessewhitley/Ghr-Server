package godzhell.model.content.randomevents;

import godzhell.model.players.Player;
import godzhell.model.players.Right;
import godzhell.util.Misc;

/**
 * Eventhandler class
 * @author Andrew (Mr Extremez)
 */

public class RandomEventHandler {

    public static int CALL_RANDOM = 350 + Misc.random(100);

    public static void resetEvent(Player c) {
        c.randomActions = 0;
    }

    private static int[][] FAIL_COORDS = { { 3333, 3333 }, { 3196, 3193 },
            { 3084, 3549 }, { 2974, 3346 }, { 2781, 3506 }, { 2810, 3508 }, };

    public static void failEvent(Player client) {
        int loc = Misc.random(FAIL_COORDS.length - 1);
        client.teleportToX = FAIL_COORDS[loc][0];
        client.teleportToY = FAIL_COORDS[loc][1];
        client.heightLevel = 0;
        client.sendMessage("You wake up in a strange location...");
        resetEvent(client);
        client.getPA().closeAllWindows();
    }

    public static void callRandom(Player c) { // add all random events here
        if (c.inFightCaves() || c.playerEquipment[c.playerWeapon] == 4024) {
            return;
        }
        int randomEvent = Misc.random(3);
        switch (randomEvent) {
            case 0:
                SandwhichLady.openSandwhichLady(c);
                resetEvent(c);
                break;
            case 1:
                if (c.chickenSpawned == false) {
                  //  EvilChicken.spawnChicken(c);
                    resetEvent(c);
                }
                break;
            case 2:
              //  FreakyForester.teleportToLocation(c);
                resetEvent(c);
                break;
            case 3:
               // Swarm.spawnSwarm(c);
                resetEvent(c);
                break;
            default:
                System.out.println("Error no random event called for " + c.playerName + ".");
                break;
        }
    }

    public static void addRandom(Player c) {
        if (c.randomEventsEnabled) {
            if (c.randomActions >= CALL_RANDOM) {
                callRandom(c);
                if (c.playerIsBusy() && !c.hasSandwhichLady) {
                    c.getPA().closeAllWindows();
                }
            } else {
                int nextRandom = CALL_RANDOM - c.randomActions;
                if (c.getRights().contains(Right.OWNER)) {
                    c.sendMessage("Next random will be in " + nextRandom + " more random actions.");
                }
                c.randomActions += 1;
            }
        }
    }
}

