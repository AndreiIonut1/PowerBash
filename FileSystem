import java.util.LinkedList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Tipul de nod cautat(folder/fisier/ambele)
 */
enum NodeType
{
    DirectoryNode,
    FileNode,
    AnyNode
}

/**
 * Clasa abstracta ce defineste comportamentul pe care fisierele si folderele trebuie sa il respecte
 * Contine implementari efective ale functiilor care au acelasi comportament pentru ambele tipuri de noduri
 * Defineste implementari default pentru functiile care au comportamente diferite pentru fisiere/foldere
 */
public abstract class FileSystem implements Comparable, Cloneable
{
    /**
     * Parintele unui nod
     */
    private FileSystem parent;
    /**
     * Directorul curent
     */
    private static FileSystem currentDirectory;

    /**
     * Intoarce numele fisierului/folderului
     *
     * @return numele ca string
     */
    public abstract String getName();

    /**
     * Construieste un iterator. Permite clientului sa trateze atat folderele cat si fisierele identic
     *
     * @return Iterator peste sistemul de fisiere
     */
    public abstract Iterator<FileSystem> createIterator();

    /**
     * Verifica daca un nod cu un anumit nume exista
     *
     * @param fileName numele nodului
     * @return true, daca un nod exista
     *         false, altfel
     */
    public abstract boolean find(String fileName);

    /**
     * Seteaza parintele unui nod
     *
     * @param parent parintele nodului curent
     */
    public void setNodeParent(FileSystem parent)
    {
        this.parent = parent;
    }

    /**
     * Intoarce parintele unui nod
     *
     * @return referinta catre parintele nodului
     */
    public FileSystem getNodeParent()
    {
        return parent;
    }

    /**
     * Verifica daca un sir respecta un pattern
     * Comanda grep are comportament identic atat pentru fisiere cat si pentru foldere
     *
     * @param strToCheck   sirul ce trebuie verificat
     * @param regexPattern patternul ce trebuie respectat
     *
     * @return true, daca sirul respecta patternul
     *         false, altfel
     */
    public boolean grep(String strToCheck, String regexPattern)
    {
        if(strToCheck.matches(regexPattern))
        {
            return true;
        }

        return false;
    }

    /**
     * Intoarce o referinta catre un nod, primit calea acestuia drept argument
     *
     * @param tokensList lista de tokenuri ce reprezinta calea catre nod
     * @param nodeType tipul de nod cautat(NodeType enum)
     *
     * @return referinta catre nod, daca acesta exista
     *         null, altfel
     */
    public FileSystem getReference(LinkedList<String> tokensList, NodeType nodeType)
    {
        //Construieste un iterator
        Iterator<FileSystem> iterator = createIterator();

        while (iterator.hasNext())
        {
            FileSystem node = iterator.next(); //ia nodul la care puncteaza iteratorul

            //Decide ce tip de nod cautam
            boolean check = false;
            if(nodeType == NodeType.DirectoryNode)
            {
                check = node instanceof FileSystemDirectory;
            }
            if(nodeType == NodeType.FileNode)
            {
                check = node instanceof FileSystemFile;
            }
            if(nodeType == NodeType.AnyNode)
            {
                //Daca ne intereseaza orice nod, check este true
                check = true;
            }

            //nodul are tipul corent si mai exista tokenuri
            if(check && !tokensList.isEmpty())
            {
                if(node.getName().equals(tokensList.element()))
                {
                    //numele nodului corespunde cu tokenul curent
                    tokensList.remove(); //stergem tokenul

                    if(tokensList.isEmpty())
                    {
                        //Daca lista a devenit goala, inseamna ca am gasit nodul
                        return node;
                    }

                    //Apeleaza recursiv getReference pentru a verifica urmatorul token
                    return node.getReference(tokensList, nodeType);
                }
            }
        }

        //Nu am reusit sa gasim un nod la calea data ca argument
        return null;
    }

