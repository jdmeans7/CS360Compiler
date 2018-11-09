import java.util.ArrayList;

public class Driver {
    public static void main(String[] args){
        CompScanner cs = new CompScanner("sqrt.tl");
        ArrayList<String> fl = cs.getFileLines();

        // Print the lines of the file
        for(String s: fl){
            System.out.println(s);
        }

        // Prints the scanned lines
        ArrayList<String> sl = cs.scan();
        for(String s: sl){
            System.out.println(s);
        }

        cs.writeFile("test.txt", sl);

        CompParser cp = new CompParser(sl);
        cp.tokenStream();
        ArrayList<Node> graph = cp.getGraph();
        for (Node n:graph){
            System.out.println(n.getLabel());
        }
    }
}
