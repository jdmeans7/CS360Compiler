import java.util.ArrayList;

public class Driver {
    public static void main(String[] args){
        CompScanner cs = new CompScanner("simple1.tl");
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
        int count = 1;
        for (Node n:graph){
            System.out.print("Node: " + count + " - " + n.getLabel());
            for(Node m:n.getChildren()){
                System.out.print(" | " +m.getLabel());
            }
            System.out.println();
        }

        cp.createDot("dotTest");

    }
}
