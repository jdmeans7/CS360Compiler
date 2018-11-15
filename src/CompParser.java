//TODO: Add parent indicator that can be used before every call to another function to keep track of which node will
// be the parent node.
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.white;

public class CompParser {
    private ArrayList<Node> graph;
    private ArrayList<String> tokens;
    private int count;
    //private int STMTIndex;


    /**
     * Constructor for class that creates empty graph and fills tokens
     * @param tokens ArrayList that contains all of the tokens scanned by CompScanner
     */
    public CompParser(ArrayList<String> tokens){
        graph = new ArrayList<>();
        this.tokens = tokens;
        count = 0;
    }


    /**
     * Method to start the token stream, looks for PROGRAM to start and goes from there
     */
    public void tokenStream(){
        if (tokens.get(0).equals("PROGRAM")){
            graph.add(new Node(true, "PROGRAM"));
            count++;
            program();
        }
        else {
            System.out.println("ERROR: STARTING TOKEN IS NOT PROGRAM, CANNOT CONSTRUCT GRAMMAR");
        }
    }


    /**
     * Method to handle the PROGRAM token
     * PROGRAM <declarations> BEGIN <statementSequence> END
     */
    public void program(){
        Node decl = new Node(false, "DECL LIST");
        graph.add(decl);
        graph.get(0).addChild(decl); //Adds DECL LIST as child to PROGRAM
        while(tokens.get(count).equals("VAR")){
            declarations(1); //Begin declarations
        }
        count++; //Moves token count from BEGIN to first stmt sequence
        Node stmt = new Node(false, "STMT LIST");
        graph.add(stmt);
        //STMTIndex = graph.size()-1;
        graph.get(0).addChild(stmt);
        statementSequence(graph.size()-1);
    }


    /**
     * Method to handle the declarations. Looks for VAR token and scans all vars that are declared.
     * Stops when the BEGIN token is found
     * VAR ident AS <type> SC <declarations> | e
     * @param parentIndex index of parent node
     */
    public void declarations(int parentIndex){
        //System.out.println(tokens.get(count));
        if (tokens.get(count).equals("VAR")){
            StringBuilder sb = new StringBuilder();
            sb.append("DECL:"); //Declaration statement
            sb.append(tokens.get(count+1)); //Adds the variable that is being declared
            count = count + 3; //Sets the token count to where the type will be declared. Skips from VAR to type.
            sb.append(":");
            sb.append(type()); //Calls type() to determine type
            Node n = new Node(false, sb.toString());
            graph.get(parentIndex).addChild(n); //Adds node to the list of children of the DECL LIST
            graph.add(n); //Adds node to graph
            count = count + 2; //Sets token to skip type and SC, will be on next VAR or BEGIN.
        }
    }


    /**
     * Method to read token and determine type
     * INT | BOOL
     * @return String "INT" or "BOOL"
     */
    public String type(){
        if (tokens.get(count).equals("INT")){
            return "INT";
        }
        if (tokens.get(count).equals("BOOL")){
            return "BOOL";
        }
        else return "";
    }


    /**
     * Method to begin a statement sequence. Scans tokens until an END token is found.
     * <statement> SC <statementSequence> | e
     * @param STMT Index of STMT LIST node
     */
    public void statementSequence(int STMT){
        while(!tokens.get(count).equals("END")){
            if(tokens.get(count).equals("SC")){count++;}
            statement(STMT);
        }

    }


    /**
     * Method to handle statements. Looks to see if token is assign, if, while, or writeint and handles it from there.
     * <assignment> | <ifStatement> | <whileStatement> | <writeInt>
     * @param STMT Index of STMT node
     */
    public void statement(int STMT){
        try {
            if (tokens.get(count + 1).contains("ASGN")) {
                assignment(STMT);
            }
            else if (tokens.get(count).equals("IF")){
                ifStatement(STMT);
            }
            else if (tokens.get(count).equals("WHILE")){
                whileStatement(STMT);
            }
            else if (tokens.get(count).equals("WRITEINT")){
                writeInt(STMT);
            }
        }catch(IndexOutOfBoundsException e){}
    }


