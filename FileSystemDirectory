import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Queue;

/**
 * Implementeaza nodurile de tip folder
 */
public class FileSystemDirectory extends FileSystem
{
    /**
     * Set continand numele nodurile continute in folderul curent, in ordine lexicografica
     */
    TreeSet<FileSystem> fileSystemTree;
    /**
     * Numele folderului
     */
    private String directoryName;

    /**
     * Construieste un nou obiect de tip FileSystemDirectory
     *
     * @param directoryName numele directorului
     */
    public FileSystemDirectory(String directoryName)
    {
        fileSystemTree = new TreeSet<>();
        this.directoryName = directoryName;
    }


    /**
     * Intoarce un iterator peste continutul folderului
     *
     * @return iterator
     */
    public Iterator<FileSystem> createIterator()
    {
        return fileSystemTree.iterator();
    }

    /**
     * Cloneaza un director si recursiv realizeaza o clona a tuturor nodurilor din directorul curent
     *
     * @return un no director cu acelasi nume ca directorul curent
     * @throws CloneNotSupportedException in cazul in care obiectul nu poate sa fie clonat
     */
    public Object clone() throws CloneNotSupportedException
    {
        FileSystemDirectory newDirectory = new FileSystemDirectory(this.getName());

        for(FileSystem file : fileSystemTree)
        {
            //Adauga un noul director clonele copiilor lui
            newDirectory.add((FileSystem)file.clone());
        }

        return newDirectory;
    }

    /**
     * Adauga un nou nod in multimea de noduri ale nodului curent
     *
     * @param node nodul ce trebuie adaugat
     */
    public void add(FileSystem node)
    {
        node.setNodeParent(this); //seteaza parintele nodului ce trebuie adaugat
        fileSystemTree.add(node);
    }

    /**
     * Sterge un nod din multimea de noduri ale nodului curent
     * @param node nodul ce trebuie sters
     */
    public void remove(FileSystem node)
    {
        fileSystemTree.remove(node);
    }

    /**
     * Intoarce numele folderului
     *
     * @return numele ca string
     */
    public String getName()
    {
        return directoryName;
    }

