package com.danders;

import net.runelite.api.NPC;
import java.util.HashSet;
import java.util.Set;

public class SpiderThreatEvaluator implements ThreatEvaluator {
    private final Set<String> obscuredNames = new HashSet<>();

    @Override
    public void updateObscuredNpcs(String input) {
        obscuredNames.clear();

        String[] entries = input.split(",");
        for (String entry : entries) {
            String trimmedEntry = entry.trim().toLowerCase();

            if (!trimmedEntry.isEmpty()) {
                obscuredNames.add(trimmedEntry);
            }
        }
    }

    @Override
    public boolean evaluate(NPC npc) {
        if (npc == null || npc.getName() == null) {
            return false;
        }

        return obscuredNames.contains(npc.getName().toLowerCase());
    }
}