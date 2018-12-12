// Team: Chester Neo (172544Q), Cara (173616M), Cassandra (172619P), Joanne (170801C)
package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class DiskOptimization {
    // Create a new Properties object
    Properties p = new Properties();

    DiskParameter dp = null;

    public static void main(String args[]) {
        // Create a new File object with the abstract path of the test case
        File f = new File("OS Practical 6\\assignment\\res\\diskq1.properties");

        // Instantiate from DiskOptimization class with the absolute path of the test case
        new DiskOptimization(f.getAbsolutePath());
    }

    public DiskOptimization(String filename) {
        try {
            // Load the test case file into the Properties object
            p.load(new BufferedReader(new FileReader(filename)));
            // Instantiate from DiskParameter class with the Properties object, to extract and arrange the parameters out for manipulation
            dp = new DiskParameter(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Execute the disk optimization algorithms
        generateAnalysis();
    }

    public void generateAnalysis() {
        generateFCFS();
        generateSSTF();
        generateSCAN();
        generateCSCAN();
        generateLOOK();
    }

    // Display the Order of Access and Total Distance of a disk algorithm
    public void printSequence(String name, int location[]) {
        // Order of Access sequence to print out
        String sequence = "";
        // Insert the current as the first cylinder
        int current = dp.getCurrent();
        sequence += dp.getCurrent();

        int previous = dp.getPrevious();
        int total = 0;
        String working1 = "";
        String working2 = "";

        // Iterate through location[] (algorithm arranged sequence)
        for (int i = 0; i < location.length; i++) {
            // Set current to the iterable element
            int current2 = location[i];

            // Add the iterable element to the sequence string for display
            sequence += "," + current2;

            // Find the distance between the previous and iterable element
            int d = Math.abs(current - current2);

            // Display the workings of the distance calculation
            working1 += "|" + current + "-" + current2 + "|+";
            working2 += d + "+";

            // Find the total distance travelled
            total += d;

            // Set previous to the iterable element for the next distance calculation
            current = current2;
        }

        // Print out the workings
        System.out.println(name + "\n" + "====");
        System.out.println("Order of Access: " + sequence);

        System.out.println("Total Distance = " + working1.substring(0, working1.length() - 1));
        System.out.println("               = " + working2.substring(0, working2.length() - 2));
        System.out.println("               = " + total + "\n");
    }

    public void generateFCFS() {
        int location[] = dp.getSequence();
        printSequence("FCFS", location);
    }

    public void generateSSTF() {
        int location[] = arrangeBySSTF(dp.getCurrent(), dp.getSequence());
        printSequence("SSTF", location);
    }

    // Helper method to convert Array to List
    static List<Integer> arrayToList(int sequence[]) {
        // Create a new List with the specified Array size
        List<Integer> list = new ArrayList<Integer>(sequence.length);

        // Enhanced for loop to loop through the sequence without specifying start and end indexes
        for (Integer s : sequence) {
            list.add(s);
        }

        return (list);
    }

    // Helper method to convert List to Array
    int[] toIntArray(List<Integer> list){
        // Create a new Array with the specified List size
        int[] arr = new int[list.size()];

        // Iterate from index 1 of the List so as not to take the current, since it is added in printSequence()
        for(int i = 1; i < arr.length; i++) {
            arr[i - 1] = list.get(i);
        }
        // Remove the last element 0 from the Array, since the Array index was offset by 1
        arr = Arrays.copyOf(arr, arr.length - 1);

        return arr;
    }

    public void generateSCAN() {
        List<Integer> location = arrangeBySCAN(dp.getCurrent(), dp.getPrevious(), dp.getSequence(), dp.getCylinders());
        // Convert List to Array
        int location2[] = toIntArray(location);

        System.out.println(Arrays.toString(location2));

        printSequence("Scan", location2);
    }

    public void generateCSCAN() {
        List<Integer> location = arrangeByCSCAN(dp.getCurrent(), dp.getPrevious(), dp.getSequence(), dp.getCylinders());
        // Convert List to Array
        int location2[] = toIntArray(location);

        System.out.println(Arrays.toString(location2));

        printSequence("CScan", location2);
    }

    public void generateLOOK() {
        List<Integer> location = arrangeByLOOK(dp.getCurrent(), dp.getPrevious(), dp.getSequence(), dp.getCylinders());
        // Convert List to Array
        int location2[] = toIntArray(location);

        System.out.println(Arrays.toString(location2));

        printSequence("Look", location2);
    }

    // Arrange by SSTF
    private int[] arrangeBySSTF(int current, int sequence[]) {
        int n = sequence.length;

        // Create a new Array with the sequence size
        int sstf[] = new int[n];

        // Iterate through the sequence and populate its elements into the Array
        for (int i = 0; i < n; i++) {
            sstf[i] = sequence[i];
        }

        int ii = -1;

        // Iterate through the Array and for each element i, find the next element that has the shortest distance to it
        for (int i = 0; i < n; i++) {
            // Set a placeholder max value for the minimum
            int minimum = Integer.MAX_VALUE;

            // Set ii to i
            ii = i;

            // Find the distance between the current (previous element) and each element starting from index i (current element)
            for (int j = i; j < n; j++) {
                int distance = Math.abs(current - sstf[j]);

                // If the distance is less than the minimum, set minimum to this distance and ii to this minimum element (what you want to find; the next element in the sequence)
                if (distance < minimum) {
                    ii = j;
                    minimum = distance;
                }
            }

            // Switch the elements in sstf[], to not lose any value and maintain the size of the Array
            // Hold sstf[i] in tmp
            int tmp = sstf[i];
            // Set the next element to go to sstf[i], to replace the current element in the unarranged sequence sstf[i]
            sstf[i] = sstf[ii];
            // sstf[ii] can now be reassigned the current element. The switch is completed.
            sstf[ii] = tmp;

            // Set current to sstf[i] so that the next iteration has the current as the previous element in the arranged sequence
            current = sstf[i];

        }
        System.out.println(Arrays.toString(sstf));
        return sstf;
    }

    // Arrange by Scan
    private List<Integer> arrangeBySCAN(int current, int previous, int sequence[], int cylinders) {
        // Use a List instead of an Array to have a resizable list and manipulate it
        List<Integer> scan = new ArrayList<Integer>();
        // Populate data in Array into List
        scan = arrayToList(sequence);
        scan.add(current);

        // Head is moving towards 0
        if (previous - current > 0) {
            // If scan does not contain the boundary cylinder, add it in
            if (!scan.contains(0)) {
                scan.add(0);
            }
            // Reverse sort scan
            Collections.sort(scan);
            Collections.reverse(scan);
        }
        // Head is moving towards max cylinder
        else if (previous - current < 0) {
            // If scan does not contain the boundary cylinder, add it in
            if (!scan.contains(cylinders)) {
                scan.add(cylinders);
            }
            // Sort scan
            Collections.sort(scan);
        }

        // Find the index of the current cylinder after sorting
        int currentIndex = scan.indexOf(current);

        System.out.println(scan);
        // Split scan into 2 Lists by the current cylinder
        // List with values from 0 to currentIndex - 1
        List<Integer> startingList = scan.subList(0, currentIndex);
        // List with values from currentIndex to max cylinder index
        List<Integer> endingList = scan.subList(currentIndex, scan.size());

        System.out.println(endingList);
        // Reverse startingList
        Collections.reverse(startingList);

        // Append startingList to endingList to recreate the entire list in Scan order
        endingList.addAll(startingList);

        System.out.println(endingList);
        return endingList;
    }

    // Arrange by CScan
    private List<Integer> arrangeByCSCAN(int current, int previous, int sequence[], int cylinders) {
        List<Integer> cscan = new ArrayList<Integer>();
        cscan = arrayToList(sequence);
        cscan.add(current);

        // Add boundary cylinders
        if (!cscan.contains(cylinders)) {
            cscan.add(cylinders);
        }
        if (!cscan.contains(0)) {
            cscan.add(0);
        }

        // Head is moving towards 0
        if (previous - current > 0) {
                // Reverse sort cscan
                Collections.sort(cscan);
                Collections.reverse(cscan);
            }
        // Head is moving towards max cylinder
        else if (previous - current < 0) {
                // Sort cscan
                Collections.sort(cscan);
            }

        // Find the index of the current cylinder after sorting
        int currentIndex = cscan.indexOf(current);

        // Split scan into 2 Lists by the current cylinder
        // List with values from 0 to currentIndex - 1
        List<Integer> startingList = cscan.subList(0, currentIndex);
        // List with values from currentIndex to max cylinder index
        List<Integer> endingList = cscan.subList(currentIndex, cscan.size());

        // Append startingList to endingList to recreate the entire list in Scan order
        endingList.addAll(startingList);

        System.out.println(endingList);
        return endingList;
    }

    // Arrange by Look
    private List<Integer> arrangeByLOOK(int current, int previous, int sequence[], int cylinders) {
        List<Integer> look = new ArrayList<Integer>();
        look = arrayToList(sequence);
        look.add(current);

        // Head is moving towards 0
        if (previous - current > 0) {
            // Reverse sort look
            Collections.sort(look);
            Collections.reverse(look);
        }
        // Head is moving towards max cylinder
        else if (previous - current < 0) {
            // Sort look
            Collections.sort(look);
        }

        // Find the index of the current cylinder after sorting
        int currentIndex = look.indexOf(current);

        // Split scan into 2 Lists by the current cylinder
        // List with values from 0 to currentIndex - 1
        List<Integer> startingList = look.subList(0, currentIndex);
        // List with values from currentIndex to max cylinder index
        List<Integer> endingList = look.subList(currentIndex, look.size());

        // Reverse startingList
        Collections.reverse(startingList);

        // Append startingList to endingList to recreate the entire list in Scan order
        endingList.addAll(startingList);

        System.out.println(endingList);
        return endingList;
    }
}
