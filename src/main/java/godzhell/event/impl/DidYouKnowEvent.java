package godzhell.event.impl;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.text.WordUtils;

import godzhell.event.Event;
import godzhell.model.players.PlayerHandler;
import godzhell.util.Misc;

public class DidYouKnowEvent extends Event<Object> {

	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCyclesOrDefault(5, 5, TimeUnit.MINUTES);

	/**
	 * A {@link Collection} of messages that are to be displayed
	 */
	private final List<String> MESSAGES = Misc.jsonArrayToList(Paths.get("Data", "json", "did_you_know.json"), String[].class);

	/**
	 * The index or position in the list that we're currently at
	 */
	private int position;

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public DidYouKnowEvent() {
		super(new String(), new Object(), INTERVAL);
	}

	@Override
	public void execute() {
		position++;
		if (position >= MESSAGES.size()) {
			position = 0;
		}
		List<String> messages = Arrays.asList(WordUtils.wrap(MESSAGES.get(position), 65).split("\\n"));
		messages.set(0, "[<col=255>News</col>] " + messages.get(0));
		PlayerHandler.nonNullStream().forEach(player -> {
			if (player.didYouKnow)
				messages.forEach(m -> player.sendMessage(m));
		});
	}

}
