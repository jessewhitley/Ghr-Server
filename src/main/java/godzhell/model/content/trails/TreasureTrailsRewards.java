package godzhell.model.content.trails;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import godzhell.util.Misc;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TreasureTrailsRewards {

	private static Map<RewardLevel, Map<RewardRarity, List<TreasureTrailsRewardItem>>> rewards;
	private static FileReader fileReader;
	public static final Map<RewardLevel, List<TreasureTrailsRewardItem>> possibleDrops = new HashMap<>();

	public static Map<RewardLevel, Map<RewardRarity, List<TreasureTrailsRewardItem>>> getRewards() {
		return rewards;
	}

	public static List<TreasureTrailsRewardItem> getRandomRewards(RewardLevel difficulty) {
		List<TreasureTrailsRewardItem> result =new ArrayList<>();

		int rolls = 1 + Misc.random(2);

		if (difficulty == RewardLevel.MEDIUM) {
			rolls += 1;
		} else if (difficulty == RewardLevel.HARD) {
			rolls += 2;
		}

		double random = Math.random();
		while (random > 0.05) {
			random = Math.random();
		}
		result.add(getReward(random, difficulty));

		for (int i = 0; i < rolls; i++) {
			TreasureTrailsRewardItem reward = getReward(Math.random(), difficulty);
			boolean flag = false;
			for (TreasureTrailsRewardItem element : result) {
				if (element.getId() == reward.getId()) {
					element.setMinAmount(element.getMinAmount() + reward.getMinAmount());
					element.setMaxAmount(element.getMaxAmount() + reward.getMaxAmount());
					flag = true;
					break;
				}
			}
			if (!flag) {
				result.add(reward);
			}
		}
		Collections.shuffle(result);
		return result;
	}

	private static TreasureTrailsRewardItem getReward(double roll, RewardLevel level) {
		List<TreasureTrailsRewardItem> rewardList;
		if (roll <= 0.001 && containsReward(level, RewardRarity.VERY_RARE)) {
			rewardList = rewards.get(level).get(RewardRarity.VERY_RARE);
		} else if (roll <= 0.009 && containsReward(level, RewardRarity.RARE)) {
			rewardList = rewards.get(level).get(RewardRarity.RARE);
		} else if (roll <= 0.07 && containsReward(level, RewardRarity.UNCOMMON)) {
			rewardList = rewards.get(level).get(RewardRarity.UNCOMMON);
		} else {
			rewardList = rewards.get(level).get(RewardRarity.COMMON);
		}
		if (rewardList.size() == 1) {
			return rewardList.get(0);
		} else {
			return rewardList.get(Misc.random(rewardList.size() - 2));
		}
	}

	private static boolean containsReward(RewardLevel level, RewardRarity rarity) {
		List<TreasureTrailsRewardItem> rewardList = rewards.get(level).get(rarity);
		return rewardList != null && rewardList.size() > 0;
	}

	public static void read() {
		rewards =new HashMap<>();
		JSONParser parser = new JSONParser();
		try {
			fileReader = new FileReader("./data/json/clue_rewards.json");
			JSONArray data = (JSONArray) parser.parse(fileReader);
			for (Object aData : data) {
				JSONObject item=(JSONObject) aData;
				int id=((Long) item.get("item_id")).intValue();
				int minAmount=((Long) item.get("min_amount")).intValue();
				int maxAmount=((Long) item.get("max_amount")).intValue();
				TreasureTrailsRewardItem reward=new TreasureTrailsRewardItem(id, minAmount, maxAmount);
				RewardRarity rarity=RewardRarity.valueOf(((String) item.get("rarity")).toUpperCase());
				RewardLevel level=RewardLevel.valueOf(((String) item.get("reward_level")).toUpperCase());
				if (level==RewardLevel.SHARED) {
					add(reward, RewardLevel.EASY, rarity);
					add(reward, RewardLevel.MEDIUM, rarity);
					add(reward, RewardLevel.HARD, rarity);
					add(reward, RewardLevel.MASTER, rarity);
				} else {
					add(reward, level, rarity);
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private static void add(TreasureTrailsRewardItem item, RewardLevel level, RewardRarity rarity) {
		if (rewards.containsKey(level)) {
			if (rewards.get(level).containsKey(rarity)) {
				rewards.get(level).get(rarity).add(item);
			} else {
				List<TreasureTrailsRewardItem> list =new ArrayList<>();
				list.add(item);
				rewards.get(level).put(rarity, list);
			}
		} else {
			List<TreasureTrailsRewardItem> list =new ArrayList<>();
			list.add(item);
			Map<RewardRarity, List<TreasureTrailsRewardItem>> map =new HashMap<>();
			map.put(rarity, list);
			rewards.put(level, map);
		}
	}


	public static List<TreasureTrailsRewardItem> getRewardsForType(int type) {
		RewardLevel level = null;
		switch(type) {
			case 1:
				level = RewardLevel.EASY;
				break;
			case 2:
				level = RewardLevel.MEDIUM;
				break;
			case 3:
				level = RewardLevel.HARD;
				break;
			case 4:
				level = RewardLevel.MASTER;
				break;
		}
		return possibleDrops.get(level);
	}
}
