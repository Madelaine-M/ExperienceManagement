package support;

import model.domain.CustomerNote;
import model.workflow.RecoveryActionSummary;

import java.util.List;

// Encodes recommendation actions as structured text in customer notes.
// This allows the system to detect previously sent recommendations without a dedicated table.
public final class RecoveryActionNoteCodec {
    private static final String HEADER = "Recovery mail sent.";
    private static final String INCIDENT_PREFIX = "Incident id: ";
    private static final String RESOLUTION_TYPE_PREFIX = "Resolution type: ";
    private static final String OPTION_PREFIX = "Resolved by option: ";
    private static final String RECOMMENDATION_PREFIX = "Selected recommendation: ";
    private static final String SUBJECT_PREFIX = "Subject: ";
    private static final String MAIL_BODY_PREFIX = "Mail body:\n";
    private static final String RECOMMENDATION_RESOLUTION_TYPE = "recommendation";

    private RecoveryActionNoteCodec() {
    }

    public static String formatRecommendationResolution(int optionNumber,
                                                        String selectedRecommendation,
                                                        String subject,
                                                        String mailBody) {
        return formatRecommendationResolution(0, optionNumber, selectedRecommendation, subject, mailBody);
    }

    public static String formatRecommendationResolution(int incidentId,
                                                        int optionNumber,
                                                        String selectedRecommendation,
                                                        String subject,
                                                        String mailBody) {
        String incidentLine = incidentId > 0 ? INCIDENT_PREFIX + incidentId + "\n" : "";
        return HEADER + "\n"
                + incidentLine
                + RESOLUTION_TYPE_PREFIX + RECOMMENDATION_RESOLUTION_TYPE + "\n"
                + OPTION_PREFIX + optionNumber + "\n"
                + RECOMMENDATION_PREFIX + selectedRecommendation + "\n"
                + SUBJECT_PREFIX + subject + "\n"
                + MAIL_BODY_PREFIX + mailBody;
    }

    public static RecoveryActionSummary findLatestRecoveryAction(List<CustomerNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return null;
        }

        for (CustomerNote note : notes) {
            RecoveryActionSummary summary = parse(note);
            if (summary != null) {
                return summary;
            }
        }
        return null;
    }

    //AI used
    public static RecoveryActionSummary parse(CustomerNote note) {
        if (note == null || note.getNoteText() == null || !note.getNoteText().startsWith(HEADER)) {
            return null;
        }

        String text = note.getNoteText();
        String resolutionType = extractSingleLine(text, RESOLUTION_TYPE_PREFIX);
        if (!RECOMMENDATION_RESOLUTION_TYPE.equalsIgnoreCase(resolutionType)) {
            return null;
        }

        String optionValue = extractSingleLine(text, OPTION_PREFIX);
        String recommendation = extractSingleLine(text, RECOMMENDATION_PREFIX);
        String subject = extractSingleLine(text, SUBJECT_PREFIX);
        String mailBody = extractMailBody(text);
        if (optionValue == null || recommendation == null || subject == null || mailBody == null) {
            return null;
        }

        try {
            return new RecoveryActionSummary(
                    note.getAdvisorId(),
                    null,
                    note.getUpdatedAt() != null ? note.getUpdatedAt() : note.getCreatedAt(),
                    Integer.parseInt(optionValue.trim()),
                    recommendation,
                    subject,
                    mailBody
            );
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String extractSingleLine(String text, String prefix) {
        int startIndex = text.indexOf(prefix);
        if (startIndex < 0) {
            return null;
        }
        int valueStart = startIndex + prefix.length();
        int lineEnd = text.indexOf('\n', valueStart);
        if (lineEnd < 0) {
            return text.substring(valueStart).trim();
        }
        return text.substring(valueStart, lineEnd).trim();
    }

    private static String extractMailBody(String text) {
        int startIndex = text.indexOf(MAIL_BODY_PREFIX);
        if (startIndex < 0) {
            return null;
        }
        return text.substring(startIndex + MAIL_BODY_PREFIX.length()).trim();
    }
}
