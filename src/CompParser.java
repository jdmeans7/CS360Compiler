//TODO: Add parent indicator that can be used before every call to another function to keep track of which node will
// be the parent node.
import java.util.ArrayList;

public class CompParser {
    private ArrayList<Node> graph;
    private ArrayList<String> tokens;
    private int count;

    public CompParser(ArrayList<String> tokens){
        graph = new ArrayList<>();
        this.tokens = tokens;
        count = 0;
    }

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

    public void program(){
        //PROGRAM <declarations> BEGIN <statementSequence> END
        Node decl = new Node(false, "DECL LIST");
        graph.add(decl);
        graph.get(0).addChild(decl);
        while(tokens.get(count).equals("VAR")){
            declarations();
        }
        count++; //Moves token count from BEGIN to first stmt sequence
        Node stmt = new Node(false, "STMT LIST");
        graph.add(stmt);
        graph.get(0).addChild(stmt);
        statementSequence();
    }

    public void declarations(){
        //VAR ident AS <type> SC <declarations> | e
        //System.out.println(tokens.get(count));
        if (tokens.get(count).equals("VAR")){
            StringBuilder sb = new StringBuilder();
            sb.append("DECL:"); //Declaration statement
            sb.append(tokens.get(count+1)); //Adds the variable that is being declared
            count = count + 3; //Sets the token count to where the type will be declared. Skips from VAR to type.
            sb.append(":");
            sb.append(type()); //Calls type to determine type
            Node n = new Node(false, sb.toString());
            graph.get(1).addChild(n); //Adds node to the list of children of the DECL List
            graph.add(n); //Adds node to graph
            count = count + 2; //Sets token to skip type and SC, will be on next VAR or BEGIN.
        }
    }

    public String type(){
        //INT | BOOL
        if (tokens.get(count).equals("INT")){
            return "INT";
        }
        if (tokens.get(count).equals("BOOL")){
            return "BOOL";
        }
        else return "";
    }

    public void statementSequence(){
        //<statement> SC <statementSequence> | e
        while(!tokens.get(count).equals("END")){
        //if(tokens.get(count).contains("ident") || tokens.get(count).equals("IF") || tokens.get(count).equals("WHILE")
        //        || tokens.get(count).equals("WRITEINT")){
            statement();
        }

    }

    public void statement(){
        //<assignment> | <ifStatement> | <whileStatement> | <writeInt>
        if(tokens.get(count).contains("ident")){
            assignment();
        }
        else if (tokens.get(count).equals("IF")){
            ifStatement();
        }
        else if (tokens.get(count).equals("WHILE")){
            whileStatement();
        }
        else if (tokens.get(count).equals("WRITEINT")){
            writeInt();
        }
    }

    public void assignment(){
        //ident ASGN <expression> | ident ASGN READINT
        Node asgn = new Node(false, ":=");
        graph.get(2).addChild(asgn);
        graph.add(asgn);
        if (tokens.get(count+2).equals("READINT")){
            Node m = new Node(false, tokens.get(count));
            Node n = new Node(false, "READINT:INT");
            graph.get(graph.size() - 1).addChild(m);
            graph.get(graph.size() - 1).addChild(n); //Adds this node as child of STMT LIST
            graph.add(m);
            graph.add(n);
            //count = tokens.size() - 1;
        }
        else {
            Node m = new Node(false, tokens.get(count));
            graph.get(graph.size() - 1).addChild(m);
            graph.add(m);
            expression();
        }
    }

    public void ifStatement(){
        //IF <expression> THEN <statementSequence> <elseClause> END
        Node n = new Node(false, tokens.get(count));
        Node m = new Node(false, "THEN");
        graph.get(graph.size() - 1).addChild(n);
        graph.get(graph.size() - 1).addChild(m);
        graph.add(n);
        count++; //move to expression token
        expression();
    }

    public void elseCause(){
        //ELSE <statementSequence> | e
    }

    public void whileStatement(){
        //WHILE <expression> DO <statementSequence> END
    }

    public void writeInt(){
        //WRITEINT <expression>
    }

    public void expression(){
        //<simpleExpression> | <simpleExpression> COMPARE <expression>
        if (tokens.get(count).contains("COMPARE")){
            Node n = new Node(false, tokens.get(count + 1));
            graph.get(graph.size() - 2).addChild(n);
            graph.add(n);
            simpleExpression();
            expression();
        }
        else{
            simpleExpression();
        }
    }

    public void simpleExpression(){
        //<term> ADDITIVE <simpleExpression> | <term>
        if (tokens.get(count).contains("ADDITIVE")){
            Node n = new Node(false, tokens.get(count + 1));
            graph.get(graph.size() - 2).addChild(n);
            graph.add(n);
            term();
            simpleExpression();
        }
        else{
            term();
        }
    }

    public void term(){
        //<factor> MULTIPLICATIVE <term> | <factor>
        if (tokens.get(count).contains("MULTIPLICATIVE")){
            Node n = new Node(false, tokens.get(count + 1));
            graph.get(graph.size() - 2).addChild(n);
            graph.add(n);
            factor();
            term();
        }
        else{
            factor();
        }
    }

    public void factor(){
        //ident | num | boolit | LP <simpleExpression> RP
        if (tokens.get(count).contains("ident") || tokens.get(count).contains("num") || tokens.get(count).contains("boolit")){
            Node n = new Node(false, tokens.get(count));
            graph.get(graph.size() - 2).addChild(n);
            graph.add(n);
        }
        else { // LP <simpleExpression> RP
            Node n = new Node(false, tokens.get(count));
            graph.get(graph.size() - 1).addChild(n);
            graph.add(n);
            count++;
            simpleExpression();
            count++;
            Node m = new Node(false, tokens.get(count));
            graph.get(graph.size() - 3).addChild(m);
            graph.add(m);
            count++;
        }
    }

    public ArrayList<Node> getGraph(){
        return graph;
    }

}
