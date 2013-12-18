public class Debt {
	private int amount;
	private Node from;
	private Node to;
	
	public Debt(Node from, Node to, int amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
	}

	public Node getFrom() {
		return from;
	}

    public Node getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public int amountSubtract(int amount) {
        this.amount -= amount;
        return this.amount;
    }

    public static Debt fromInfo(Graph graph, String from, String to, int amount) {
        return new Debt(graph.getNode(from), graph.getNode(to), amount);
    }
}