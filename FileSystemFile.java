import java.util.Iterator;

/**
 * Implementeaza un iterator null. Iteratorul null este iteratorul
 * care nu contine niciun element si metoda hasNext va intoarce mereu false
 * Ne permite sa tratam atat fisierele cat si folderele in mod identic
 * Pe un folder vom avea un iterator valid, care puncteaza la noduri existente
 * Pe un fisier vom avea un iteratorul null.
 */
class NullIterator implements Iterator<FileSystem>
{
    /**
     * Nodul urmatorul la care puncteaza iteratorul Null
     *
     * @return null, deoarece iteratorul Null nu puncteaza la niciun nod
     */
    public FileSystem next()
    {
        return null;
    }

    /**
     * Verifica daca mai exista noduri la care puncteaza iteratorul Null
     *
     * @return false, iteratorul Null nu puncteaza la niciun nod
     */
    public boolean hasNext()
    {
        return false;
    }
}

/**
 * Implementeaza nodurile de tip fisier
 */
public class FileSystemFile extends FileSystem
{
    /**
     * Numele fisierului
     */
    private String fileName;

    /**
     * Construieste un obiect de tip FileSystemFile
     *
     * @param fileName numele fisierului
     */
    public FileSystemFile(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Intoarce numele fisierului
     *
     * @return numele fisierului ca string
     */
    public String getName()
    {
        return fileName;
    }

    /**
     * Construieste un iterator peste un fisier
     *
     * @return o instanta a lui NullIterator
     */
    public Iterator<FileSystem> createIterator()
    {
        return new NullIterator();
    }

    /**
     * Compara numele fisierului curent cu numele primit ca argument
     *
     * @param fileName numele nodului
     * @return true, daca numele coincid
     *         false, altfel
     */
   public boolean find(String fileName)
   {
       if(this.fileName.equals(fileName))
       {
           return true;
       }

       return false;
   }

    /**
     * Cloneaza un fisier
     *
     * @return un nou fisier cu acelasi nume ca fisierul curent
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        FileSystemFile newFile = new FileSystemFile(this.getName());
        return newFile;
    }
}
