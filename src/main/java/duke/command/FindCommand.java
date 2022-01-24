package duke.command;

import duke.exception.DukeException;
import duke.function.Storage;
import duke.function.TaskList;
import duke.function.Ui;

import java.util.StringTokenizer;

public class FindCommand extends Command {
    String keyword;
    DukeException exception;

    public FindCommand(String fullCommand) {
        super(fullCommand);

        String[] fullCommandParsed = fullCommand.split(" ");
        try {
            this.keyword = fullCommandParsed[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.exception = new DukeException("Please input a keyword so that I can find your task for you!");
        }
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (this.exception != null) {
            throw this.exception;
        }
        TaskList filteredTasks = tasks.filterByKeyword(this.keyword);
        filteredTasks.printTasks(ui);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