    /**
     * Method to handle the assignment of a variable.
     * ident ASGN <expression> | ident ASGN READINT
     * @param parentIndex Index of parent node
     */
    public void assignment(int parentIndex){
        //ident ASGN <expression> | ident ASGN READINT
        Node asgn = new Node(false, ":=");
        graph.get(parentIndex).addChild(asgn);
        graph.add(asgn);
        int ASGNIndex = graph.size()-1;
        if (tokens.get(count+2).equals("READINT")){ //If the variable is assigned to readint
            Node m = new Node(false, tokens.get(count));
            Node n = new Node(false, "READINT:INT");
            graph.get(ASGNIndex).addChild(m);
            graph.get(ASGNIndex).addChild(n);
            graph.add(m);
            graph.add(n);
            count = count + 4;
        }
        else {
            Node m = new Node(false, tokens.get(count));
            count = count + 2;
            graph.get(ASGNIndex).addChild(m);
            graph.add(m);
            expression(ASGNIndex);
        }
    }


    /**
     * Method to handle if statements.
     * IF <expression> THEN <statementSequence> <elseClause> END
     * @param parentIndex Index of parent node.
     */
    public void ifStatement(int parentIndex){
        Node n = new Node(false, tokens.get(count)); //IF node
        graph.get(parentIndex).addChild(n); //Added to STMT LIST
        graph.add(n);
        count++; //move to expression token
        int ifIndex = graph.size()-1;
        Node e = expression(ifIndex);
        graph.get(ifIndex).addChild(e);
        count++;
        statementSequence(ifIndex);
        count++;
        elseCause(ifIndex);
        //Node l = elseCause(parentIndex);
    }


    /**
     * Method to handle else clauses for the if statement method.
     * ELSE <statementSequence> | e
     * @param parentIndex Index of the parent node
     */
    public void elseCause(int parentIndex){
        if (tokens.get(count).equals("ELSE")) {
            Node n = new Node(false, tokens.get(count));
            graph.get(parentIndex).addChild(n);
            graph.add(n);
            count++; //Move past ELSE
            statementSequence(graph.size()-1);
        }
    }


    /**
     * Method to handle while loops.
     * WHILE <expression> DO <statementSequence> END
     * @param parentIndex Index of the parent node
     */
    public void whileStatement(int parentIndex){
        Node n = new Node(false, tokens.get(count));
        graph.get(parentIndex).addChild(n);
        graph.add(n);
        int whileIndex = graph.size()-1;
        count++; //Move past WHILE
        Node e = expression(whileIndex);
        graph.get(whileIndex).addChild(e);
        //graph.add(e);
        count++; //Move past DO
        statementSequence(whileIndex);
        count++; //Move past END
    }


    /**
     * Method to handle writeInt tokens
     * //WRITEINT <expression>
     * @param parentIndex Index of the parent node
     */
    public void writeInt(int parentIndex){
        Node n = new Node(false, "writeInt");
        graph.get(parentIndex).addChild(n);
        graph.add(n);
        count++;
        expression(graph.size()-1);
    }


    /**
     * Method to handle expressions.
     * //<simpleExpression> | <simpleExpression> COMPARE <expression>
     * @param parentIndex Index of the parent node
     * @return Parent node of the expression
     */
    public Node expression(int parentIndex){
        Node returned = simpleExpression(parentIndex); //Do SimpleExpression
        if (tokens.get(count).contains("COMPARE")){ //Now if after simpleexpression there is a compare, we know its the second case
            if(graph.get(parentIndex).getChildren().contains(returned)) {
                graph.get(parentIndex).removeChild(returned);
            }
            Node n = new Node(false, tokens.get(count));
            count++;
            graph.add(n);
            int compIndex = graph.size()-1;
            graph.get(compIndex).addChild(returned); //Add left child to ADDITIVE
            //graph.add(returned);
            expression(compIndex);
            //graph.get(compIndex).addChild(m); //Add right child to ADDITIVE
            //graph.add(m);
            return n;
        }
        else{
            if (graph.get(parentIndex).getChildren().contains(returned)){
                return returned;
            }
            else {
                graph.get(parentIndex).addChild(returned);
                return returned;
            }
        }
    }


