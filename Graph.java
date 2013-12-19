import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Graph {
    private HashMap<String,Node> nodes;
    private int order;

    public Graph(int size) {
        nodes = new HashMap<String,Node>();
        order = size;
    }

    // Create a new graph from a file.
    public static Graph fromFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        Graph graph = null;
        
        try {
            int order;
            reader = new BufferedReader(new FileReader(file));
            order = Integer.parseInt(reader.readLine());
            graph = new Graph(order);
            
            graph.addNodesFromReader(reader);
            graph.addDebtsFromReader(reader);
            
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    private void addNodesFromReader(BufferedReader reader) {
        String buffer = null;
        int i = 0;
        Node node = null;
        String output[];
        try{
            while ((i < order) && ((buffer = reader.readLine()) != null)) {
                output = new String[2];
                output = buffer.split(" ");
                node = new Node(output[0], Integer.parseInt(output[1]));
                addNode(node);
                i++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addDebtsFromReader(BufferedReader reader){
        Debt debt = null;
        String buffer = null;
        String output[];
        try{
            while ((buffer = reader.readLine()) != null) {                
                output = new String[3];
                output = buffer.split(" ");
                debt = Debt.fromInfo(this, output[0], output[1], Integer.parseInt(output[2]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toFile(String fileName) {
        try {
            PrintWriter file = new PrintWriter(fileName, "UTF-8");
            file.print(toDot());
            file.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void addNode(Node node){
        nodes.put(node.getName(), node);
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }
    public String toDot() {
        String output = "digraph plop {\n";
        Iterator<Node>nodesIterator = nodes.values().iterator();
        while(nodesIterator.hasNext()) {
            output += nodesIterator.next().toDot();
        }
        output += "}";
        return output;
    }

    public void cycleDetectAndResolve(){
        Iterator<Node>nodesIterator = nodes.values().iterator();
        Node node;
        DebtStack stack;

        // walk through each node of the graph
        while (nodesIterator.hasNext()) {
            node = nodesIterator.next();
            // check if node was already reached
            if (!node.isReached()){
                // if not, try to detect a cycle by walking 
                // in the graph, beginning from that node
                stack = new DebtStack();
                cycleDetectAndResolve(node, stack);
            }
        }
    }

    private int minimalDebt(int position, DebtStack stack, StringBuilder cycleString){
        int minAmount = 0;
        Debt arrete;
        for (int i = position; i < stack.size() && minAmount != -1 ; i++) {
            arrete = stack.get(i);
            cycleString.append(String.format("%s (%d) -> ", arrete.getFrom().getName(), arrete.getAmount()));
            if (arrete.getAmount() == 0)
                // Cycle detected previousy was broken, abort
                minAmount = -1;
            else if(arrete.getAmount() < minAmount || minAmount == 0)
                // lower amount found
                minAmount = arrete.getAmount();
        }

        return minAmount;
    }

    private void cycleResolve(Node currentNode, Debt currentDebt, DebtStack stack) {
        StringBuilder cycleString = new StringBuilder("");
        int minAmount = 0;
        int position, i;
        Debt arrete = null;

        position = stack.lastIndexOfNode(currentNode);
        minAmount = minimalDebt(position, stack, cycleString);
        if (minAmount != -1) {
            System.out.println("\nRéduction de " + minAmount);
            // re-add first node to the end of the string
            cycleString.append(String.format("%s (%d) -> ...", currentDebt.getFrom().getName(), currentDebt.getAmount()));
            System.out.println(cycleString);

            String cycle2 = "";
            for (; position < stack.size(); position++) {
                arrete = stack.get(position);
                if (arrete.amountSubstract(minAmount) == 0){
                    if(cycle2.length() > 0)
                        cycle2 += arrete.getFrom().getName() + "\n";
                }
                else{
                    cycle2 += String.format("%s (%d) -> ", arrete.getFrom().getName(), arrete.getAmount());
                }
            }
            if(!cycle2.endsWith("\n"))
                cycle2 += currentNode.getName();
            else
                cycle2 = cycle2.substring(0, cycle2.length()-1);
            System.out.println("Nouvelle situation:");
            System.out.println(cycle2);
        }
    }

    private void cycleDetectAndResolve(Node node, DebtStack stack) {
        Vector<Debt> debts = node.getDebts();
        Debt currentDebt = null;
        int i;
        
        // tag the node as "reached"
        node.tag();
        
        // walk through the node's debts
        for (i = 0; i < debts.size(); i++) {
            currentDebt = debts.get(i);
            if(currentDebt != null){
                // test if in the stack
                if (stack.lastIndexOfNode(node) == -1) {
                    // node not in the stack => no cycle
                    stack.push(currentDebt);
                    cycleDetectAndResolve(currentDebt.getTo(), stack);
                    stack.pop();
                }
                else {
                    // node in stack => cycle
                    cycleResolve(node, currentDebt, stack);
                }
            }
        }
    }


    public void resolveDebt() {
        Iterator<Node>nodesIterator = nodes.values().iterator();
        Node node;
        while(nodesIterator.hasNext()) {
            node = nodesIterator.next();
            if(node.isHead()){
                node.resolveDebt();
            }
        }
    }
}