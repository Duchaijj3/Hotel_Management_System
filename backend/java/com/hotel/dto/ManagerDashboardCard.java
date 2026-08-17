package com.hotel.dto;

import java.util.List;

public record ManagerDashboardCard(String id, String title, String description,
                                   String icon, List<String> useCases,
                                   String targetPath, boolean enabled) {
    public ManagerDashboardCard {
        useCases = List.copyOf(useCases);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public List<String> getUseCases() { return useCases; }
    public String getTargetPath() { return targetPath; }
    public boolean isEnabled() { return enabled; }
}
