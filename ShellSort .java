import java.util.Random;

public class ShellSort {
	
	
	public ShellSort() {
		
	}
	
	//main method
	public static void main(String[] args) {
		//generating and printing random list
		Random rd = new Random();
		int[] rdarr = new int[20];
		System.out.println("Random list: ");
		
		for(int i = 0; i<rdarr.length; i++) {
			rdarr[i] = rd.nextInt();
			System.out.println(rdarr[i]);
		}
		
		//sorting and printing random list
		System.out.println();
		System.out.println("Sorted list: ");
		sort(rdarr);
		for(int i = 0; i<rdarr.length; i++) {
			System.out.println(rdarr[i]);
		}
		
		boolean isSorted = true;
		for(int i = 0; i<rdarr.length-1; i++) {
			if (rdarr[i]>rdarr[i+1]) {
				isSorted = false;
			}
		}
		
		System.out.println();
		if(isSorted)
			System.out.println("List is successfully sorted.");
		else
			System.out.println("List not sorted.");
	}
	
	//shell sort array, takes an int array as input and sorts it using shell sort
	public static void sort(int[] a) {
		int n = a.length;		
		for (int gap = n / 2; gap > 0; gap /= 2) {
	        for (int i = gap; i < n; i++) {
	            int x = a[i];
	            int j = i;
	            while (j >= gap && a[j - gap] > x) {
	                a[j] = a[j - gap];
	                j -= gap;
	            }
	            a[j] = x;
	        }
	    }
		
	}
	
	
}

