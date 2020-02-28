import java.util.LinkedList;

/**
 * Imparte un path in token-uri dupa caracterul /
 */
public class PathTokenizer
{
    /**
     * Lista in care sunt salvate tokenurile
     */
    LinkedList<String> pathTokens;

    /**
     * Construieste un obiect de tip PathTokenizer
     *
     * @param path          pathul ce trebuie impartit in tokenuri
     * @param fileSystem    o referinta catre sistemul de fisiere
     */
    public PathTokenizer(String path, FileSystem fileSystem)
    {
        pathTokens  = new LinkedList<>();

        if(path.charAt(0) != '/')
        {
            //Daca pathul nu incepe cu / inseamna ca este o adresa relativa
            StringBuilder fullPath = new StringBuilder(FileSystem.getCurrentDirectory().getPath());
            if(!fullPath.toString().equals("/"))
            {
                //Daca pathul absolut nu duce catre root, adaugam "/" manual
                fullPath.append("/");
            }

            fullPath.append(path);
            path = fullPath.toString();
        }

        //split o sa ignore "/"(root), adaugam root manual
        //in acest punct calea este absoluta
        pathTokens.add("/");

        for (String token: path.split("/"))
        {
            if(token.isEmpty())
            {
                //ignoram tokenurile goale
                continue;
            }

            if(token.equals("."))
            {
                //ignoram si . deoarece . = directorul curent
                continue;
            }

            if(token.equals(".."))
            {
                /*
                    .. inseamna directorul anterior
                    Apelam getReference pentru a gasi adresa directorului cu pathul dat de pathTokens
                 */
                FileSystem dir = fileSystem.getReference(pathTokens, NodeType.DirectoryNode);

                if(dir != null)
                {
                    //directorul exista, verificam daca nu este /
                    if(!dir.getPath().equals("/"))
                    {
                        //Gasim parintele directorului
                        dir = dir.getNodeParent();

                        //Reimpartim calea in tokenuri si inlocuim vechea lista de tokenuri
                        PathTokenizer tokenizer = new PathTokenizer(dir.getPath(), fileSystem);
                        pathTokens = tokenizer.getTokensQueue();
                    }
                    else
                    {
                        //In cazul in care nu am gasit pathul, pathTokens devine null
                        pathTokens = null;
                        return;
                    }
                }
            }
            else
            {
                //E un token normal(nu ./..), il adaugam direct
                pathTokens.add(token);
            }
        }
    }

    /**
     * Intoarce tokenurile obtinute din calea primita in constructor
     *
     * @return tokenurile ca o lista inlantuita
     */
    public LinkedList<String> getTokensQueue()
    {
        return pathTokens;
    }
}
