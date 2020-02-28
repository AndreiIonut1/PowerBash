import java.io.*;
import java.util.*;

public class Main
{
    /**
     * Obiect de tipul PrintWriter folosit pentru a scrie fisierul de output
     */
    public static PrintWriter outputFileWriter;
    /**
     * Obiect de tipul PrintWriter folosit pentru a scrie fisierul de erori
     */
    public static PrintWriter errorFileWriter;
    /**
     * Indexul comenzii curente
     */
    public static int currentCommandIndex = 0;

    /**
     * Sterge spatiile de la finalul liniilor dintr-un fisier
     *
     * @param fileToFix numele fisierul
     */
    public static void fixLineEndings(String fileToFix)
    {
        try
        {
            //creeaza un fisier temporar
            outputFileWriter = new PrintWriter("tempfile.txt");
        }
        catch(FileNotFoundException ex)
        {
            return;
        }

        //deschide fisierul de output
        File outputFile = new File(fileToFix);
        Scanner fileScanner;
        try
        {
            fileScanner = new Scanner(outputFile);
        }
        catch(FileNotFoundException e)
        {
            return;
        }

        while(fileScanner.hasNextLine())
        {
            //ia fiecare linie din fisierul de output, aplica trim si o scrie in fisierul temporar
            String line = fileScanner.nextLine();

            line = line.trim();
            outputFileWriter.println(line);
        }

        fileScanner.close();
        outputFileWriter.close();

        //sterge fisierul original de output
        outputFile.delete();

        //redenumeste fisierul temporar in fisierul de output
        File file = new File("tempfile.txt");
        file.renameTo(new File(fileToFix));

    }

    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            return;
        }

        /*
            root este radacina sistemului de fisiere
            dummyNode este un nod folosit pe post de "santinela", astfel
            putem sa lucram cu root la fel cum lucram cu orice alt nod
         */
        FileSystemDirectory dummyNode = new FileSystemDirectory("/");
        FileSystemDirectory root = new FileSystemDirectory("/");
        dummyNode.add(root);
        root.setNodeParent(null); // root nu are parinte
        FileSystem.setCurrentDirectory(root); //root este directorul curent by default

        File inputFile = new File(args[0]);

        try
        {
            outputFileWriter = new PrintWriter(args[1]);
            errorFileWriter  = new PrintWriter(args[2]);
        }
        catch(FileNotFoundException ex)
        {
            return;
        }

        Scanner fileScanner;
        try
        {
            fileScanner = new Scanner(inputFile);
        }
        catch(FileNotFoundException e)
        {
            return;
        }

        while(fileScanner.hasNextLine())
        {
            currentCommandIndex++;
            Main.errorFileWriter.println(Main.currentCommandIndex);
            Main.outputFileWriter.println(Main.currentCommandIndex);

            String line = fileScanner.nextLine();
            Scanner stringScanner = new Scanner(line);

            CommandFactory commandFactory = CommandFactory.getInstance();
            Command commandToExecute = null;

            //Verifica daca este o comanda normala(fara * in path - cu exceptia regex-urilor)
            if(line.contains("*") && line.contains("grep") || !line.contains("*"))
            {
                commandToExecute = commandFactory.createCommand(dummyNode, stringScanner, true);
            }
            else
            {
                commandToExecute = commandFactory.createStarCommand(dummyNode, stringScanner);
            }

            CommandInvoker commandInvoker = new CommandInvoker();
            commandInvoker.setCommand(commandToExecute); //seteaza comanda ce trebuie apelata
            commandInvoker.invokeCommand();
        }

        outputFileWriter.close();
        errorFileWriter.close();

        //Sterge spatiile de la finalul liniilor din fisierul de output
        fixLineEndings(args[1]);
    }
}
