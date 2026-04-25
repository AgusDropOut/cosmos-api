package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CosmosTrailState {
    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory;
    private final List<ResourceLocation> trailIds;


    private CosmosTrailState(int maxHistory, List<ResourceLocation> trailIds) {
        this.maxHistory = maxHistory;
        this.trailIds = new ArrayList<>(trailIds);
    }

    public Deque<Vec3> getHistory() { return this.history; }
    public List<ResourceLocation> getTrailIds() { return this.trailIds; }

    public void tickHistory(Vec3 currentPos) {
        this.history.addFirst(currentPos);
        if (this.history.size() > this.maxHistory) {
            this.history.removeLast();
        }
    }

    // BUILDER PATTERN
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxHistory = 20; // Default fallback
        private final List<ResourceLocation> trailIds = new ArrayList<>();

        public Builder setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
            return this;
        }

        public Builder addTrail(ResourceLocation id) {
            if (!this.trailIds.contains(id)) {
                this.trailIds.add(id);
            }
            return this;
        }

        public CosmosTrailState build() {
            if (this.trailIds.isEmpty()) {
                throw new IllegalStateException("Cosmos API Error: A Trail Entity was created without a Trail ID!");
            }
            return new CosmosTrailState(this.maxHistory, this.trailIds);
        }
    }
}