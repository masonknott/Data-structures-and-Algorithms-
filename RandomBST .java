import java.util.Random;

public class RandomBST {
	
	public class Node {
		private int value;
		private double balanceValue;
		public Node left, right, Parent;
		
		public Node(int value, double balanceValue) {
			this.value = value;
			this.balanceValue = balanceValue;
			left = null;
			right = null;
			Parent = null;
		}
	
		public int getValue() {
			return value;
		}
		
		public void setValue(int value) {
			this.value = value;
		}
		
		public double getBalanceValue() {
			return balanceValue;
		}
		
		public void setBalanceValue(double balanceValue) {
			this.balanceValue = balanceValue;
		}
		
		private boolean isLeftChild () {
            return Parent != null && Parent.left == this;
        }

        private boolean isRightChild () {
            return Parent != null && Parent.right == this;
        }
		
		public boolean contains(int i) {
			Node current = root;
			return (current != null && current.equals(i))
					|| (current.left != null && current.left.contains(i))
					|| (current.right != null && current.right.contains(i));
		}
		
		int getHeight() {
			if (left == null && right == null)
                return 0;

            int leftHeight = (left == null) ? 0 : left.getHeight();
            int rightHeight = (right == null) ? 0 : right.getHeight();
            return 1 + Math.max (leftHeight, rightHeight);
		}
	}
	
	Node root = null;
	int size = 0;
	
	public void insert(int i) {
		root = insertRec(root, i);
	}
	
	private Node insertRec(Node root, int i) {
		Random random = new Random();
		double randBV = random.nextDouble();
		
		if (root == null) {
			return new Node(i,randBV);
		}
			
		if (i < root.getValue()) {
			root.left = insertRec(root.left, i);
		}
		else {
			root.right = insertRec(root.right, i);
		}
		
		if(root.Parent != null) {
			if (root.balanceValue > root.Parent.balanceValue) {
				if (root.isLeftChild()) {
					rotateLeft(root);
				}
				else {
					rotateRight(root);
				}
			}
		}
		
		return root;
	}
	
	private void rotateLeft (Node x) {
        System.out.println("rotateLeft at " + x.value);

        Node y = x.right; // Non-null because x is right-heavy.
        Node t2 = y.left; // Could be null.

        x.right = t2;
        if (t2 != null)
            t2.Parent = x;
        y.left = x;

        y.Parent = x.Parent;
        if (x.isLeftChild())
            y.Parent.left = y;
        else if (x.isRightChild())
            y.Parent.right = y;
        else
            root = y;
        x.Parent = y;
    }
	
	private void rotateRight (Node x) {
        System.out.println("rotateRight at " + x.value);

        Node y = x.left; // Non-null because x is right-heavy.
        Node t2 = y.right; // Could be null.

        x.left = t2;
        if (t2 != null)
            t2.Parent = x;
        y.right = x;

        y.Parent = x.Parent;
        if (x.isLeftChild())
            y.Parent.left = y;
        else if (x.isRightChild())
            y.Parent.right = y;
        else
            root = y;
        x.Parent = y;
    }
	
	public int getHeight() {
		return (root == null) ? 0 : root.getHeight();
	}
	
	public static void main(String[] args) {
		final int SIZE = 1000;
		final int ITERATIONS = 1000;
		
		double total = 0;
		
		int[] ints = new int[SIZE];
		
		for(int t = 0; t < ITERATIONS; t++) {
			RandomBST bst = new RandomBST();
			for(int i = 0; i < SIZE; i++)
				ints[i] = i;
			
			for(int i = 0; i < SIZE; i++)
				bst.insert(ints[i]);
			
			total += bst.getHeight();
		}
		double avgTotal = total / ITERATIONS;
		System.out.println("Average height: " + avgTotal);
	}
	
}
