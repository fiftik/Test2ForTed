package console.test;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
 
public class Main {
 
    private static final String MSG_COMMAND_NOT_FOUND = "Command not found";
    private static final String MSG_DELIM = "==========================================";
 
    private Map<String, Command> commands;
 
    private String consoleEncoding;
 
    public Main(String consoleEncoding) {
        commands = new TreeMap<>();
        Command cmd = new HelpCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new LsCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new PsCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new PwdCommand();
        commands.put(cmd.getName(), cmd);
        cmd = new ExitCommand();
        commands.put(cmd.getName(), cmd);
        this.consoleEncoding = consoleEncoding;
    }
 
    public void execute() {
        Context c = new Context();
        c.currentDirectory = new File(".").getAbsoluteFile();
        boolean result = true;
        Scanner scanner = new Scanner(System.in, consoleEncoding);
        do {
            System.out.print("> ");
            String fullCommand = scanner.nextLine();
            ParsedCommand pc = new ParsedCommand(fullCommand);
            if (pc.command == null || "".equals(pc.command)) {
                continue;
            }
            Command cmd = commands.get(pc.command.toUpperCase());
            if (cmd == null) {
                System.out.println(MSG_COMMAND_NOT_FOUND);
                continue;
            }
            System.out.println(pc.command);
            result = cmd.execute(c, pc.args);
        } while (result);
    }
 
    public static void main(String[] args) {
        Main cp = new Main("Cp1251");
        cp.execute();
    }
 
 
    class ParsedCommand {
 
        String command;
 
        String[] args;
 
        public ParsedCommand(String line) {
            String parts[] = line.split(" ");
            if (parts != null) {
                command = parts[0];
                if (parts.length > 1) {
                    args = new String[parts.length - 1];
                    System.arraycopy(parts, 1, args, 0, args.length);
                }
            }
        }
    }
 
    interface Command {
 
        boolean execute(Context context, String... args);
 
        void printHelp();
 
        String getName();
 
        String getDescription();
    }
 
    class Context {
 
        private File currentDirectory;
 
    }
 
    class HelpCommand implements Command {
 
        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                System.out.println("Avaliable commands:\n" + MSG_DELIM);
                for (Command cmd : commands.values()) {
                    System.out.println(cmd.getName() + ": " + cmd.getDescription());
                }
                System.out.println(MSG_DELIM);
            } else {
                for (String cmd : args) {
                    System.out.println("Help for command " + cmd + ":\n" + MSG_DELIM);
                    Command command = commands.get(cmd.toUpperCase());
                    if (command == null) {
                        System.out.println(MSG_COMMAND_NOT_FOUND);
                    } else {
                        command.printHelp();
                    }
                    System.out.println(MSG_DELIM);
                }
            }
            return true;
        }
 
        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }
 
        @Override
        public String getName() {
            return "HELP";
        }
 
        @Override
        public String getDescription() {
            return "Prints list of available commands";
        }
    }
    
    class PsCommand implements Command {

		@Override
		public boolean execute(Context context, String... args) {
			if (args == null) {
				// print information about a selection of the active processes
				if(isWindows()){
					printProcessesW();
				} else {
					printProcessesU();
				}
			} else {
				// todo
			}
			return true;
		}
		
		 public boolean isWindows(){
		        String os = System.getProperty("os.name").toLowerCase();
		        //windows
		        return (os.indexOf( "win" ) >= 0); 
		}
		
		private void printProcessesW() {
			try {
				System.getProperty("os.name" );
				
			    String line;
			    Process p = Runtime.getRuntime().exec
			    	    (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
			    BufferedReader input =
			            new BufferedReader(new InputStreamReader(p.getInputStream()));
			    while ((line = input.readLine()) != null) {
			        System.out.println(line); //<-- Parse data here.
			    }
			    input.close();
			} catch (Exception err) {
			    err.printStackTrace();
			}
        }
		
		private void printProcessesU() {
			try {
			    String line;
			    Process p = Runtime.getRuntime().exec("ps -e");
			    BufferedReader input =
			            new BufferedReader(new InputStreamReader(p.getInputStream()));
			    while ((line = input.readLine()) != null) {
			        System.out.println(line); //<-- Parse data here.
			    }
			    input.close();
			} catch (Exception err) {
			    err.printStackTrace();
			}
        }

		@Override
		public void printHelp() {
			 System.out.println(getDescription());			
		}

		@Override
		public String getName() {
			return "PS";
		}

		@Override
		public String getDescription() {
			return "Prints information about a selection of the active processes";
		}
    	
    }
    
    class PwdCommand implements Command {

		@Override
		public boolean execute(Context context, String... args) {
			if (args == null) {
				// print path to directory
				printPath(context.currentDirectory);
			} else {
				// todo
			}
			return true;
		}
		
		private void printPath(File dir) {
			Context c = new Context();
		    c.currentDirectory = new File(".").getAbsoluteFile();
			System.out.println(c.currentDirectory);
        }

		@Override
		public void printHelp() {
			 System.out.println(getDescription());
		}

		@Override
		public String getName() {
			return "PWD";
		}

		@Override
		public String getDescription() {
			return "Prints path to directory";
		}
    
    }
 
    class LsCommand implements Command {
 
        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }
 
        @Override
        public boolean execute(Context context, String... args) {
            if (args == null) {
                // print current directory content
                printDir(context.currentDirectory);
            } else {
                // todo
            }
            return true;
        }
 
        @Override
        public String getName() {
            return "LS";
        }
 
        private void printDir(File dir) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    System.out.println(f.getName());
                }
            }
        }
 
        @Override
        public String getDescription() {
            return "Prints directory content";
        }
    }
 
    class ExitCommand implements Command {
        @Override
        public boolean execute(Context context, String... args) {
            System.out.println("Finishing command processor... done.");
            return false;
        }
 
        @Override
        public void printHelp() {
            System.out.println(getDescription());
        }
 
        @Override
        public String getName() {
            return "EXIT";
        }
 
        @Override
        public String getDescription() {
            return "Exits from command processor";
        }
    }
}