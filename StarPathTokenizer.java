import java.util.LinkedList;
import java.util.Iterator;

/**
 * Imparte un path care contine * in tokenuri dupa caracterul /
 */
public class StarPathTokenizer
{
    /**
     * Lista de pathuri gasita dupa ce toate * au fost inlocuite
     */
    private LinkedList<String> actualPaths;

    public StarPathTokenizer(String path, FileSystem fileSystem)
    {
        actualPaths = new LinkedList<>();

        //prima data imparte pathul in tokenuri(inclusiv *) folosind PathTokenizer
        PathTokenizer pathTokenizer = new PathTokenizer(path, fileSystem);
        LinkedList<String> tokensQueue = pathTokenizer.getTokensQueue();

        //Construieste toate caile posibile prin inlocuirea tuturor *
        buildRealPaths(tokensQueue, fileSystem);

        for(int i = 0; i < actualPaths.size(); i++)
        {
            //Verifica pathurile obtinute si le pastreaza doar pe cele care chiar exista
            String potentialPath = actualPaths.get(i);
            PathTokenizer tokenizer = new PathTokenizer(potentialPath, fileSystem);
            LinkedList<String> queue = tokenizer.getTokensQueue();

            FileSystem node = fileSystem.getReference(queue, NodeType.AnyNode);
            if(node == null)
            {
                //calea nu duce catre un nod valid, trebuie stearsa
                actualPaths.remove(potentialPath);
                i = i - 1;
            }
        }
    }

    void buildRealPaths(LinkedList<String> tokensQueue, FileSystem fileSystem)
    {
        //recursivitatea se opreste cand lista de tokenuri este goala
        if(tokensQueue.isEmpty())
        {
            return;
        }

        //ia tokenul curent si il sterge din lista
        String token = tokensQueue.remove();

        if(!token.contains("*")) //tokenul nu este *
        {
            //construieste calea din tokenurile adaugate pana acum

            int i = 0;
            int size = actualPaths.size();

            if(size == 0)
            {
                //daca lista de pathuri este goala, adauga tokenul direct
                actualPaths.add(token);
            }
            else
            {
                while (i < size)
                {
                    String path = actualPaths.remove(); //ia pathul format pana in momentul curent
                    StringBuilder builder = new StringBuilder(path);
                    builder.append(token + "/"); //adauga tokenul si /
                    actualPaths.addLast(builder.toString()); //adauga la noul path la final
                    i++;
                }
            }
        }
        else
        {
            int i = 0;
            int size = actualPaths.size();

            String regexPattern = "";

            /*
                Daca tokenul nu este doar * si contine si text,
                construieste un regex care sa faca match pe el
             */
            if(!token.equals("*"))
            {
                if(token.charAt(0) == '*') //daca tokenul incepe cu *, de exemplu *file
                {
                    token = token.replace("*", "");
                    regexPattern = ".*" + token + "$";
                }
                else if(token.charAt(token.length() - 1) == '*') //daca tokenul se termina cu * - file *
                {
                    token = token.replace("*", "");
                    regexPattern = "^" + token + ".*";
                }
                else
                {
                    //* este undeva in interiorul sirului
                    String[] parts = token.split("\\*"); //imparte in 2 subsiruri dupa *
                    regexPattern = "^" + parts[0] + ".*" + parts[1] + "$";
                }
            }

            while (i < size)
            {
                String path = actualPaths.remove(); //ia pathul curent

                PathTokenizer pathTokenizer = new PathTokenizer(path, fileSystem);
                LinkedList<String> currentPathTokens = pathTokenizer.getTokensQueue();

                //Gaseste directorul de la adresa construita pana in acel moment(fara *)
                FileSystem dir = fileSystem.getReference(currentPathTokens, NodeType.DirectoryNode);

                if (dir != null)
                {
                    //Daca directorul exista, verifica fiecare nod din el
                    Iterator<FileSystem> iter = dir.createIterator();
                    while (iter.hasNext())
                    {
                        FileSystem node = iter.next();

                        boolean shouldAdd = true; // trebuie adaugat nodul?
                        if(!regexPattern.isEmpty() && !node.getName().matches(regexPattern))
                        {
                            /*
                                Daca expresia regulata nu este goala, tokenul nu a fost o simpla *
                                Verifica daca numele nodului corespunde cu patternul construit
                             */

                            shouldAdd = false;
                        }

                        if(shouldAdd)
                        {
                            //Construieste calea adaugand numele nodului si o adauga la lista de cai
                            StringBuilder fullPath = new StringBuilder(path);
                            fullPath.append(node.getName() + "/");

                            actualPaths.addLast(fullPath.toString());
                        }
                    }
                }

                i++;
            }
        }

        //Apeleaza recursiv buildRealPaths pentru a construit toate caile posibile
        buildRealPaths(tokensQueue, fileSystem);
    }

    /**
     * Intoarce lista de cai posibile
     *
     * @return caile construite ca lista inlantuita
     */
    public LinkedList<String> getActualPaths()
    {
        return actualPaths;
    }
}
