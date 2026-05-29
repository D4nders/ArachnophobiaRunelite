package com.danders;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("arachnophobia")
public interface ArachnophobiaConfig extends Config {

	@ConfigItem(
			keyName = "obscuredNpcs",
			name = "Obscured NPCs",
			description = "Comma-separated list of NPC names or IDs to obscure",
			position = 1
	)
	default String obscuredNpcs() {
		return "spider, giant spider, sarachnis, venenatis, araxxor, spindel, nylocas";
	}

	@ConfigItem(
			keyName = "showShiftClickMenu",
			name = "Show Shift-Click Menu",
			description = "Enable shift-right-click options on NPCs to quickly obscure or reveal them",
			position = 2
	)
	default boolean showShiftClickMenu() {
		return true;
	}
}