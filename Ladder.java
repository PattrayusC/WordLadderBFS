import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.*;
import java.io.*;
import org.jgrapht.alg.shortestpath.*;

class WL {
    public class sameWord extends Exception {
        public sameWord(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public class NotInDataBase extends Exception {
        public NotInDataBase(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public class NoConnection extends Exception {
        public NoConnection(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private Graph<String, DefaultEdge> G;
    private Map<String, ArrayList<String>> umap;
    
    private reads rd;
    
    public WL() {
        G = new SimpleGraph<>(DefaultEdge.class);
        umap = new HashMap<>();
        rd = new reads();
        //umap is whole graph
        for(String obj : rd.getD()) {
            for(int i=0; i<obj.length(); i++) {
                String str = obj.substring(0, i) + "*" + obj.substring(i + 1);
                //get a neightbor nodes that already exist in the graph from previus iteration
                ArrayList<String> s = umap.get(str);
                if(s == null) {
                    s = new ArrayList<>();
                }
                //adding a new neightbor node to s and the put it back into hashmap
                s.add(obj);
                umap.put(str, s);
            }
        }
        
        Graphs.addAllVertices(G, rd.getD());
        for(Map.Entry<String, ArrayList<String>> entry : umap.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                String parent = entry.getValue().get(i);
                for(int j = 0; j < entry.getValue().size(); j++) {
                    String neighbor = entry.getValue().get(j);
                    if(parent.equals(neighbor)) {
                        continue;
                    }
                    G.addEdge(parent, neighbor);
                }
            }
        }
        
        do {
            char c = rd.FS();
            if(c == 'f') {
                shortestChainLen();
            }
            else if(c == 's') {
                rd.search();
            }
        }
        while(rd.YN());
    }
    
    public int shortestChainLen() {
        Scanner input = new Scanner(System.in);
        String start = null, target = null;
        
        System.out.println("\nEnter 5 letter word 1 =");
        start = rd.userInput();
        System.out.println("\nEnter 5 letter word 2 =");
        target = rd.userInput();
        
        boolean NewStartNode = false;
        try {
            if(start.equals(target)) {
                throw new sameWord(start + " is already equal to " + target);
            }
            if(!G.containsVertex(target)) {
                throw new NotInDataBase("transform target doesn't exist in table : " + target);
            }
            if(!G.containsVertex(start)) {
                G.addVertex(start);
                boolean connection = false;
                for(int i=0; i<start.length(); i++) {
                    String str = start.substring(0, i) + "*" + start.substring(i + 1);
                    ArrayList<String> s = umap.get(str);
                    if(s != null) {
                        connection = true;
                        NewStartNode = true;
                        for(String obj : s) {
                            G.addEdge(start, obj);
                        }
                    }
                }
                if(!connection) {
                    throw new NoConnection("trying to add unintialize node : " + start + " but doesn't have connection to any node. can't transform ");
                }
            }
        }
        catch(sameWord | NotInDataBase | NoConnection e) {
            System.out.println(e.getMessage());
            return 0;
        }
        
        BFSShortestPath<String, DefaultEdge> shpath = new BFSShortestPath<>(G);
        if (shpath.getPath(start, target) != null) {
            List<String> allNodes = shpath.getPath(start, target).getVertexList();
            System.out.println("\n" + allNodes.get(0));
            int totalcost = 0;
            for(int i = 0; i < allNodes.size()-1; i++) {
                String parent = allNodes.get(i);
                String neighbor = allNodes.get(i+1);
                int cost = Math.abs((parent).compareTo(neighbor));
                totalcost += cost;
                System.out.print(neighbor + "  (+" + cost + ")\n");
            }
            System.out.println("\nTotal cost = " + totalcost);
        }
        else {
            System.out.printf("Cannot transform %s into %s \n", start, target);
        }
        
        if(NewStartNode) {
            G.removeVertex(start);
        }
        
        return 0;
    }
}

class reads {
    public class WordNotFound extends Exception {
        public WordNotFound(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public class OnlyFS extends Exception {
        public OnlyFS(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public class OnlyYN extends Exception {
        public OnlyYN(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public class invalidWordSize extends Exception {
        public invalidWordSize(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private Scanner input = new Scanner(System.in);
    private HashSet<String> D = new HashSet<>();
    
    public reads() {
        boolean loop = true;
        while(loop) {
            try {
                System.out.println("Enter data file =");
                String in = input.next();
                Scanner scan = new Scanner(new File(in));
                while(scan.hasNext()) {
                    String line = scan.nextLine();
                    D.add(line);
                }
                loop = false;
            }
            catch(FileNotFoundException e) {
                System.out.println("File not found");
            }
        }
    }
    
    public char FS() {
        System.out.println("\nSearch words or find the shortest path (\"s\" for search words, \"f\" for find the shortest path)");
        
        boolean loop = true;
        while(loop) {
            try {
                String choose = input.next();
                choose = choose.toLowerCase();
                switch(choose) {
                    case "f" :
                        return 'f';
                    case "s" :
                        return 's';
                    default :
                        throw new OnlyFS("Insert only \"f\" or \"s\"");
                }
            }
            catch(OnlyFS e) {
                System.out.println(e.getMessage());
            }
        }
        
        return 0;
    }
    
    public boolean YN() {
        System.out.println("Continue (y/n) =");
        
        boolean loop = true;
        while(loop) {
            try {
                String choose = input.next();
                switch(choose.toLowerCase()) {
                    case "y" :
                        return true;
                    case "n" :
                        return false;
                    default :
                        throw new OnlyYN("Insert only \"y\" or \"n\"");
                }
            }
            catch(OnlyYN e) {
                System.out.println(e.getMessage());
            }
        }
        
        return false;
    }
    
    public String userInput() {
        String userinput = null;
        
        boolean loop = true;
        while(loop) {
            try {
                userinput = input.next();
                userinput = userinput.toLowerCase();
                if(userinput.length() != 5)
                    throw new invalidWordSize("\nWord lenght need to equal to 5");
                loop = false;
            }
            catch(invalidWordSize e) {
                System.out.println(e.getMessage());
            }
        }
        
        return userinput;
    }
    
    public void search() {
        System.out.println("\nSearch =");
        String search = input.next();
        boolean found = false;
        for(String obj : D) {
            if(obj.startsWith(search)) {
                found = true;
                System.out.println(obj);
            }
        }
        if(!found) {
            try {
                throw new WordNotFound("Word not found");
            }
            catch(WordNotFound e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public HashSet<String> getD() { return D; }
}

public class Ladder {
    public static void main(String[] args) {
        WL wl = new WL();
    }
}
