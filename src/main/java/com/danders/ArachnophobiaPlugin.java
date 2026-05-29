package com.danders;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@PluginDescriptor(
		name = "Arachnophobia Mode",
		description = "Obscures spiders and custom user-defined NPCs",
		tags = {"arachnophobia", "spider", "hide"}
)
public class ArachnophobiaPlugin extends Plugin {
	@Inject
	private Client runescapeClient;

	@Inject
	private ArachnophobiaConfig pluginConfig;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ArachnophobiaOverlay arachnophobiaOverlay;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientThread clientThread;

	private ThreatEvaluator threatEvaluator;
	private final List<NPC> activeThreatEntities = new CopyOnWriteArrayList<>();
	private static final String CONFIG_GROUP = "arachnophobia";

	@Provides
	ArachnophobiaConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ArachnophobiaConfig.class);
	}

	@Override
	protected void startUp() {
		threatEvaluator = new SpiderThreatEvaluator();
		threatEvaluator.updateObscuredNpcs(pluginConfig.obscuredNpcs());
		overlayManager.add(arachnophobiaOverlay);

		clientThread.invokeLater(this::evaluateAllActiveNpcs);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(arachnophobiaOverlay);
		activeThreatEntities.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChangedEvent) {
		if (configChangedEvent.getGroup().equals(CONFIG_GROUP)) {
			threatEvaluator.updateObscuredNpcs(pluginConfig.obscuredNpcs());
			clientThread.invokeLater(this::evaluateAllActiveNpcs);
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawnedEvent) {
		NPC spawnedNpc = npcSpawnedEvent.getNpc();
		if (threatEvaluator.evaluate(spawnedNpc)) {
			activeThreatEntities.add(spawnedNpc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawnedEvent) {
		activeThreatEntities.remove(npcDespawnedEvent.getNpc());
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded menuEntryEvent) {
		if (!pluginConfig.showShiftClickMenu() || !runescapeClient.isKeyPressed(KeyCode.KC_SHIFT)) {
			return;
		}

		if (menuEntryEvent.getType() == MenuAction.EXAMINE_NPC.getId()) {
			NPC targetNpc = menuEntryEvent.getMenuEntry().getNpc();
			if (targetNpc == null || targetNpc.getName() == null) {
				return;
			}

			String targetNpcName = targetNpc.getName();
			boolean isAlreadyObscured = threatEvaluator.evaluate(targetNpc);

			if (isAlreadyObscured) {
				runescapeClient.createMenuEntry(-1)
						.setOption("Reveal")
						.setTarget(menuEntryEvent.getTarget())
						.setType(MenuAction.RUNELITE)
						.onClick(event -> toggleNpcInConfig(targetNpcName, false));
			} else {
				runescapeClient.createMenuEntry(-1)
						.setOption("Obscure")
						.setTarget(menuEntryEvent.getTarget())
						.setType(MenuAction.RUNELITE)
						.onClick(event -> toggleNpcInConfig(targetNpcName, true));
			}
		}
	}

	public List<NPC> retrieveActiveThreats() {
		return Collections.unmodifiableList(activeThreatEntities);
	}

	private void evaluateAllActiveNpcs() {
		activeThreatEntities.clear();
		if (runescapeClient.getNpcs() == null) {
			return;
		}
		for (NPC npc : runescapeClient.getNpcs()) {
			if (threatEvaluator.evaluate(npc)) {
				activeThreatEntities.add(npc);
			}
		}
	}

	private void toggleNpcInConfig(String npcName, boolean addToList) {
		String currentList = pluginConfig.obscuredNpcs();
		List<String> items = new ArrayList<>();

		if (currentList != null && !currentList.isEmpty()) {
			for (String s : currentList.split(",")) {
				String trimmed = s.trim().toLowerCase();
				if (!trimmed.isEmpty()) {
					items.add(trimmed);
				}
			}
		}

		String targetName = npcName.toLowerCase();

		if (addToList && !items.contains(targetName)) {
			items.add(targetName);
		} else if (!addToList && items.contains(targetName)) {
			items.remove(targetName);
		} else {
			return; // No change needed
		}

		String newList = String.join(", ", items);
		configManager.setConfiguration(CONFIG_GROUP, "obscuredNpcs", newList);
	}
}