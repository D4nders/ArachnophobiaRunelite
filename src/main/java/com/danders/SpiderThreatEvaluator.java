package com.danders;

import net.runelite.api.NPC;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpiderThreatEvaluator implements ThreatEvaluator {
    private static final Set<String> DEFAULT_THREAT_NAMES = new HashSet<>(Arrays.asList(
            "spider", "sarachnis", "venenatis", "araxxor", "spindel", "nylocas"
    ));

    private final Set<String> customThreatNames = new HashSet<>();
    private final Set<Integer> customThreatIds = new HashSet<>();

    private final Set<String> ignoredThreatNames = new HashSet<>();
    private final Set<Integer> ignoredThreatIds = new HashSet<>();

    @Override
    public void updateCustomThreats(String customThreatsInput) {
        customThreatNames.clear();
        customThreatIds.clear();
        populateEvaluationSets(customThreatsInput, customThreatNames, customThreatIds);
    }

    @Override
    public void updateIgnoredThreats(String ignoredThreatsInput) {
        ignoredThreatNames.clear();
        ignoredThreatIds.clear();
        populateEvaluationSets(ignoredThreatsInput, ignoredThreatNames, ignoredThreatIds);
    }

    private void populateEvaluationSets(String inputString, Set<String> nameTargetSet, Set<Integer> idTargetSet) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return;
        }

        String[] entries = inputString.split(",");
        for (String entry : entries) {
            String trimmedEntry = entry.trim().toLowerCase();
            if (trimmedEntry.isEmpty()) {
                continue;
            }
            try {
                idTargetSet.add(Integer.parseInt(trimmedEntry));
            } catch (NumberFormatException exception) {
                nameTargetSet.add(trimmedEntry);
            }
        }
    }

    @Override
    public boolean evaluate(NPC npc) {
        if (ignoredThreatIds.contains(npc.getId())) {
            return false;
        }

        String npcName = npc.getName();
        if (npcName == null) {
            return false;
        }

        String lowerCaseName = npcName.toLowerCase();

        for (String ignoredName : ignoredThreatNames) {
            if (lowerCaseName.contains(ignoredName)) {
                return false;
            }
        }

        if (customThreatIds.contains(npc.getId())) {
            return true;
        }

        for (String customName : customThreatNames) {
            if (lowerCaseName.contains(customName)) {
                return true;
            }
        }

        for (String defaultName : DEFAULT_THREAT_NAMES) {
            if (lowerCaseName.contains(defaultName)) {
                return true;
            }
        }

        return false;
    }
}