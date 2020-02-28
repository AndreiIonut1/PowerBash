import java.util.LinkedList;
import java.util.Scanner;

/**
 * Interfata implementata de fiecare comanda
 */
interface Command
{
    void execute();
}

/**
 * Executa o comanda care implementeaza interfata Command
 */
class CommandInvoker
{
    Command fileSystemCommand;

    /**
     * Seteaza comanda in invoker
     * @param command comanda ce va fi invocata
     */
    public void setCommand(Command command)
    {
        fileSystemCommand = command;
    }

    /**
     * Executa comanda stocata
     */
    public void invokeCommand()
    {
        fileSystemCommand.execute();
    }
}

/**
 * Implementeaza comanda ls
 */
class LSCommand implements Command
{
    /**
     * Controleaza mesajele de eroare
     */
    private boolean shouldPrint;
    /**
     * Referinta catre o comanda grep(daca este cazul)
     */
    private Command grepCommand;
    /**
     * Calea pe care se face ls
     */
    private String path;
    /**
     * Referinta catre nodul pe care se face ls
     */
    private FileSystem directory = null;
    /**
     * true daca ls este recursiva(a primit argumentul -R)
     */
    private boolean isRecursive;

    /**
     * Construieste o comanda ls
     *
     * @param fileSystem    Referinta catre sistemul de fisiere
     * @param path          Calea pe care se face ls
     * @param isRecursive   true daca ls este recursiva, false altfel
     * @param grepCommand   Referinta catre comanda de grep, daca este cazul(poate sa fie null)
     * @param shouldPrint   true daca trebuie afisate mesaje de eroare
     */
    public LSCommand(FileSystem fileSystem, String path, boolean isRecursive, Command grepCommand, boolean shouldPrint)
    {
        this.shouldPrint = shouldPrint;
        this.grepCommand = grepCommand;
        this.path = path;
        this.isRecursive = isRecursive;

        if(!path.equals(""))
        {
            //Daca ls nu este apelata pe directorul curent, converteste calea la o referinta catre un director
            PathTokenizer pathTokenizer = new PathTokenizer(path, fileSystem);
            LinkedList<String> tokensList = pathTokenizer.getTokensQueue();

            if(tokensList != null)
            {
                directory = fileSystem.getReference(tokensList, NodeType.DirectoryNode);
            }
        }
        else
        {
            //Trebuie facut ls pe directorul curent
            directory = FileSystem.getCurrentDirectory();
        }
    }

    /**
     * Executa comanda ls
     */
    public void execute()
    {
        if(directory != null)
        {
            //am gasit directorul pe care trebuie sa facem ls
            if(isRecursive)
            {
                directory.recursiveLS(grepCommand);
            }
            else
            {
                directory.ls(grepCommand);
            }
        }
        else
        {
            if(shouldPrint)
            {
                Main.errorFileWriter.println("ls: " + path + ": No such directory");
            }
        }
    }
}

/**
 * Implementeaza comanda pwd
 */
class PWDCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere, pentru a putea apela pwd
     */
    private FileSystem fileSystem;

    /**
     * Construieste o comanda pwd
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     */
    public PWDCommand(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    /*
     * Executa comanda pwd
     */
    public void execute()
    {
        fileSystem.pwd();
    }
}

/**
 * Implementeaza comanda cd
 */
class CDCommand implements Command
{
    /**
     * Referinta catre nodul ce devine current directory
     */
    private FileSystem directory = null;
    /**
     * Calea catre nod
     */
    private String path;

    /**
     * Construieste o comanda cd
     *
     * @param fileSystem Referinta catre sistemul de fisere
     * @param path Calea ce va deveni directorul curent
     */
    public CDCommand(FileSystem fileSystem, String path)
    {
        this.path = path;

        PathTokenizer pathTokenizer = new PathTokenizer(path, fileSystem);
        LinkedList<String> tokensList  = pathTokenizer.getTokensQueue();

        if(tokensList != null)
        {
            directory = fileSystem.getReference(tokensList, NodeType.DirectoryNode);
        }
    }

    /*
     * Executa comanda cd
     */
    public void execute()
    {
        if(directory != null)
        {
            //Daca directorul exista, seteaza-l drept director curent
            directory.cd();
        }
        else
        {
             Main.errorFileWriter.println("cd: " + path + ": No such directory");
        }
    }
}

/**
 * Implementeaza comanda cp
 */
class CPCommand implements Command
{
    /**
     *  Sursa copierii(fisier/director)
     */
    private FileSystem sourceNode = null;
    /**
     *  Destinatia copierii(director)
     */
    private FileSystem destDirectory = null;
    /**
     * Calea sursa
     */
    private String source;
    /**
     * Numele destinatiei
     */
    private String destFolder;
    /**
     * Numele nodului sursa
     */
    private String sourceName = "";

