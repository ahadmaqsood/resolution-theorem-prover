import java.util.Scanner;  // Import the Scanner class

public class prover
{
    public static void main(String[] args) 
    {
        ResolutionSolver solver = new ResolutionSolver();
        System.out.println("Problem Names:\n• howling\n• rr\n• customs\n• harmonia\n• custom");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Problem Name: ");
        String problemName = myObj.nextLine();  // Read user input

        solver.readClauses("../Examples/"+ problemName + ".txt");
        solver.printClauses();
        solver.solve();
    }
}