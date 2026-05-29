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
		return "spider, kalrag, nid, sraracha, venenatis spiderling, araxyte, araxxor, " +
				"blessed spider, crypt spider, deadly red spider, fever spider, " +
				"giant crypt spider, giant spider, huge spider, ice spider, jungle spider, " +
				"poison spider, shadow spider, sarachnis, spindel, spindel's spiderling, " +
				"temple spider, venenatis, venenatis' spiderling, sraracha, " +
				"nylocas ischyros, nylocas toxobolos, nylocas hagios, nylocas matomenos, " +
				"nylocas athanatos, nylocas vasilias, nylocas prinkipas, nylocas queen, " +
				"verzik vitur, lil' zik, lil' nylo";
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