    /**
     * Verifica daca in folder se afla un nod cu un anumit nume
     *
     * @param name numele cu care se face comparatia
     * @return true, daca un nod cu numele name exista
     *         false, altfel
     */
    public boolean find(String name)
    {
        Iterator<FileSystem> iterator = createIterator();

        while (iterator.hasNext())
        {
            FileSystem node = iterator.next();
            if(node.getName().equals(name)) //compara numele nodului cu numele de referinta
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Implementarea efectiva comenzii ls - listeaza continutul unui director
     *
     * @param grepCommand referinta catre o comanda grep
     */
    public void ls(Command grepCommand)
    {
        Main.outputFileWriter.println(getPath() + ":");

        Iterator<FileSystem> iter = createIterator();

        while(iter.hasNext())
        {
            boolean printData = true;
            FileSystem node = iter.next();

            if(grepCommand != null)
            {
                /*
                    Daca am primit si o comanda grep, trebuie sa verificam daca numele nodului respecta patternul
                    inainte sa il afisam
                    Apelam comanda grep si verificam valoarea intoarsa

                 */
                GrepCommand grep = (GrepCommand)grepCommand; //trebuie sa facem cast de la Command la GrepCommand
                grep.setStringToCheck(node.getPath());

                CommandInvoker commandInvoker = new CommandInvoker();
                commandInvoker.setCommand(grep);
                commandInvoker.invokeCommand();

                printData = grep.getReturn();
            }

            if(printData)
            {
                Main.outputFileWriter.print(node.getPath() + " ");
            }
        }

        Main.outputFileWriter.println();
        Main.outputFileWriter.println();
    }

    /**
     * Implementeaza parcurgerea depth first a unui director
     *
     * @param node         directorul pe care il parcurgem
     * @param visited      stiva ce contine nodurile vizitate deja
     * @param orderedNodes coada ce contine nodurile in ordinea in care trebuie afisate
     */
    private void depthFirstSearch(FileSystem node, Stack<FileSystem> visited, Queue<FileSystem> orderedNodes)
    {
        //Daca nodul este un folder, il adaugam in coada de noduri ce trebuie afisate
        if(node instanceof FileSystemDirectory)
        {
            orderedNodes.add(node);
        }

        Iterator<FileSystem> iter = node.createIterator();

        while(iter.hasNext())
        {
            FileSystem nodeToCheck = iter.next();

            if(!visited.contains(nodeToCheck)) //daca nodul nu a mai fost vizitat
            {
                visited.push(nodeToCheck); //il marcheaza ca vizitat
                depthFirstSearch(nodeToCheck, visited, orderedNodes); //apeleaza depthFirstSearch pe noul nod
            }
        }
    }

    /**
     * Implementare efectiva o comenzii ls -R - listeaza recursiv continutul unui director
     *
     * @param grepCommand referinta catre o comanda grep
     */
    public void recursiveLS(Command grepCommand)
    {
        Stack<FileSystem> visitedNodesStack = new Stack<>();
        Queue<FileSystem> orderedNodes = new LinkedList<>();

        //Construieste o coada cu nodurile ce trebuie afisate in ordinea corecta
        depthFirstSearch(this, visitedNodesStack, orderedNodes);

        while(!orderedNodes.isEmpty())
        {
            //Ia cate un nod din coada si apeleaza comanda ls pe el
            FileSystem node = orderedNodes.remove();
            node.ls(grepCommand);
        }
    }

    /**
     * Implementare efectiva a comenzii pwd - scrie directorul curent
     */
    public void pwd()
    {
        FileSystem currentDirectory = FileSystem.getCurrentDirectory(); //ia directorul curent
        Main.outputFileWriter.println(currentDirectory.getPath()); //il scrie in fisierul de output
    }

    /**
     * Implementare efectiva a comenzii cd - seteaza directorul curent
     */
    public void cd()
    {
        //nodul this trebuie sa devina current directory
        FileSystem.setCurrentDirectory(this);
    }

    /**
     * Implementare efectiva a comenzii cp - copiaza un nod
     *
     * @param sourceNode sursa ce trebuie copiata
     */
    public void cp(FileSystem sourceNode)
    {
        //Incearca sa adaugi in nodul curent o clona a nodului de copiat
        try
        {
            this.add((FileSystem)sourceNode.clone());
        }
        catch(CloneNotSupportedException ex)
        {
            return;
        }
    }

    /**
     * Implementare efectiva a comenzii mv - muta un nod
     *
     * @param sourceNode sursa ce trebuie mutata
     */
    public void mv(FileSystem sourceNode)
    {
        FileSystem sourceParent = sourceNode.getNodeParent();

        //Verifica daca se incearca mutarea unui subarbore ce contine directorul curent
        boolean updateCurrentDirectory = false;
        if(FileSystem.getCurrentDirectory().getPath().contains(sourceNode.getPath()))
        {
            updateCurrentDirectory = true;
        }

        //Sterge sursa din lista de noduri a vechiului parinte
        sourceParent.remove(sourceNode);

        FileSystem directory;
        try
        {
            //Insereaza in nodul curent o clona a sursei
            directory = (FileSystem)sourceNode.clone();
            this.add(directory);
        }
        catch(CloneNotSupportedException ex)
        {
            return;
        }

        //Actualizeaza directorul curent
        if(updateCurrentDirectory)
        {
            FileSystem.setCurrentDirectory(directory);
        }
    }

    /**
     * Implementare efectiva a comenzii touch - construieste un nou fisier
     *
     * @param fileName numele fisierului ce trebuie creat
     */
    public void touch(String fileName)
    {
        add(new FileSystemFile(fileName));
    }

    /**
     * Implementare efectiva a comenzii mkdir - construieste un nou folder
     *
     * @param folderName numele folderului ce trebuie creat
     */
    public void mkdir(String folderName)
    {
        add(new FileSystemDirectory(folderName));
    }
}