    /**
     * Construieste o comanda cp
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param source     Calea catre sursa
     * @param destFolder Calea catre destinatie
     */
    public CPCommand(FileSystem fileSystem, String source, String destFolder)
    {
        this.source     = source;
        this.destFolder = destFolder;

        PathTokenizer sourcePathTokenizer    = new PathTokenizer(source, fileSystem);
        LinkedList<String> sourceTokensList  = sourcePathTokenizer.getTokensQueue();

        PathTokenizer destPathTokenizer   = new PathTokenizer(destFolder, fileSystem);
        LinkedList<String> destTokensList = destPathTokenizer.getTokensQueue();

        if(destTokensList != null)
        {
            destDirectory = fileSystem.getReference(destTokensList, NodeType.DirectoryNode);
        }

        if(sourceTokensList != null)
        {
            sourceName = sourceTokensList.getLast(); //numele sursei este ultimul token
            sourceNode = fileSystem.getReference(sourceTokensList, NodeType.AnyNode);
        }
    }

    /*
     * Executa comanda cp
     */
    public void execute()
    {
        if(sourceNode == null)
        {
            Main.errorFileWriter.println("cp: cannot copy " + source + ": No such file or directory");
        }
        else if(destDirectory == null)
        {
            Main.errorFileWriter.println("cp: cannot copy into " + destFolder + ": No such directory");
        }
        else if(destDirectory.find(sourceName))
        {
            Main.errorFileWriter.println("cp: cannot copy " + source + ": Node exists at destination");
        }
        else
        {
            destDirectory.cp(sourceNode);
        }
    }
}

/**
 * Implementeaza comanda mv
 */
class MVCommand implements Command
{
    /**
     * Sursa mutarii(fisier/folder)
     */
    private FileSystem sourceNode = null;
    /**
     * Destinatia mutarii(folder)
     */
    private FileSystem destDirectory = null;
    /**
     * Calea sursa ca sir
     */
    private String source;
    /**
     * Calea destinatie ca sir
     */
    private String destFolder;
    /**
     * Numele nodului sursa
     */
    private String sourceName = "";

    /**
     * Construieste o comanda mv
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param source     Calea catre sursa
     * @param destFolder Calea catre destinatie
     */
    public MVCommand(FileSystem fileSystem, String source, String destFolder)
    {
        this.source     = source;
        this.destFolder = destFolder;

        PathTokenizer sourcePathTokenizer    = new PathTokenizer(source, fileSystem);
        LinkedList<String> sourceTokensList  = sourcePathTokenizer.getTokensQueue();

        PathTokenizer destPathTokenizer    = new PathTokenizer(destFolder, fileSystem);
        LinkedList<String> destTokensList  = destPathTokenizer.getTokensQueue();

        if(destTokensList != null)
        {
            destDirectory = fileSystem.getReference(destTokensList, NodeType.DirectoryNode);
        }

        if(sourceTokensList != null)
        {
            sourceName = sourceTokensList.getLast();
            sourceNode = fileSystem.getReference(sourceTokensList, NodeType.AnyNode);
        }
    }

    /**
     * Executa comanda mv
     */
    public void execute()
    {
        if(sourceNode == null)
        {
            Main.errorFileWriter.println("mv: cannot move " + source + ": No such file or directory");
        }
        else if(destDirectory == null)
        {
            Main.errorFileWriter.println("mv: cannot move into " + destFolder + ": No such directory");
        }
        else if(destDirectory.find(sourceName))
        {
            Main.errorFileWriter.println("mv: cannot move " + source + ": Node exists at destination");
        }
        else
        {
            destDirectory.mv(sourceNode);
        }
    }
}

/**
 * Implementeaza comanda rm
 */
class RMCommand implements Command
{
    /**
     * Activeaza mesajele de eroare
     */
    private boolean shouldPrint;
    /**
     * Nodul pe care se facem rm
     */
    private FileSystem node;
    /**
     * Calea pe care se face rm, ca string
     */
    private String path;

    /**
     * Construieste o comanda rm
     *
     * @param fileSystem  Referinta catre sistemul de fisiere
     * @param path        Calea catre nodul ce trebuie sters
     * @param shouldPrint true daca trebuie afisate mesaje de eroare
     */
    public RMCommand(FileSystem fileSystem, String path, boolean shouldPrint)
    {
        this.shouldPrint = shouldPrint;
        this.path = path;

        PathTokenizer pathTokenizer = new PathTokenizer(path, fileSystem);
        LinkedList<String> tokensList = pathTokenizer.getTokensQueue();

        if(tokensList != null)
        {
            node = fileSystem.getReference(tokensList, NodeType.AnyNode);
        }
    }

