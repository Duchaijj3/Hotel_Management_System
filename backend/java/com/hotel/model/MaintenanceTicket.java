package java.com.hotel.model;

// File: src/main/java/com/hotel/model/MaintenanceTicket.java

import java.time.LocalDateTime;

/**
 * MaintenanceTicket entity - tracks room maintenance/repair issues
 */
public class MaintenanceTicket {
    private long maintenanceTicketId;
    private long roomId;
    private long reportedByUserId;        // Staff who reported the issue
    private long assignedStaffUserId;     // Maintenance person assigned
    private String ticketCode;            // Unique ticket identifier
    private String title;
    private String description;
    private String priorityCode;          // LOW, NORMAL, HIGH, URGENT
    private String statusCode;            // OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED
    private LocalDateTime reportedAt;
    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;
    private String resolutionNote;

    public MaintenanceTicket() {}

    // Getters and Setters
    public long getMaintenanceTicketId() { return maintenanceTicketId; }
    public void setMaintenanceTicketId(long maintenanceTicketId) { this.maintenanceTicketId = maintenanceTicketId; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    // ... add all other getters/setters
}