package com.danders;

import net.runelite.api.NPC;

public interface ThreatEvaluator {
    boolean evaluate(NPC npc);
    void updateObscuredNpcs(String obscuredNpcsInput);
}