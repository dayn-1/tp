package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPOINTMENT_VIEWS;

import seedu.address.commons.core.date.Date;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentType;
import seedu.address.model.appointment.Mark;
import seedu.address.model.appointment.Note;
import seedu.address.model.appointment.TimePeriod;
import seedu.address.model.patient.Nric;

/**
 * Marks an existing appointment in the CLInic as completed.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Mark the appointment of the patient identified as completed"
            + "Parameters: "
            + PREFIX_NRIC + "NRIC "
            + PREFIX_DATE + "DATE "
            + PREFIX_START_TIME + "START_TIME "
            + PREFIX_END_TIME + "END_TIME";

    public static final String MESSAGE_MARK_APPOINTMENT_SUCCESS = "Appointment successfully marked as seen: %1$s";

    private final Nric targetNric;
    private final Date targetDate;
    private final TimePeriod targetTimePeriod;

    /**
     * Creates a MarkCommand to mark the appointment with the
     * specified {@code Nric, Date, TimePeriod}
     * @param targetNric nric of the Patient matching the existing Appointment to be marked
     * @param targetDate date of the existing Appointment to be marked
     * @param targetTimePeriod timePeriod of the existing Appointment to be marked
     */
    public MarkCommand(Nric targetNric, Date targetDate, TimePeriod targetTimePeriod) {
        requireNonNull(targetNric);
        requireNonNull(targetDate);
        requireNonNull(targetTimePeriod);

        this.targetNric = targetNric;
        this.targetDate = targetDate;
        this.targetTimePeriod = targetTimePeriod;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasPatientWithNric(targetNric)) {
            throw new CommandException(Messages.MESSAGE_PATIENT_NRIC_NOT_FOUND);
        }

        Appointment mockAppointmentToMatch = new Appointment(targetNric, targetDate, targetTimePeriod,
            new AppointmentType("Anything"), new Note("Anything"), new Mark(false));
        if (!model.hasAppointment(mockAppointmentToMatch)) {
            throw new CommandException(Messages.MESSAGE_APPOINTMENT_NOT_FOUND);
        }

        Appointment apptToMark = model.getMatchingAppointment(targetNric, targetDate, targetTimePeriod);

        Appointment markedAppt = createMarkedAppointment(apptToMark);
        model.setAppointment(apptToMark, markedAppt);
        model.updateFilteredAppointmentViewList(PREDICATE_SHOW_ALL_APPOINTMENT_VIEWS);
        return new CommandResult(String.format(MESSAGE_MARK_APPOINTMENT_SUCCESS, Messages.format(markedAppt)));
    }

    /**
     * Creates and returns a {@code Appointment} that is marked
     */
    private static Appointment createMarkedAppointment(Appointment apptToMark) {
        assert apptToMark != null;
        return new Appointment(apptToMark.getNric(), apptToMark.getDate(), apptToMark.getTimePeriod(),
                apptToMark.getAppointmentType(), apptToMark.getNote(), new Mark(true));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;
        return targetNric.equals(otherMarkCommand.targetNric)
                && targetDate.equals(otherMarkCommand.targetDate)
                && targetTimePeriod.equals(otherMarkCommand.targetTimePeriod);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nric", targetNric)
                .add("date", targetDate)
                .add("timePeriod", targetTimePeriod)
                .toString();
    }
}
