import java.util.ArrayList;

public class Node {
    private String label; //The String representation of what this Node represents
    private ArrayList<Node> children; //All children of this node
    private Boolean root;

    public Node(Boolean root, String label){
        children = new ArrayList<>();
        this.root = root;
        this.label = label;
    }

    public void addChild(Node n){
        children.add(n);
    }

    public void removeChild(Node n) {
        if (children.contains(n)){
            children.remove(n);
        }
    }

    public ArrayList<Node> getChildren(){
        return children;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String s){
        label = s;
    }

    public boolean getRoot(){
        return root;
    }

    public void setRoot(boolean b){
        this.root = b;
    }
}
