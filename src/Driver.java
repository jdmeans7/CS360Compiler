import java.util.ArrayList;

public class Driver {
    public static void main(String[] args){
        CompScanner cs = new CompScanner("simple1.tl");
        cs.readFile();
        ArrayList<String> fl = cs.getFileLines();
        for(String s: fl){
            System.out.println(s);
        }
    }
}
