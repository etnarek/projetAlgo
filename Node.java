import java.util.Vector;
import java.util.Collections;

public class Node {
	private int balance;
	private String name;
	private Vector<Debt> debts;
	private boolean reached;
	private int creanceCounter;

	public Node(String name, int balance) {
		this.name = name;
		this.balance = balance;
		reached = false;
		debts = new Vector<Debt>();
		creanceCounter = 0;
	}

	public String getName() {
		return name;
	}
	
	public int getBalance() {
		return balance;
	}

	public boolean isHead() {
		return creanceCounter == 0;
	}

	public void addCreance() {
		creanceCounter++;
	}

	public void delCreance() {
		creanceCounter--;
	}

	public void addDebt(Debt debt) {
		debts.add(debt);
	}

	public void tag() {
		reached = true;
	}

	public boolean isReached() {
		return reached;
	}

	public Vector<Debt> getDebts() {
		return debts;
	}

	public void removeDebt(Debt debt) {
		debts.set(debts.indexOf(debt), null);
	}

	// return itself in a dot-formatted string
	public String toDot() {
		String output = "";
		Debt debt;
		Node debtTo;
		output += String.format("\"%s\\n%d\"\n", name, balance);

		// read through every debt the node has
		for(int i=0; i < debts.size(); i++) {
			debt = debts.get(i);
			// if debt is solved, it is setted to null
			if(debt != null) {
				debtTo = debt.getTo();
				output += String.format("\"%s\\n%d\" -> \"%s\\n%d\" [label=\"%d\"]%n",
					name, balance, debtTo.getName(), debtTo.getBalance(), debt.getAmount());
			}
		}
		return output;
	}

	// resolve all non null debts
	public void resolveDebt() {
		Debt debt;
		for(int i = 0; i < debts.size(); i++){
			debt = debts.get(i);
			if(debt != null){
			debt.resolveDebt();
			}
		}
	}

	public int pay(int amount){
		if(amount > balance){
			amount -= balance;
			balance = 0;
		}
		else{
			balance -= amount;
			amount = 0;
		}
		return amount;
	}
	public void add(int amount) {
		balance +=amount;
	}
}