    /**
     * Intoarce calea catre nod
     *
     * @return calea drept string
     */
    public String getPath()
    {
        /*
         * Algoritmul pentru a gasit calea functioneaza astfel:
         * se urca in ierarhia de noduri folosind mereu parintele nodului curent
         * se adauga numele parintelui intr-o stiva
         * se repeta procedura pana cand am ajuns la root(parintele lui este null)
         */
        Stack<String> pathStack = new Stack<>();

        FileSystem parent = this;
        StringBuilder fullPath = new StringBuilder();

        if(!parent.getName().equals("/"))
        {
            do
            {
                pathStack.push(parent.getName());
                parent = parent.getNodeParent();
            }
            while (parent != null);

            //Acum ca avem toate tokenurile, construieste un sir ce reprezinta calea catre nod
            while (!pathStack.isEmpty())
            {
                String token = pathStack.pop();

                if(!token.equals("/"))
                {
                    fullPath.append(token + "/");
                }
                else
                {
                    //Daca am ajuns la root, nu mai adaugam / pentru ca am obtine // in loc de /
                    fullPath.append(token);
                }
            }
        }
        else
        {
            fullPath = new StringBuilder("/");
        }

        String str = fullPath.toString();
        if(str.length() > 1 && str.charAt(str.length() - 1) == '/')
        {
            //sterge / de la finalul pathului
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    /**
     * Intoarce directorul curent
     *
     * @return referinta catre directorul curent
     */
    public static FileSystem getCurrentDirectory()
    {
        return currentDirectory;
    }

    /**
     * Seteaza directorul curent
     *
     * @param directory directorul ce trebuie sa devina director curent
     */
    public static void setCurrentDirectory(FileSystem directory)
    {
        currentDirectory = directory;
    }

    /**
     * Implementare default a lui ls
     * ls este implementat de FileSystemDirectory
     * FileSystemFile nu implementeaza ls si foloseste implementarea default
     *
     * @param grepCommand referinta catre o comanda grep
     */
    public void ls(Command grepCommand)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui recursive ls
     * recursive ls ls este implementat de FileSystemDirectory
     * FileSystemFile nu implementeaza recursive ls si foloseste implementarea default
     *
     * @param grepCommand referinta catre o comanda grep
     */
    public void recursiveLS(Command grepCommand)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui pwd
     */
    void pwd()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui cd
     * cd nu are sens pe un fisier, deci FileSystemFile foloseste implementarea default
     */
    public void cd()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui cp
     *
     * @param sourceNode sursa ce trebuie copiata
     */
    public void cp(FileSystem sourceNode)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui mv
     *
     * @param sourceNode sursa ce trebuie mutata
     */
    public void mv(FileSystem sourceNode)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare efectiva a lui rm
     * rm are sens si pe fisiere si pe foldere
     */
    public void rm()
    {
        boolean containsWorkingDirectory = false;

        //Verifica daca incercam sa stergem un nod in al carui subarbore se afla directorul curent
        if(FileSystem.getCurrentDirectory().getPath().contains(this.getPath()))
        {
            containsWorkingDirectory = true;
        }

        if(!containsWorkingDirectory)
        {
            //Sterge nodul curent din lista de noduri a parintelui
            this.getNodeParent().remove(this);
        }
    }

    /**
     * Implementare default a lui touch
     *
     * @param fileName numele fisierului ce trebuie creat
     */
    public void touch(String fileName)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui mkdir
     *
     * @param folderName numele folderului ce trebuie creat
     */
    public void mkdir(String folderName)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui add
     * Adauga un nou nod in lista de noduri a nodului curent
     *
     * @param node nodul ce trebuie adaugat
     */
    public void add(FileSystem node)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementare default a lui remove
     * Sterge un nod din lista de noduri a nodului curent
     *
     * @param node nodul ce trebuie sters
     */
    public void remove (FileSystem node)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Suprascrie metoda compareTo
     *
     * @param o obiectul cu care comparam
     * @return  aceleasi valoari intoarse de metoda compareTo folosinda pe String
     */
    public int compareTo(Object o)
    {
        FileSystem other = (FileSystem)o;
        return this.getName().compareTo(other.getName()); //comparatia se face dupa nume
    }

    /**
     * Implementeaza interfata Cloneable
     * Un folder/fisier se cloneaza in mod diferit + ne intereseaza sa facem un deep copy, nu shallow copy,
     * altfel vom copia referinte
     *
     * @return clona nodului curent
     * @throws CloneNotSupportedException in cazul in care obiectul nu poate fi clonat
     */
    public Object clone() throws CloneNotSupportedException
    {
       return this.clone();
    }
}
