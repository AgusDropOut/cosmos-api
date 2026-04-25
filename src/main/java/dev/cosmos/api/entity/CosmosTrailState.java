package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayDeque;
import java.util.Deque;

public class CosmosTrailState {
    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory;
    private final ResourceLocation trailId;

    public CosmosTrailState(ResourceLocation trailId, int maxHistory) {
        this.trailId = trailId;
        this.maxHistory = maxHistory;
    }

    public ResourceLocation getTrailId() { return this.trailId; }
    public Deque<Vec3> getHistory() { return this.history; }


    public void tickHistory(Vec3 currentPos) {
        this.history.addFirst(currentPos);
        if (this.history.size() > this.maxHistory) {
            this.history.removeLast();
        }
    }
}