    /**
     * Executa comanda rm
     */
    public void execute()
    {
        if(node == null)
        {
            if(shouldPrint)
            {
                Main.errorFileWriter.println("rm: cannot remove " + path + ": No such file or directory");
            }
        }
        else
        {
            node.rm();
        }
    }
}

/**
 * Implementeaza comanda touch
 */
class TouchCommand implements Command
{
    /**
     * Activeaza mesajele de eroare
     */
    private boolean shouldPrint;
    /**
     * Calea catre fisierul ce trebuie creat
     */
    private String originalPath;
    /**
     * Numele fisierul ce trebuie creat
     */
    private String fileName;
    /**
     * Referinta catre directorul in care trebuie creat fisierul
     */
    private FileSystem directory = null;

    /**
     * Construieste o comanda touch
     *
     * @param fileSystem   Referinta catre sistemul de fisere
     * @param filePath     Calea unde trebuie creat fisierul
     * @param shouldPrint  true daca trebuie afisate mesaje de eroare
     */
    public TouchCommand(FileSystem fileSystem, String filePath, boolean shouldPrint)
    {
        this.shouldPrint = shouldPrint;
        originalPath = filePath;

        PathTokenizer pathTokenizer = new PathTokenizer(filePath, fileSystem);
        LinkedList<String> tokensList  = pathTokenizer.getTokensQueue();

        if(tokensList != null)
        {
            fileName = tokensList.removeLast();
            directory = fileSystem.getReference(tokensList, NodeType.DirectoryNode);
        }
    }

    /**
     * Executa comanda touch
     */
    public void execute()
    {
        if(directory != null)
        {
            if(directory.find(fileName)) // verifica daca exista deja un folder/fisier cu acelasi nume
            {
                originalPath = directory.getPath();
                if(originalPath.equals("/"))
                {
                    //Daca pathul este catre root, adauga doar numele fisierului
                    originalPath = originalPath + fileName;
                }
                else
                {
                    //Altfel adauga / urmat de numele fisierului
                    originalPath = originalPath + "/" +  fileName;
                }

                Main.errorFileWriter.println("touch: cannot create file " + originalPath + ": Node exists");
            }
            else
            {
                directory.touch(fileName);
            }
        }
        else
        {
            if(shouldPrint)
            {
                if(originalPath.contains("/"))
                {
                    //Elimina numele fisierului din cale
                    originalPath = originalPath.substring(0, originalPath.lastIndexOf("/"));
                }

                Main.errorFileWriter.println("touch: " + originalPath + ": No such directory");
            }
        }
    }
}

/**
 * Implementeaza comanda mkdir
 */
class MKDirCommand implements Command
{
    /**
     * Activeaza mesajele de eroare
     */
    private boolean shouldPrint;
    /**
     * Calea catre folderul ce trebuie creat
     */
    private String originalPath;
    /**
     * Numele folderului ce trebui creat
     */
    private String directoryName;
    /**
     * Referinta catre folderului in care trebuie creat noul folder
     */
    private FileSystem directory = null;

    /**
     * Construieste o comanda mkdir
     *
     * @param fileSystem  Referinta catre sistemul de fisiere
     * @param folderPath  Calea catre folderul ce trebuie creat
     * @param shouldPrint true daca trebuie afisate mesaje de eroare
     */
    public MKDirCommand(FileSystem fileSystem, String folderPath, boolean shouldPrint)
    {
        this.shouldPrint = shouldPrint;

        originalPath = folderPath;
        PathTokenizer pathTokenizer = new PathTokenizer(folderPath, fileSystem);
        LinkedList<String> tokensList  = pathTokenizer.getTokensQueue();

        if(tokensList != null)
        {
            directoryName = tokensList.removeLast();
            directory = fileSystem.getReference(tokensList, NodeType.DirectoryNode);
        }
    }

    /**
     * Executa comanda mkdir
     */
    public void execute()
    {
        if(directory != null)
        {
            if(directory.find(directoryName))
            {
                originalPath = directory.getPath();

                if(originalPath.equals("/"))
                {
                    //Daca pathul este catre root, adauga doar numele fisierului
                    originalPath = originalPath + directoryName;
                }
                else
                {
                    //Altfel adauga / urmat de numele fisierului
                    originalPath = originalPath + "/" +  directoryName;
                }

                Main.errorFileWriter.println("mkdir: cannot create directory " + originalPath + ": Node exists");
            }
            else
            {
                directory.mkdir(directoryName);
            }
        }
        else
        {
            if(shouldPrint)
            {
                if (originalPath.contains("/"))
                {
                    //Elimina numele fisierului din cale
                    originalPath = originalPath.substring(0, originalPath.lastIndexOf("/"));
                }
                Main.errorFileWriter.println("mkdir: " + originalPath + ": No such directory");
            }
        }
    }
}

