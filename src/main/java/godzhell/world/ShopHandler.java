package godzhell.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import godzhell.Config;
import godzhell.model.players.PlayerHandler;
import godzhell.util.Misc;

/**
 * Shops
 **/

public class ShopHandler {

	public static int MaxShops = 370;
	public static int MaxShopItems = 370;
	public static int MaxInShopItems = 40;
	public static int MaxShowDelay = 10;
	public static int MaxSpecShowDelay = 60;
	public static int TotalShops = 0;
	public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
	public static int[] ShopItemsStandard = new int[MaxShops];
	public static String[] ShopName = new String[MaxShops];
	public static int[] ShopSModifier = new int[MaxShops];
	public static int[] ShopBModifier = new int[MaxShops];
	public static long[][] ShopItemsRestock = new long[MaxShops][MaxShopItems];
	
	public ShopHandler() {
		for (int i = 0; i < MaxShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				ResetItem(i, j);
				ShopItemsSN[i][j] = 0;
			}
			ShopItemsStandard[i] = 0;
			ShopSModifier[i] = 0;
			ShopBModifier[i] = 0;
			ShopName[i] = "";
		}
		TotalShops = 0;
		loadShops("shop_config.cfg");
	}

	public static void shophandler() {
		Misc.println("Shop Handler class successfully loaded");
	}
	public static int restockTimeItem(int itemId) {
		switch(itemId) {
			case 556:
			case 557:
			case 554:
			case 555:
			case 558:
			case 559:
			return 400;
			default:
			return 22000;
		}

	}

	public void process() {
		try {
			boolean DidUpdate = false;
			for (int i = 1; i <= TotalShops - 1; i++) {
				for (int j = 0; j < MaxShopItems; j++) {
					if (ShopItems[i][j] > 0) {
						if (ShopItemsDelay[i][j] >= MaxShowDelay) {
							if (j <= ShopItemsStandard[i]
									&& ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
								if (ShopItemsN[i][j] < ShopItemsSN[i][j] && System.currentTimeMillis() - ShopItemsRestock[i][j] > restockTimeItem(ShopItems[i][j])) {
									ShopItemsN[i][j] += 1;
									DidUpdate = true;
									ShopItemsDelay[i][j] = 1;
									ShopItemsDelay[i][j] = 0;
									DidUpdate = true;
									ShopItemsRestock[i][j] = System.currentTimeMillis();
								}
							} else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
								DiscountItem(i, j);
								ShopItemsDelay[i][j] = 0;
								DidUpdate = true;
							}
						}
						ShopItemsDelay[i][j]++;
					}
				}
				if (DidUpdate == true) {
					for (int k = 1; k < Config.MAX_PLAYERS; k++) {
						if (PlayerHandler.players[k] != null) {
							if (PlayerHandler.players[k].isShopping == true
									&& PlayerHandler.players[k].myShopId == i) {
								PlayerHandler.players[k].updateShop = true;
								DidUpdate = false;
								PlayerHandler.players[k].updateshop(i);
							}
						}
					}
					DidUpdate = false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void DiscountItem(int ShopID, int ArrayID) {
		ShopItemsN[ShopID][ArrayID] -= 1;
		if (ShopItemsN[ShopID][ArrayID] <= 0) {
			ShopItemsN[ShopID][ArrayID] = 0;
			ResetItem(ShopID, ArrayID);
		}
	}

	public void ResetItem(int ShopID, int ArrayID) {
		ShopItems[ShopID][ArrayID] = 0;
		ShopItemsN[ShopID][ArrayID] = 0;
		ShopItemsDelay[ShopID][ArrayID] = 0;
	}

	@SuppressWarnings("resource")
	public boolean loadShops(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[(MaxShopItems * 2)];
		boolean EndOfFile = false;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("./data/cfg/" + FileName));
		} catch (FileNotFoundException fileex) {
			Misc.println(FileName + ": file not found.");
			return false;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			Misc.println(FileName + ": error loading file.");
			return false;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("shop")) {
					int ShopID = Integer.parseInt(token3[0]);
					ShopName[ShopID] = token3[1].replaceAll("_", " ");
					ShopSModifier[ShopID] = Integer.parseInt(token3[2]);
					ShopBModifier[ShopID] = Integer.parseInt(token3[3]);
					for (int i = 0; i < ((token3.length - 4) / 2); i++) {
						if (token3[(4 + (i * 2))] != null) {
							ShopItems[ShopID][i] = (Integer.parseInt(token3[(4 + (i * 2))]) + 1);
							ShopItemsN[ShopID][i] = Integer.parseInt(token3[(5 + (i * 2))]);
							ShopItemsSN[ShopID][i] = Integer.parseInt(token3[(5 + (i * 2))]);
							ShopItemsStandard[ShopID]++;
						} else {
							break;
						}
					}
					TotalShops++;
				}
			} else {
				if (line.equals("[ENDOFSHOPLIST]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}
		return false;
	}
}
