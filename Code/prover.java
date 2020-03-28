import java.util.Scanner;  // Import the Scanner class
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class prover
{
    public static void main(String[] args) 
    {
        ResolutionSolver solver = new ResolutionSolver();
        System.out.println("Problem Names:\n• howling\n• rr\n• customs\n• harmonia\n• special");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Problem Name: ");
        String problemName = myObj.nextLine();  // Read user input
        try
        {
            File file = new File(problemName + "Out.txt");
            //Instantiating the PrintStream class
            PrintStream stream = new PrintStream(file);
            System.setOut(stream);
        }
        catch(Exception ex)
        {

        }
        solver.readClauses("../Examples/"+ problemName + ".txt");
        solver.printClauses();
        solver.solve();
    }
}