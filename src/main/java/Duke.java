import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;

public class Duke {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String SAVE_FILE_PATH = "src/main/data/save.txt";

        // Storage for all the items listed by the user
        List<Task> tasks = new ArrayList<Task>();
        boolean hasUpdated = false;

        // Loading up the file
        File file = new File(SAVE_FILE_PATH);
        if (file.exists()) {
            Scanner fileSc = new Scanner(file);
            List<String> errors = new ArrayList<String>();
            while (fileSc.hasNextLine()) {
                String[] line = fileSc.nextLine().split(";");
                Task task;
                try {
                    // TODO Task
                    if (line[0].equals("T")) {
                        task = new ToDoTask(line[2]);
                    }
                    // Deadline Task
                    else if (line[0].equals("D")) {
                        task = new DeadlineTask(line[2], line[3], LocalDateTime.parse(line[4]));
                    }
                    // Event Task
                    else if (line[0].equals("E")) {
                        task = new EventTask(line[2], line[3], LocalDateTime.parse(line[4]));
                    } else {
                        throw new Exception("An invalid task type was read");
                    }
                    if (line[1].equals("X")) task.mark();
                    tasks.add(task);
                } catch (Exception e) {
                    System.out.println("An error occurred while restoring your saved tasks");
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("Hello! I'm Duck\nWelcome back, your tasks have been restored! *quack*");
        } else {
            System.out.println("Hello! I'm Duck\nWhat can I do for you? *quack*");
        }

        // Scanner for reading user input
        Scanner sc = new Scanner(System.in);
        String command = ""; //Where user input will be stored

        // Program will keep taking in new user input until terminated
        while (true) {
            StringTokenizer st = new StringTokenizer(sc.nextLine());
            command = st.nextToken();
            System.out.println("____________________________________________________________");

            // User terminates the program
            if (command.equals("bye"))  {
                System.out.println("Aww. Hope to see you again soon! *quack*");
                return;
            }
            // User lists out all stored items
            else if (command.equals("list")) {
                if (tasks.size() == 0) {
                    System.out.println("You currently do not have any tasks *quack*, please add some more");
                    continue;
                }

                System.out.println("These are your tasks *quack*:");
                for (int i = 0; i < tasks.size(); i++) {
                    int number = i + 1;
                    Task task = tasks.get(i);
                    System.out.println(String.format("%d. %s", number, task.toString()));
                }
            }
            // Add a ToDo Task OR Deadline Task OR Event Task
            else if (command.equals("todo") || command.equals("deadline") || command.equals("event")) {
                //Reading in the name and preposition
                List<String> nameArray = new ArrayList<>();
                String preposition = "";
                while (st.hasMoreTokens()) {
                    String nextToken = st.nextToken();
                    if (command.equals("todo") || nextToken.charAt(0) != '/') {
                        nameArray.add(nextToken);
                    }
                    //Only take in preposition if it is not a todo
                    else {
                        preposition = nextToken.substring(1);
                        break;
                    }
                }
                String name = String.join(" ", nameArray);
                //Error handling for no name
                if (name.equals("")) {
                    System.out.println("Please add a description of your todo as it cannot be empty! *quack*");
                    continue;
                }
                //Setting up deadline/ date
                String date = "";
                String time = "";
                if (st.hasMoreTokens()) date = st.nextToken();
                if (st.hasMoreTokens()) time = st.nextToken();
                if ((date.equals("") || time.equals(""))) {
                    if (command.equals("deadline")) {
                        System.out.println("Please add a date and time for your deadline in the following format 'deadline <name> /by <YYYY-MM-DD> <HH:MM>' ! *quack*");
                        continue;
                    } else if (command.equals("event")) {
                        System.out.println("Please add a date and time for your event in the following format 'event <name> /on <YYYY-MM-DD> <HH:MM>' ! *quack*");
                        continue;
                    }
                }
                LocalDateTime dateTime = LocalDateTime.now();
                try {
                    dateTime = LocalDateTime.parse(date + "T" + time);
                } catch (Exception e) {
                    if (command.equals("deadline")) {
                        System.out.println("Please add a date and time for your deadline in the following format 'deadline <name> /by <YYYY-MM-DD> <HH:MM>' ! *quack*");
                        continue;
                    } else if (command.equals("event")) {
                        System.out.println("Please add a date and time for your event in the following format 'event <name> /on <YYYY-MM-DD> <HH:MM>' ! *quack*");
                        continue;
                    }
                }
                //Creating the new task
                Task newTask = new Task("placeholder task");
                if (command.equals("todo")) newTask = new ToDoTask(name);
                else if (command.equals("deadline")) newTask = new DeadlineTask(name, preposition, dateTime);
                else if (command.equals("event")) newTask = new EventTask(name, preposition, dateTime);
                tasks.add(newTask);
                hasUpdated = true;
                //Output to update user
                System.out.println("Got it. I've added this task *quack*:");
                System.out.println(String.format("  %s", newTask.toString()));
                System.out.println(String.format("Now you have %d task%s in the list *quack*.", tasks.size(), tasks.size() == 1 ? "" : "s"));
            }
            // User marks or unmarks or deletes a task
            else if (command.equals("mark") || command.equals("unmark") || command.equals("delete")) {
                // Error handling if user does not input a second argument
                if (!st.hasMoreTokens()) {
                    System.out.println("Please input an item number (eg. 'mark 1', 'unmark 1', or 'delete 1')");
                } else {
                    // Error handling
                    try {
                        int number = Integer.parseInt(st.nextToken());
                        //If the number given is out of range
                        if (number <= 0 || number > tasks.size()) throw new DukeException("OutOfTaskRangeException");
                        Task task = tasks.get(number - 1);
                        String action = "";
                        String additionalText = "";
                        //Deleting a task
                        if (command.equals("delete")) {
                            tasks.remove(number - 1);
                            action = "delet";
                            hasUpdated = true;
                        }
                        //Marking a task as done
                        else if (command.equals("mark")) {
                            task.mark();
                            additionalText = " as done";
                            action = command;
                            hasUpdated = true;
                        }
                        //Unmarking a task
                        else if (command.equals("unmark")) {
                            task.unmark();
                            action = command;
                            hasUpdated = true;
                        }
                        System.out.println(String.format("I've %sed task %d%s! *quack*", action, number, additionalText));
                        System.out.println(String.format("  %d. %s", number, task.toString()));
                    } catch (NumberFormatException e) {
                        System.out.println("Please ONLY input integers (eg. 'mark 1', 'unmark 1', 'delete 1')");
                    } catch (DukeException e) {
                        System.out.println("Please ONLY input integers that correspond to tasks (type 'list' to see your tasks)");
                    }
                }
            }
            // User deletes a task
            else if (command.equals("delete")) {
                // Error handling if user does not input a second argument
                if (!st.hasMoreTokens()) {
                    System.out.println("Please input an item number when deleting (eg. 'delete 1')");
                } else {
                    // Error handling if user inputs strings
                    try {
                        int number = Integer.parseInt(st.nextToken());
                        Task removedTask = tasks.remove(number - 1);
                        System.out.println(String.format("I've deleted task %d! *quack*", number));
                        System.out.println(String.format("  %s", removedTask.toString()));
                    } catch (Exception e) {
                        System.out.println("Please ONLY input integers when deleting (eg. 'delete 1')");
                    }
                }
            }
            // Help command
            else if (command.equals("help")) {
                System.out.println("These are the commands you can use *quack*:");
                System.out.println("  'list' to list out all your tasks");
                System.out.println("  'todo <description>' to add a todo task");
                System.out.println("  'deadline /<preposition> <description>' to add a task with a deadline");
                System.out.println("  'event /<preposition> <description>' to add an event with a date");
                System.out.println("  'mark <task number>' to mark a task as done");
                System.out.println("  'unmark <task number>' to unmark a task as done");
                System.out.println("  'delete <task number>' to delete a task");
                System.out.println("  'bye' to close your Duck app");
                System.out.println("*quack*");
            }
            // User adds items to the list
            else {
                System.out.println(String.format("That is not a valid command *quack*\nType 'help' to see a list of valid commands"));
            }
            System.out.println("____________________________________________________________");

            // Saving changes
            if (hasUpdated) {
                FileWriter fileWriter = new FileWriter(SAVE_FILE_PATH);
                for (Task task: tasks) {
                    String taskString = "";
                    if (task instanceof ToDoTask) {
                        taskString += "T;";
                    } else if (task instanceof DeadlineTask) {
                        taskString += "D;";
                    } else if (task instanceof EventTask) {
                        taskString += "E;";
                    }
                    if (task.done) {
                        taskString += "X;";
                    } else {
                        taskString += "O;";
                    }
                    taskString += task.name;
                    if (task instanceof DeadlineTask) {
                        DeadlineTask dTask = (DeadlineTask) task;
                        taskString += ";";
                        taskString += dTask.preposition;
                        taskString += ";";
                        taskString += dTask.dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
                    } else if (task instanceof EventTask) {
                        EventTask dTask = (EventTask) task;
                        taskString += ";";
                        taskString += dTask.preposition;
                        taskString += ";";
                        taskString += dTask.dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
                    }
                    fileWriter.write(taskString);
                    fileWriter.write(System.lineSeparator());
                }
                fileWriter.close();
                hasUpdated = false;
            }
        }
    }
}
