import java.util.Scanner;

/**
 * Command factory-ul folosit pentru crearea comenzilor
 */
public class CommandFactory
{
    //Unica instanta a clasei
    private static CommandFactory uniqueInstance;

    /**
     * Constructorul este private pentru patternul singleton
     */
    private CommandFactory()
    {

    }

    /**
     * Creeaza(daca este cazul) si intoarce unica instanta a lui CommandFactory
     * @return uniqueInstance
     */
    public static CommandFactory getInstance()
    {
        if (uniqueInstance == null)
        {
            uniqueInstance = new CommandFactory();
        }

        return uniqueInstance;
    }

    /**
     * Construieste o comanda care va primit argumente fara *
     *
     * @param fileSystem    Referinta catre sistemul de fisiere(root)
     * @param stringScanner Scannerul folosit pentru a parsa argumentele
     * @param shouldPrint   true - comenzile construite trebuie sa afiseze mesaje, 0 altfel
     * @return              comanda construita
     */
    public Command createCommand(FileSystem fileSystem, Scanner stringScanner, boolean shouldPrint)
    {
        Command command = null;
        String type = stringScanner.next();

        if (type.equals("ls"))
        {
            boolean isRecursive = false;
            String path = "";
            Command grepCommand = null;

            if(stringScanner.hasNextLine())
            {
                //Verificam daca ls este recursiv sau daca este folosit printr-un pipe cu grep
                String line = stringScanner.nextLine();
                String[] splitLine = line.split("\\|", 2);


                stringScanner = new Scanner(splitLine[0]); //parseaza doar comanda ls, fara grep

                for (int i = 0; i < 2; i++)
                {
                    if (stringScanner.hasNext())
                    {
                        String arg = stringScanner.next();
                        if (arg.equals("-R"))
                        {
                            isRecursive = true;
                        }
                        else
                        {
                            path = arg;
                        }
                    }
                }

                if (splitLine.length == 2)
                {
                    //Daca split a returnat 2 stringuri, inseamna ca avem si grep impreuna cu ls
                    stringScanner = new Scanner(splitLine[1]);
                    stringScanner.next(); // "grep"

                    //Construieste o comanda grep, argumentul este patternul
                    grepCommand = new GrepCommand(fileSystem, stringScanner.next());
                }
            }

            //ls o sa primeasca comanda grep drept argument ca sa poata verifica daca nodurile respecta patternul
            command = new LSCommand(fileSystem, path, isRecursive, grepCommand, shouldPrint);
        }
        else if (type.equals("pwd"))
        {
            command = new PWDCommand(fileSystem);
        }
        else if (type.equals("cd"))
        {
            command = new CDCommand(fileSystem, stringScanner.next());
        }
        else if (type.equals("cp"))
        {
            command = new CPCommand(fileSystem, stringScanner.next(), stringScanner.next());
        }
        else if (type.equals("mv"))
        {
            command = new MVCommand(fileSystem, stringScanner.next(), stringScanner.next());
        }
        else if (type.equals("rm"))
        {
            command = new RMCommand(fileSystem, stringScanner.next(), shouldPrint);
        }
        else if (type.equals("touch"))
        {
            command = new TouchCommand(fileSystem, stringScanner.next(), shouldPrint);
        }
        else if (type.equals("mkdir"))
        {
            command = new MKDirCommand(fileSystem, stringScanner.next(), shouldPrint);
        }

        return command;
    }

    /**
     * Construieste o comanda care va primit argumente cu *
     *
     * @param fileSystem    Referinta catre sistemul de fisiere(root)
     * @param stringScanner Scannerul folosit pentru a parsa argumentele
     * @return              comanda construita
     */
    public Command createStarCommand(FileSystem fileSystem, Scanner stringScanner)
    {
        Command command = null;
        String type = stringScanner.next();

        if (type.equals("ls"))
        {
            command = new LSStarCommand(fileSystem, stringScanner.next());
        }
        else if (type.equals("rm"))
        {
            command = new RMStarCommand(fileSystem, stringScanner.next());
        }
        else if (type.equals("touch"))
        {
            command = new TouchStarCommand(fileSystem, stringScanner.next());
        }
        else if (type.equals("mkdir"))
        {
            command = new MKDirStarCommand(fileSystem, stringScanner.next());
        }

        return command;
    }
}
