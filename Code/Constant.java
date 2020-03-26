public class Constant implements Unifiable
{
    private String printName = null;
    private static int nextId = 1;
    private int id;
    
    public Constant()
    {
        this.id = nextId++;
    }
    public Constant(String printName)
    {
        this();
        this.printName = printName;
    }
    
    public String toString()
    {
        if (printName!= null)
            return "(" + printName + ")";
        return "constant_" + id;
    }
    
    public SubstitutionSet unify(Unifiable exp, SubstitutionSet s)
    {
        if (this == exp)
           return new SubstitutionSet(s);
        if (exp instanceof Variable)
            return exp.unify(this, s);
        return null;
    }
    
    public PCExpression replaceVariables(SubstitutionSet s)
    {
        return this;
    }
}