import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CompScanner {
    private File fileName;
    private ArrayList<String> fileLines = new ArrayList<>();
    private ArrayList<String> scannedLines = new ArrayList<>();

    public CompScanner(String name){
        fileName = new File(name);
    }

    public void readFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Scanner fileScan = new Scanner(br);
            while (fileScan.hasNextLine()){
                fileLines.add(fileScan.nextLine());
            }
        }catch(IOException e){System.out.println("IOException");}
    }

    public ArrayList<String> getFileLines(){
        return fileLines;
    }

    private boolean scan(){
        for (String s:fileLines){
            if (s.equals("program")){
                scannedLines.add("PROGRAM");
            }
            if (s.contains("var")){
                String[] a = s.split(" ");
                scannedLines.add("VAR");
                scannedLines.add("IDENT(" + a[1] + ")");
                scannedLines.add("AS");
                scannedLines.add(a[3].toUpperCase());
            }
            if (s.equals("begin")){
                scannedLines.add("BEGIN");
            }
            if (s.contains(":=")){
                String[] a = s.split(" ");
                scannedLines.add("IDENT(" + a[0] + ")");
                scannedLines.add("ASGN");
                if(a[2].equals("readint")){
                    scannedLines.add(("READINT"));
                }

            }
        }
        return true;
    }
}
