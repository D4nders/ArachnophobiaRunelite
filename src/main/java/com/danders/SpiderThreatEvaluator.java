package com.danders;

import net.runelite.api.NPC;
import java.util.HashSet;
import java.util.Set;

public class SpiderThreatEvaluator implements ThreatEvaluator {
    private final Set<String> obscuredNames = new HashSet<>();
    private final Set<Integer> obscuredIds = new HashSet<>();

    @Override
    public void updateObscuredNpcs(String input) {
        obscuredNames.clear();
        obscuredIds.clear();

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] entries = input.split(",");
        for (String entry : entries) {
            String trimmedEntry = entry.trim().toLowerCase();
            if (trimmedEntry.isEmpty()) {
                continue;
            }
            try {
                obscuredIds.add(Integer.parseInt(trimmedEntry));
            } catch (NumberFormatException exception) {
                obscuredNames.add(trimmedEntry);
            }
        }
    }

    @Override
    public boolean evaluate(NPC npc) {
        if (obscuredIds.contains(npc.getId())) {
            return true;
        }

        String npcName = npc.getName();
        if (npcName == null) {
            return false;
        }

        String lowerCaseName = npcName.toLowerCase();

        for (String name : obscuredNames) {
            if (lowerCaseName.equals(name)) {
                return true;
            }
        }

        return false;
    }
}