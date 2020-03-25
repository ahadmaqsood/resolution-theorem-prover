public class Function implements Unifiable
{
    private Unifiable[] terms; // first element is the function name
    private String functionName;

    public Function(String functionName, Unifiable... args)
    {
        this.terms = args;
        this.functionName = functionName;
    }

    public String toString()
    {
        String s = null;
        for (Unifiable p : terms)
        {
            if (s == null)
            s = p.toString();
            else
            s += " " + p;
        }
        
        if (s == null)
        return "null";

        return functionName + " (" + s + ")";
    }

    public int length()
    {
        return terms.length;
    }
    
    public Unifiable getTerm(int index)
    {
        return terms[index];
    }

    public SubstitutionSet unify(Unifiable p, SubstitutionSet s)
    {
        if (p instanceof Function)
        {
            Function s2 = (Function) p;
            if (this.length() != s2.length())
            return null;
            
            SubstitutionSet sNew = new SubstitutionSet(s);
            for (int i = 0; i < this.length(); i++)
            {
                sNew = this.getTerm(i).unify(s2.getTerm(i), sNew);
                if(sNew == null)
                return null;
            }
            return sNew;
        }
        
        if(p instanceof Variable)
        return p.unify(this, s);
        
        return null;
    }
    
    public PCExpression replaceVariables(SubstitutionSet s)
    {
        Unifiable[] newTerms = new
        Unifiable[terms.length];
        for(int i = 0; i < length(); i++)
        newTerms[i] = (Unifiable)terms[i].replaceVariables(s);
        return new Function(functionName, newTerms);
    }
}