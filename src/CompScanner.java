import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CompScanner {
    private File fileName;
    private ArrayList<String> fileLines = new ArrayList<>();
    private ArrayList<String> scannedLines = new ArrayList<>();
    private HashMap<String, String> tokenMap = new HashMap();


    /**
     * Constructor that calls readFile, and builds a HashMap to read all of the tokens.
     * @param name Filename
     */
    public CompScanner(String name) {
        fileName = new File(name);
        readFile();
        // Build HashMap of values
        tokenMap.put("(", "LP");
        tokenMap.put(")", "RP");
        tokenMap.put(":=", "ASGN");
        tokenMap.put(";", "SC");
        tokenMap.put("if", "IF");
        tokenMap.put("then", "THEN");
        tokenMap.put("else", "ELSE");
        tokenMap.put("begin", "BEGIN");
        tokenMap.put("end", "END");
        tokenMap.put("while", "WHILE");
        tokenMap.put("do", "DO");
        tokenMap.put("program", "PROGRAM");
        tokenMap.put("var", "VAR");
        tokenMap.put("as", "AS");
        tokenMap.put("int", "INT");
        tokenMap.put("bool", "BOOL");
        tokenMap.put("writeint", "WRITEINT");
        tokenMap.put("readint", "READINT");
        tokenMap.put("readInt", "READINT");
        tokenMap.put("writeInt", "READINT");
        tokenMap.put("+", "ADDITIVE(+)");
        tokenMap.put("-", "ADDITIVE(-)");
        tokenMap.put("*", "MULTIPLICATIVE(*)");
        tokenMap.put("div", "MULTIPLICATIVE(div)");
        tokenMap.put("mod", "MULTIPLICATIVE(mod)");
        tokenMap.put("=", "COMPARE(=)");
        tokenMap.put("!=", "COMPARE(!=)");
        tokenMap.put("<", "COMPARE(<)");
        tokenMap.put(">", "COMPARE(>)");
        tokenMap.put("<=", "COMPARE(<=)");
        tokenMap.put(">=", "COMPARE(>=)");
        tokenMap.put("false", "FALSE");
        tokenMap.put("true", "TRUE");
    }


    /**
     * Method to read the input file. Reads line by line and adds lines to an ArrayList.
     */
    private void readFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Scanner fileScan = new Scanner(br);
            while (fileScan.hasNextLine()) {
                fileLines.add(fileScan.nextLine());
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    /**
     * Method to write the tokens to an output file
     * @param outFileName Output File Name
     * @param out ArrayList to write
     */
    public void writeFile(String outFileName, ArrayList<String> out){
        try{
            PrintWriter pw = new PrintWriter(new File(outFileName));
            StringBuilder sb = new StringBuilder();
            for (String s:out){
                sb.append(s);
                sb.append('\n');
            }
            pw.write(sb.toString());
            pw.close();
        } catch(IOException e) {
            System.out.println("IOException");
        }
    }


    /**
     * Method to retrieve the read lines.
     * @return ArrayList of lines
     */
    public ArrayList<String> getFileLines() {
        return fileLines;
    }


    /**
     * Method to scan the read file and seperate the lines into tokens for the parser.
     * @return ArrayList of tokens
     * @throws NullPointerException
     */
    public ArrayList<String> scan() throws NullPointerException {
        for (String s : fileLines) {
            String[] a = s.split(" ");
            for (String b : a) {
                b = b.trim();
                if(b.equals("")){}
                else if (b.matches("-?\\d+(\\.\\d+)?")) {
                    scannedLines.add("num(" + b + ")");
                }
                else if (tokenMap.get(b) != null){
                    scannedLines.add(tokenMap.get(b));
                }
                else {
                    scannedLines.add("ident(" + b + ")");
                }
            }
        }
        return scannedLines;
    }
}
