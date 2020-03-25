public interface Unifiable extends PCExpression
{
    public SubstitutionSet unify(Unifiable exp, SubstitutionSet s);
}