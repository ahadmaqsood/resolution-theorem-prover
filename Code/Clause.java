import java.util.*;

public class Clause
{
    private int clauseNumber;
    private ArrayList<Unifiable> positiveLiterals;
    private ArrayList<Unifiable> negativeLiterals;

    private String[] getLiteralsStrings(String clauseString)
    {
        String toReturn [] = new String[2];
        int startIndex = clauseString.indexOf("(", clauseString.indexOf("(") + 1) + 1;
        int endIndex = startIndex;
        int counter = 0;
        
        while (counter >= 0)
        {
            if (clauseString.charAt(endIndex) == ')')
                --counter;
            else if (clauseString.charAt(endIndex) == '(')
                ++counter;
            ++endIndex;
        }

        toReturn [0] = clauseString.substring(startIndex, endIndex - 1).trim();

        // for negatives string
        startIndex = clauseString.indexOf("(", endIndex) + 1;
        // get 2nd last ')'
        endIndex = clauseString.lastIndexOf(")",clauseString.lastIndexOf(")") - 1);

        toReturn [1] = clauseString.substring(startIndex, endIndex).trim();
        return toReturn;
    }

    private ArrayList<Unifiable> convertStringToLiterals(String literalsString)
    {
        ArrayList<Unifiable> toReturn = new ArrayList<Unifiable> ();

        Stack<Object> theStack = new Stack<Object> ();

        for (int i = 0; i < literalsString.length(); ++i)
        {
            // if you find literal and no '(' or ')', it's a variable
            if (literalsString.charAt(i) == ' ')
                continue;
            // Push if you find (
            else if (literalsString.charAt(i) == '(')
            {
                theStack.push("(");
            }
            else if (literalsString.charAt(i) == ')')
            {
                // Pop and evaluate
                ArrayList<Object> popped = new ArrayList<Object> ();
                while (true)
                {
                    Object ele = theStack.pop();

                    if (ele instanceof String)
                    {
                        if (((String)ele).equals("("))
                            break;
                    }

                    popped.add(0, ele);
                }

                if (popped.size() == 1)
                {
                    Constant toAdd = new Constant((String) popped.get(0));
                    theStack.push(toAdd);
                }
                else
                {
                    if (theStack.empty())
                    {
                        // Base level predicate
                        Unifiable terms [] = new Unifiable [popped.size() - 1];
                        String predicateName = (String) popped.get(0);
                        popped.remove(0);
                        int index = 0;
                        for (Object term : popped)
                        {
                            if (term instanceof String)
                            {
                                terms[index] = new Variable((String) term);
                            }
                            else if (term instanceof Constant)
                            {
                                terms[index] = (Constant) term;
                            }
                            else if (term instanceof Function)
                            {
                                terms[index] = (Function) term;
                            }

                            ++index;
                        }
                        
                        toReturn.add(new Predicate(predicateName, terms));
                    }
                    else
                    {
                        // Some function
                        Unifiable terms [] = new Unifiable [popped.size() - 1];
                        String functionName = (String) popped.get(0);
                        popped.remove(0);
                        int index = 0;
                        for (Object term : popped)
                        {
                            if (term instanceof String)
                            {
                                terms[index] = new Variable((String) term);
                            }
                            else if (term instanceof Constant)
                            {
                                terms[index] = (Constant) term;
                            }
                            else if (term instanceof Function)
                            {
                                terms[index] = (Function) term;
                            }

                            ++index;
                        }
                        
                        theStack.push(new Function(functionName, terms));
                    }
                }
            }
            else
            {
                int firstSpace = literalsString.indexOf(" ", i),
                    endBracket = literalsString.indexOf(")", i);
                int endIndex = Math.min(firstSpace, endBracket);
                if (firstSpace == -1)
                    endIndex = endBracket;
                else if (endBracket == -1)
                    endIndex = firstSpace;

                String toPush = literalsString.substring(i, endIndex);
                i = endIndex - 1;
                theStack.push(toPush);
            }
        }

        return toReturn;
    }

    public Clause (String clauseString)
    {
        clauseString = clauseString.replaceAll("nil", "( )");
        clauseNumber = Integer.parseInt(clauseString.split("[(]") [1].trim());
        
        String literalStrings [] = getLiteralsStrings(clauseString);
        positiveLiterals = convertStringToLiterals(literalStrings [0]);
        negativeLiterals = convertStringToLiterals(literalStrings [1]);
    }

    public Clause (int clauseNumber, ArrayList<Unifiable> positives, ArrayList<Unifiable> negatives)
    {
        this.clauseNumber = clauseNumber;
        this.positiveLiterals = positives;
        this.negativeLiterals = negatives;
    }

    public int getNumber()
    {
        return clauseNumber;
    }

    public String toString()
    {
        String toReturn = "(" + clauseNumber + " (";
        for (Unifiable positiveLiteral: positiveLiterals)
        {
            toReturn += positiveLiteral + " ";
        }
        toReturn += ") (";

        for (Unifiable negativeLiteral: negativeLiterals)
        {
            toReturn += negativeLiteral + " ";
        }

        toReturn += ") )";
        return toReturn;
    }

    public ArrayList<Unifiable> getPositiveTerms()
    {
        return positiveLiterals;
    }

    public ArrayList<Unifiable> getNegativeTerms()
    {
        return negativeLiterals;
    }

    public void negateClause()
    {
        // Negate by interchanging positive and negative literals
        ArrayList<Unifiable> temp = new ArrayList<Unifiable> (positiveLiterals);
        positiveLiterals.clear();
        positiveLiterals.addAll(negativeLiterals);
        negativeLiterals.clear();
        negativeLiterals.addAll(temp);
    }

    public boolean isEmpty()
    {
        return (positiveLiterals.size() == 0 && negativeLiterals.size() == 0);
    }

    public boolean isEqualTo(Clause toCheck)
    {
        // Check sizes
        if (toCheck.getPositiveTerms().size() != positiveLiterals.size() || toCheck.getNegativeTerms().size() != negativeLiterals.size())
            return false;

        for (int i = 0; i < positiveLiterals.size(); ++i)
        {
            Predicate p1 = (Predicate)toCheck.getPositiveTerms().get(i), p2 = (Predicate)positiveLiterals.get(i);
            var unifier = p1.unify(p2, new SubstitutionSet());
            if (unifier != null)
            {
                p1 = (Predicate) p1.replaceVariables(unifier);
                p2 = (Predicate) p2.replaceVariables(unifier);
            }
            if (!p1.isEqualTo(p2))
                return false;
        }

        for (int i = 0; i < negativeLiterals.size(); ++i)
        {
            Predicate p1 = (Predicate)toCheck.getNegativeTerms().get(i), p2 = (Predicate)negativeLiterals.get(i);
            var unifier = p1.unify(p2, new SubstitutionSet());
            if (unifier != null)
            {
                p1 = (Predicate) p1.replaceVariables(unifier);
                p2 = (Predicate) p2.replaceVariables(unifier);
            }
            if (!p1.isEqualTo(p2))
                return false;
        }

        return true;
    }
}