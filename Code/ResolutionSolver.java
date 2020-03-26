import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ResolutionSolver
{
    private ArrayList<Clause> clauses = new ArrayList<Clause> ();
    private ArrayList<Clause> goalClauses = new ArrayList<Clause> ();
    private int originalClausesSize = 0;
    
    private String normalizeParantheses(String givenString)
    {
        int counter = 0;
        for (int i = 0; i < givenString.length(); ++i)
        {
            if (givenString.charAt(i) == '(')
                ++counter;
            else if (givenString.charAt(i) == ')')
                --counter;
        }

        if (counter < 0)
        {
            // Remove last 2 ")"s
            return (givenString.substring(0, givenString.lastIndexOf(")", givenString.lastIndexOf(")") - 1)));
        }

        return givenString;
    }

    public void readClauses(String inputFile)
    {
        BufferedReader reader;
        try 
        {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine().trim();
            boolean goalStarted = false;
            while (line != null)
            {
                var nextLine = reader.readLine();
                
                var clauseStrings = line.split("[']");
                String clauseString = line;
                if (clauseStrings.length > 1)
                clauseString = clauseStrings[clauseStrings.length - 1].split("[(]", 2)[1];
                if (clauseString.indexOf("goal") != -1)
                goalStarted = true;

                clauseString = clauseString.trim();
                clauseString = normalizeParantheses(clauseString);
                if (goalStarted)
                {
                    goalClauses.add(new Clause(clauseString));
                }
                else
                {
                    clauses.add(new Clause(clauseString));
                }
                
                line = nextLine;
            }
            reader.close();
            originalClausesSize = clauses.size();
            addGoals();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void printClauses()
    {
        for (var clause: clauses)
        System.out.println(clause);
    }
    
    private void addGoals()
    {
        for (var goal: goalClauses)
        {
            // goal.negateClause();
            clauses.add(goal);
        }
    }
    
    private boolean checkDuplicate (Clause toCheck)
    {
        for (var clause: clauses)
        {
            if (clause.isEqualTo(toCheck))
                return true;
        }

        return false;
    }

    private Clause removeAndCreateClause (Clause original1, Clause original2, int p1Remove, int n1Remove, int p2Remove, int n2Remove, SubstitutionSet ss, int clauseNo)
    {
        ArrayList<Unifiable> positivesForClause = new ArrayList<Unifiable> ();
        ArrayList<Unifiable> negativesForClause = new ArrayList<Unifiable> ();
        // Go through positives and negatives of both clauses
        for (int i = 0; i < original1.getPositiveTerms().size(); ++i)
        {
            if (i == p1Remove)
                continue;
            
            positivesForClause.add((Predicate) original1.getPositiveTerms().get(i).replaceVariables(ss));
        }

        for (int j = 0; j < original1.getNegativeTerms().size(); ++j)
        {
            if (j == n1Remove)
                continue;
            
            negativesForClause.add((Predicate) original1.getNegativeTerms().get(j).replaceVariables(ss));
        }

        for (int i = 0; i < original2.getPositiveTerms().size(); ++i)
        {
            if (i == p2Remove)
                continue;
            
            positivesForClause.add((Predicate) original2.getPositiveTerms().get(i).replaceVariables(ss));
        }

        for (int j = 0; j < original2.getNegativeTerms().size(); ++j)
        {
            if (j == n2Remove)
                continue;
            
            negativesForClause.add((Predicate) original2.getNegativeTerms().get(j).replaceVariables(ss));
        }

        Clause toReturn = new Clause(clauseNo, positivesForClause, negativesForClause);
        return toReturn;
    }

    private List<Clause> tryResolve(int clause1Index, int clause2Index)
    {
        int clauseNo = clauses.size() + 1;
        List<Clause> toReturn = new ArrayList<Clause> ();
        Clause clause1 = clauses.get(clause1Index),
               clause2 = clauses.get(clause2Index);
        
        // Get positive and negative terms
        ArrayList<Unifiable> clause1Positives = clause1.getPositiveTerms(),
                        clause1Negatives = clause1.getNegativeTerms(),
                        clause2Positives = clause2.getPositiveTerms(),
                        clause2Negatives = clause2.getNegativeTerms();

        // Check positive of 1 and negative of 2
        for (int i = 0; i < clause1Positives.size(); ++i)
        {
            for (int j = 0; j < clause2Negatives.size(); ++j)
            {
                var literal1Predicate = (Predicate) clause1Positives.get(i);
                var literal2Predicate = (Predicate) clause2Negatives.get(j);

                // Check if predicates are equal after unifying
                // TODO: check for repeating variables before unifying

                literal1Predicate.changeMatchingVariables(literal2Predicate);

                var unifier = literal1Predicate.unify(literal2Predicate, new SubstitutionSet());
                if (unifier != null)
                {
                    // Replace using unifier
                    literal1Predicate = (Predicate) literal1Predicate.replaceVariables(unifier);
                    literal2Predicate = (Predicate) literal2Predicate.replaceVariables(unifier);
                }
                
                if (literal1Predicate.isEqualTo(literal2Predicate))
                {
                    toReturn.add(removeAndCreateClause(clause1, clause2, i, -1, -1, j, unifier, clauseNo));
                    ++clauseNo;
                }
            }
        }


        // Check negative of 1 and positive of 2
        for (int i = 0; i < clause2Positives.size(); ++i)
        {
            for (int j = 0; j < clause1Negatives.size(); ++j)
            {
                var literal1Predicate = (Predicate) clause2Positives.get(i);
                var literal2Predicate = (Predicate) clause1Negatives.get(j);

                // Check if predicates are equal after unifying
                var unifier = literal1Predicate.unify(literal2Predicate, new SubstitutionSet());
                if (unifier == null)
                continue;

                // Replace using unifier
                Predicate replacedPredicate1 = (Predicate) literal1Predicate.replaceVariables(unifier);
                Predicate replacedPredicate2 = (Predicate) literal2Predicate.replaceVariables(unifier);
               
                if (replacedPredicate1.isEqualTo(replacedPredicate2))
                {
                    toReturn.add(removeAndCreateClause(clause1, clause2, -1, j, i, -1, unifier, clauseNo));
                    ++clauseNo;
                }
            }
        }
        
        return toReturn;
    }
    
    public void solve()
    {
        boolean solutionFound = false;
        // Use inner loop pointer, outer loop pointer
        // ilp -> Beginning of clauses, olp -> Beginning of negated goals
        int innerLoopPointer = 0, outerLoopPointer = originalClausesSize;
          
        while(!solutionFound)
        {
            // Try finding resolution (positive and negative + unification stuff)
            var newClauses = tryResolve(innerLoopPointer, outerLoopPointer);

            // If found, add new clauses at the end
            // If false, END (proven)
            for (var newClause: newClauses)
            {   
                if(checkDuplicate(newClause))
                    continue;            
                System.out.println(newClause + " -> From (" + (innerLoopPointer + 1) + ", " + (outerLoopPointer + 1) + ")");

                if (newClause.isEmpty())
                {
                    System.out.println("Solution found!");
                    return;
                }

                clauses.add(newClause);
            }

            // Move ilp forward
            ++innerLoopPointer;
            // If ilp == olp
            if (innerLoopPointer == outerLoopPointer)
            {
                // increase olp and reset ilp to 0
                ++outerLoopPointer;
                innerLoopPointer = 0;
                // if olp out of bounds
                if (outerLoopPointer >= clauses.size())
                {
                    System.out.println("NO SOLUTION");
                    // no solution
                    return;
                }
            }

            // otherwise repeat
        }
    }
    
}