package godzhell.model.content.skills.runecrafting;


import godzhell.model.players.Player;

public class RuneCraftingActions {

    public static void handleRuneCrafting(Player player, int objectId) {
        switch (objectId) {
            case 2465:// air altar portal
                if (player.objectX == 2841 && player.objectY == 4828) {
                    player.getPlayerAssistant().startTeleport(2983, 3293, 0, "modern", false);
                }
                break;

            case 2466:// mind altar portal
                if (player.objectX == 2793 && player.objectY == 4827) {
                    player.getPlayerAssistant().startTeleport(2980, 3514, 0, "modern", false);
                }
                break;

            case 2467:// water altar portal
                if (player.objectX == 2727 && player.objectY == 4832) {
                    player.getPlayerAssistant().startTeleport(3184, 3162, 0, "modern", false);
                }
                break;

            case 2468:// earth rune portal
                if (player.objectX == 2655 && player.objectY == 4829) {
                    player.getPlayerAssistant().startTeleport(3308, 3476, 0, "modern", false);
                }
                break;

            case 2469:// fire rune portal
                if (player.objectX == 2574 && player.objectY == 4850) {
                    player.getPlayerAssistant().startTeleport(3311, 3256, 0, "modern", false);
                }
                break;

            case 2470:// body altar portal
                if (player.objectX == 2523 && player.objectY == 4825) {
                    player.getPlayerAssistant().startTeleport(3051, 3444, 0, "modern", false);
                }
                break;

            case 2471:// cosmic altar portal
                if (player.objectX == 2163 && player.objectY == 4833 || player.objectX == 2142 && player.objectY == 4854 || player.objectX == 2121 && player.objectY == 4833 || player.objectX == 2412 && player.objectY == 4812) {
                    player.getPlayerAssistant().startTeleport(2410, 4379, 0, "modern", false);
                }
                break;

            case 2472:// law altar portal
                if (player.objectX == 2464 && player.objectY == 4817) {
                    player.getPlayerAssistant().startTeleport(2857, 3379, 0, "modern", false);
                }
                break;

            case 2473:// nature portal altar
                if (player.objectX == 2400 && player.objectY == 4834) {
                    player.getPlayerAssistant().startTeleport(2866, 3022, 0, "modern", false);
                }
                break;

            case 2474:
                if (player.objectX == 3233 && player.objectY == 9312) {// desert
                    // treasure
                    // portal
                    player.getPlayerAssistant().startTeleport(3233, 2887, 0, "modern", false);
                } else if (player.objectX == 2282 && player.objectY == 4837) {// chaos
                    // altar
                    // portal
                    player.getPlayerAssistant().startTeleport(3062, 3593, 0, "modern", false);
                }
                break;

            case 34758:// death altar portal
                if (player.objectX == 2208 && player.objectY == 4829) {
                    player.getPlayerAssistant().startTeleport(1863, 4639, 0,
                            "modern", false);
                }
                break;

            case 34760:// air altar crafting
                if (player.objectX == 2843 && player.objectY == 4833) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34761:// mind altar crafting
                if (player.objectX == 2785 && player.objectY == 4840) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34762:// water altar crafting
                if (player.objectX == 2715 && player.objectY == 4835) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34763:// earth altar crafting
                if (player.objectX == 2657 && player.objectY == 4840) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34764:// fire altar crafting
                if (player.objectX == 2584 && player.objectY == 4837) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34765:// body altar crafting
                if (player.objectX == 2522 && player.objectY == 4839) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34766:// cosmic altar crafting
                if (player.objectX == 2141 && player.objectY == 4832) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34767:// law altar crafting
                if (player.objectX == 2463 && player.objectY == 4831) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34768:// nature altar crafting
                if (player.objectX == 2399 && player.objectY == 4840) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34769:// chaos altar crafting
                if (player.objectX == 2270 && player.objectY == 4841) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 34770:// death altar crafting
                if (player.objectX == 2204 && player.objectY == 4835) {
                    player.getRunecrafting().craftRunes(objectId);
                }
                break;

            case 2452:// air altar entrance
                if (player.objectX == 2984 && player.objectY == 3291) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2453:// mind altar entrance
                if (player.objectX == 2981 && player.objectY == 3513) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2454:// water altar entrance
                if (player.objectX == 3184 && player.objectY == 3164) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2455:// earth altar entrance
                if (player.objectX == 3305 && player.objectY == 3473) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2456:// fire altar entrance
                if (player.objectX == 3312 && player.objectY == 3254) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2457:// body altar entrance
                if (player.objectX == 3052 && player.objectY == 3444) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2458:// cosmic altar entrance
                if (player.objectX == 2407 && player.objectY == 4376) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2459:// law altar entrance
                if (player.objectX == 2857 && player.objectY == 3380) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2460:// nature altar entrance
                if (player.objectX == 2868 && player.objectY == 3018) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2461:// chaos altar entrance
                if (player.objectX == 3059 && player.objectY == 3590) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;

            case 2462:// death altar entrance
                if (player.objectX == 1859 && player.objectY == 4638) {
                    player.getRunecrafting().enterAltar(objectId, 0);
                }
                break;
        }
    }

}
