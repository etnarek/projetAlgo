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

    public static Debt fromInfo(Graph graph, String from, String to, int amount) {
        return new Debt(graph.getNode(from), graph.getNode(to), amount);
    }
}