/**
 * Implementeaza comanda grep
 */
class GrepCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere
     */
    private FileSystem fileSystem;
    /**
     * Patternul pe care sirurile trebuie sa il respecte
     */
    private String regexPattern;
    /**
     * Sirul ce trebuie verificat
     */
    private String strToCheck;
    /**
     * Rezultatul verificarii sirului
     */
    private boolean returnValue;

    /**
     * Construieste o comanda grep
     *
     * @param fileSystem   Referinta catre sistemul de fisiere
     * @param regexPattern Patternul regex
     */
    public GrepCommand(FileSystem fileSystem, String regexPattern)
    {
        regexPattern = regexPattern.replaceAll("\"", ""); //sterge "" din pattern

        this.fileSystem   = fileSystem;
        this.regexPattern = regexPattern;

    }

    /**
     * Seteaza sirul ce trebuie verificat
     *
     * @param strToCheck sirul de verificat
     */
    public void setStringToCheck(String strToCheck)
    {
        if(strToCheck.contains("/"))
        {
            //Extrage numele nodului in cazul in care o cale este primita drept argument
            strToCheck = strToCheck.substring(strToCheck.lastIndexOf("/"));
            strToCheck = strToCheck.replaceFirst("/", "");
        }

        strToCheck = strToCheck.trim(); //sterge spatiile libere de la inceput/final(daca exista)
        this.strToCheck = strToCheck;
    }

    /**
     * Executa comanda grep
     */
    public void execute()
    {
        //Salveaza valoarea intoarsa
        returnValue = fileSystem.grep(strToCheck, regexPattern);
    }

    /**
     * Rezultatul verificarii sirului cu un pattern
     *
     * @return true, daca sirul respecta patternul
     *         false, altfel
     */
    public boolean getReturn()
    {
        return returnValue;
    }
}

/**
 *  Wrapper peste LSCommand, folosit pentru a executa comenzi ce contin * in path
 */
class LSStarCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere
     */
    private FileSystem fileSystem;
    /**
     * Calea(cu *) primita ca argument
     */
    private String path;
    /**
     * Lista cailor efective obtinute din calea cu *
     */
    private LinkedList<String> actualPaths;

    /**
     * Construieste o comanda de tip ls star
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param path Calea cu *
     */
    public LSStarCommand(FileSystem fileSystem, String path)
    {
        this.path = path;
        this.fileSystem = fileSystem;

        StarPathTokenizer starPathTokenizer = new StarPathTokenizer(path, fileSystem);
        actualPaths = starPathTokenizer.getActualPaths();
    }

    /**
     * Executa o comanda ls cu * in cale
     */
    public void execute()
    {
        if(actualPaths.isEmpty())
        {
            //nu s-a gasit niciun path care sa faca match
            Main.errorFileWriter.println("ls: " + path + ": No such directory");
            return;
        }
        else
        {
            //trebuie sa apelam comanda ls pe fiecare path obtinut, ne trebuie instanta lui CommandFactory
            CommandFactory commandFactory = CommandFactory.getInstance();

            for (String actualPath : actualPaths)
            {
                String commandText = "ls " + actualPath; // comanda este ls + cale
                //createCommand primeste ca argument un scanner, nu sirul ce reprezinta comanda
                //false pentru ca nu vrem sa afisam mesaje de eroare in ls
                Command commandToExecute = commandFactory.createCommand(fileSystem, new Scanner(commandText), false);

                //executa comanda
                CommandInvoker commandInvoker = new CommandInvoker();
                commandInvoker.setCommand(commandToExecute);
                commandInvoker.invokeCommand();
            }
        }
    }
}

/**
 *  Wrapper peste RMCommand, folosit pentru a executa comenzi ce contin * in path
 */
class RMStarCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere
     */
    private FileSystem fileSystem;
    /**
     * Calea(cu *) primita ca argument
     */
    private String path;
    /**
     * Lista cailor efective obtinute din calea cu *
     */
    private LinkedList<String> actualPaths;

    /**
     * Construieste o comanda de tip rm star
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param path Calea cu *
     */
    public RMStarCommand(FileSystem fileSystem, String path)
    {
        this.path = path;
        this.fileSystem = fileSystem;

        StarPathTokenizer starPathTokenizer = new StarPathTokenizer(path, fileSystem);
        actualPaths = starPathTokenizer.getActualPaths();
    }

    /**
     * Executa o comanda rm *
     */
    public void execute()
    {
        CommandFactory commandFactory = CommandFactory.getInstance();

        if(actualPaths.isEmpty())
        {
            Main.errorFileWriter.println("rm: cannot remove " + path + ": No such file or directory");
            return;
        }
        else
        {
            for (String actualPath : actualPaths)
            {
                String commandText = "rm " + actualPath; // comanda este rm + cale
                Command commandToExecute = commandFactory.createCommand(fileSystem, new Scanner(commandText), false);

                //executa comanda folosind un CommandInvoker
                CommandInvoker commandInvoker = new CommandInvoker();
                commandInvoker.setCommand(commandToExecute);
                commandInvoker.invokeCommand();
            }
        }
    }
}

/**
 *  Wrapper peste TouchCommand, folosit pentru a executa comenzi ce contin * in path
 */
class TouchStarCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere
     */
    private FileSystem fileSystem;
    /**
     * Lista cailor efective obtinute din calea cu *
     */
    private LinkedList<String> actualPaths;
    /**
     * Numele fisierului ce trebuie creat
     */
    private String fileName;
    /**
     * Calea cu * unde trebuie creat fisierul
     */
    private String filePath;

    /**
     * Construieste o comanda touch star
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param filePath   Calea cu *
     */
    public TouchStarCommand(FileSystem fileSystem, String filePath)
    {
        this.filePath = filePath;
        this.fileSystem = fileSystem;

        //extrage numele fisierului din cale
        this.fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        filePath = filePath.substring(0, filePath.lastIndexOf("/")); //construieste calea fara numele fisierului

        StarPathTokenizer starPathTokenizer = new StarPathTokenizer(filePath, fileSystem);
        actualPaths = starPathTokenizer.getActualPaths();
    }

    public void execute()
    {
        CommandFactory commandFactory = CommandFactory.getInstance();

        if(actualPaths.isEmpty())
        {
            Main.errorFileWriter.println("touch: " + filePath + ": No such directory");
            return;
        }
        else
        {
            for (String actualPath : actualPaths)
            {
                String commandText = "touch " + actualPath + "/" + fileName; //comanda este touch cale/nume_fisier
                Command commandToExecute = commandFactory.createCommand(fileSystem, new Scanner(commandText), false);

                //Executa comanda
                CommandInvoker commandInvoker = new CommandInvoker();
                commandInvoker.setCommand(commandToExecute);
                commandInvoker.invokeCommand();
            }
        }
    }
}

/**
 *  Wrapper peste MKDirCommand, folosit pentru a executa comenzi ce contin * in path
 */
class MKDirStarCommand implements Command
{
    /**
     * Referinta catre sistemul de fisiere
     */
    private FileSystem fileSystem;
    /**
     * Lista cailor efective obtinute din calea cu *
     */
    private LinkedList<String> actualPaths;
    /**
     * Numele folderului ce trebuie creat
     */
    private String directoryName;
    /**
     * Calea cu *
     */
    private String folderPath;

    /**
     * Construieste o comanda mkdir star
     *
     * @param fileSystem Referinta catre sistemul de fisiere
     * @param folderPath Calea cu *
     */
    public MKDirStarCommand(FileSystem fileSystem, String folderPath)
    {
        this.fileSystem = fileSystem;
        this.folderPath = folderPath;

        directoryName = folderPath.substring(folderPath.lastIndexOf("/") + 1); //extrage numele folderului
        folderPath = folderPath.substring(0, folderPath.lastIndexOf("/")); // construieste calea fara numele folderului

        StarPathTokenizer starPathTokenizer = new StarPathTokenizer(folderPath, fileSystem);
        actualPaths = starPathTokenizer.getActualPaths();
    }

    public void execute()
    {
        CommandFactory commandFactory = CommandFactory.getInstance();

        if(actualPaths.isEmpty())
        {
            Main.errorFileWriter.println("mkdir: " + folderPath + ": No such directory");
            return;
        }

        for(String actualPath : actualPaths)
        {
            String commandText = "mkdir " + actualPath + "/" + directoryName; // comanda este mkdir cale/nume_folder
            Command commandToExecute = commandFactory.createCommand(fileSystem, new Scanner(commandText), false);

            //executa comanda
            CommandInvoker commandInvoker = new CommandInvoker();
            commandInvoker.setCommand(commandToExecute);
            commandInvoker.invokeCommand();
        }
    }
}
