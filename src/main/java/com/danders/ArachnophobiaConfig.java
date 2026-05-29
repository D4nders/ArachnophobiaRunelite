package com.danders;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("danders")
public interface ArachnophobiaConfig extends Config {

	@ConfigItem(
			keyName = "customThreats",
			name = "Additional NPCs",
			description = "Comma-separated list of additional NPC names or IDs to obscure",
			position = 1
	)
	default String customThreats() {
		return "";
	}

	@ConfigItem(
			keyName = "ignoredThreats",
			name = "Ignored NPCs",
			description = "Comma-separated list of NPC names or IDs to NOT obscure",
			position = 2
	)
	default String ignoredThreats() {
		return "";
	}

	@ConfigItem(
			keyName = "showShiftClickMenu",
			name = "Show Shift-Click Menu",
			description = "Enable shift-right-click options on NPCs to quickly add them to your custom or ignored lists",
			position = 3
	)
	default boolean showShiftClickMenu() {
		return true;
	}
}