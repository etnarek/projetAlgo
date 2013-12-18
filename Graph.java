import java.lang.Object;
import java.io.*;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Graph {
    private HashMap<String,Node> nodes;

    public Graph(int size) {
        nodes = new HashMap<String,Node>();
    }

    public static Graph fromFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        Graph graph = null;
        int nodeNumber;
        try {
            reader = new BufferedReader(new FileReader(file));
            nodeNumber = Integer.parseInt(reader.readLine());
            graph = new Graph(nodeNumber);
            String buffer = null;
            int i = 0;
            Node node = null;
            String str[];
            while ((i < nodeNumber) && ((buffer = reader.readLine()) != null)) {
                str = new String[2];
                str = buffer.split(" ");
                node = new Node(str[0], Integer.parseInt(str[1]));
                graph.addNode(node);
                i++;
            }
            Debt debt = null;
            while ((buffer = reader.readLine()) != null) {                
                str = new String[3];
                str = buffer.split(" ");
                debt = Debt.fromInfo(graph, str[0], str[1], Integer.parseInt(str[2]));
                graph.addDebt(debt);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public void addNode(Node node){
        nodes.put(node.getName(), node);
    }

    public void addDebt(Debt debt){
        getNode(debt.getFrom().getName()).addDebt(debt);
        
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

    private void cycleDetect(){
        Iterator<Node>nodesIterator = nodes.values().iterator();
        if (nodesIterator.hasNext()) {
            node = nodesIterator.next();
        }
        // use tagged node and continue iterator
        stack = new Vector<Debt>();

        cycleDetect(node, stack);
    }

    private void cycleDetect(Node node, Vector<Debt> stack) {
        Iterator<Debt>debtsIterator = node.getDebts().iterator();
        Debt arrete;
        int minAmount;
        boolean top;
        int position;
        int i;

        node.tag();

        for (; debtsIterator.hasNext(); debt = debtsIterator.next()) {
            if (stack.lastIndexOf(debt) == -1) {
                stack.add(debt);
                cycleDetect(debt, stack);
                stack.remove(stack.size() - 1);
            }
            else {
                position = stack.lastIndexOf(debt);
                minAmount = 0;
                top = false;
                for (i = 0; i < stack.size() && !stop; i++) {
                    arrete = stack.get(i);
                    if (arrete.amount == 0) {
                        stop = true;
                    }
                    else {
                        if (arrete.amount < minAmount || minAmount == 0) {
                            minAmount = arrete.amount;
                        }
                    }
                }
                if (!stop) {
                    for (; position < stack.size(); position++)
                        arrete = stack.get(position);
                        if (arrete.amountRemove(minAmount) == 0) {
                            arrete.getFrom().removeDebt(arrete);
                            // callback
                        }
                    }
                }
            }

    }
    
}