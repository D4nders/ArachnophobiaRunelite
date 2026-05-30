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
		tags = {"arachnophobia", "spider", "hide", "accessibility", "silhouette", "nylocas"}
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
			boolean isCurrentlyObscured = threatEvaluator.evaluate(targetNpc);
			String menuOption = isCurrentlyObscured ? "Reveal" : "Obscure";

			runescapeClient.createMenuEntry(-1)
					.setOption(menuOption)
					.setTarget(menuEntryEvent.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(event -> toggleNpcInConfig(targetNpcName, isCurrentlyObscured));
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

	private void toggleNpcInConfig(String npcName, boolean remove) {
		String[] entries = pluginConfig.obscuredNpcs().split(",");
		List<String> currentNpcs = new ArrayList<>();

		for (String entry : entries) {
			String trimmed = entry.trim().toLowerCase();
			if (!trimmed.isEmpty()) {
				currentNpcs.add(trimmed);
			}
		}

		String lowerCaseName = npcName.toLowerCase();

		if (remove) {
			currentNpcs.removeIf(name -> name.equals(lowerCaseName));
		} else if (!currentNpcs.contains(lowerCaseName)) {
			currentNpcs.add(lowerCaseName);
		}

		String newList = String.join(", ", currentNpcs);
		configManager.setConfiguration(CONFIG_GROUP, "obscuredNpcs", newList);
	}
}