    /**
     * Method to handle simple expressions
     * <term> ADDITIVE <simpleExpression> | <term>
     * @param parentIndex Index of the parent node
     * @return Parent node of the expression
     */
    public Node simpleExpression(int parentIndex){
        Node returned = term(parentIndex); //Do TERM
        if (tokens.get(count).contains("ADDITIVE")){ //Now if after term there is an ADDITIVE, we know its the first case
            Node n = new Node(false, tokens.get(count)); //ADDITIVE node
            if(graph.get(parentIndex).getChildren().contains(returned)) {
                graph.get(parentIndex).removeChild(returned);
            }
            count++;
            graph.add(n);
            int addIndex = graph.size()-1;
            graph.get(addIndex).addChild(returned); //Add left child to ADDITIVE
            //graph.add(returned);
            simpleExpression(addIndex);
            //graph.get(addIndex).addChild(m); //Add right child to ADDITIVE
            //graph.add(m);
            return n;
        } else {
            if (graph.get(parentIndex).getChildren().contains(returned)){
                return returned;
            }
            else {
                graph.get(parentIndex).addChild(returned);
                return returned;
            }
        }
    }


    /**
     * Method to handle terms.
     * <factor> MULTIPLICATIVE <term> | <factor>
     * @param parentIndex Index of the parent node
     * @return Parent node of the term.
     */
    public Node term(int parentIndex){
        Node returned = factor(parentIndex); //Do TERM
        if (tokens.get(count).contains("MULTIPLICATIVE")){ //Now if after term there is an MULTI, we know its the first case
            Node n = new Node(false, tokens.get(count)); //This Node is the MULTI
            if(graph.get(parentIndex).getChildren().contains(returned)) {
                graph.get(parentIndex).removeChild(returned);
            }
            count++;
            graph.add(n);
            int multiIndex = graph.size()-1;
            graph.get(multiIndex).addChild(returned); //Add left child to MULTI
            //graph.add(returned);
            Node m = term(multiIndex);
            //graph.get(multiIndex).addChild(m); //Add right child to MULTI
            //graph.add(m);
            return n;
        } else {
            if (graph.get(parentIndex).getChildren().contains(returned)){
                return returned;
            }
            else {
                graph.get(parentIndex).addChild(returned);
                return returned;
            }
        }
    }


    /**
     * Method to handle factors
     * ident | num | boolit | LP <simpleExpression> RP
     * @param parentIndex Index of parent node
     * @return Parent node of factor
     */
    public Node factor(int parentIndex){
        if (tokens.get(count).contains("ident") || tokens.get(count).contains("num") || tokens.get(count).contains("boolit")){
            Node n = new Node(false, tokens.get(count));
            //graph.get(parentIndex).addChild(n);
            graph.add(n);
            count++;
            return n;
        }
        else { // LP <simpleExpression> RP
            Node n = new Node(false, tokens.get(count));
            graph.get(graph.size() - 1).addChild(n);
            graph.add(n);
            count++;
            //simpleExpression();
            count++;
            Node m = new Node(false, tokens.get(count));
            graph.get(graph.size() - 3).addChild(m);
            graph.add(m);
            count++;
            return n;
        }
    }


    /**
     * Method to return the graph
     * @return ArrayList of nodes
     */
    public ArrayList<Node> getGraph(){
        return graph;
    }


    /**
     * Translate the graph that was created into a dot file that can be read by Graphviz
     * @param outFileName The name of the file that will be written to.
     */
    public void createDot(String outFileName){
        try{
            PrintWriter pw = new PrintWriter(new File(outFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("digraph t112Ast {\n");
            sb.append("ordering=out\n");
            sb.append("node [shape = box, style = filled, fillcolor=\"white\"];\n");
            for(int i = 0;i<graph.size();i++) {
                sb.append("n" + i + "[label = \"" +graph.get(i).getLabel() + "\",shape=box]");
                sb.append("\n");
                if (graph.get(i).getChildren().size() > 0){
                    for (Node n: graph.get(i).getChildren()){
                        sb.append("n" + i + " -> ");
                        sb.append("n" + (graph.indexOf(n)));
                        sb.append("\n");
                    }
                }
            }
            sb.append("}");
            pw.write(sb.toString());
            pw.close();
        } catch(IOException e) {
            System.out.println("IOException");
        }
    }
}
