import java.util.StringTokenizer;

public class DeleteCommand extends Command {

    int taskNumber;
    DukeException exception;

    DeleteCommand(String fullCommand) {
        super(fullCommand);

        String[] fullCommandSplit = fullCommand.split(" ");
        try {
            this.taskNumber = Integer.parseInt(fullCommandSplit[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.exception = new DukeException("Please input an item number when deleting (eg. 'delete 1')");
        } catch (NumberFormatException e) {
            this.exception = new DukeException("Please only input integers when deleting tasks (eg. 'delete 1");
        }
    }

    @Override
    void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (this.exception != null) throw this.exception;
        if (this.taskNumber <= 0 || this.taskNumber > tasks.size())
            throw new DukeException("Please only input integers within the range of your tasks");

        Task removedTask = tasks.deleteByNumber(this.taskNumber);
        ui.print(String.format("I've deleted task %d! *quack*", this.taskNumber));
        ui.print(String.format("  %s", removedTask.toString()));
    }

    @Override
    boolean isExit() {
        return false;
    }
}
