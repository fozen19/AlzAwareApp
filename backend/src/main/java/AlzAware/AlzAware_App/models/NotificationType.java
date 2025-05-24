package AlzAware.AlzAware_App.models;

import lombok.Getter;

@Getter
public enum NotificationType {
    HELP("Help Request"),
    SAFEZONE("Safe Zone Alert"),
    MEDICATION("Medication Alert"),
    APPOINTMENT("Appointment Reminder");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }
}
