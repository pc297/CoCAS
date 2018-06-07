package cocas;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pierre Cauchy
 */
import org.rosuda.JRI.*;
public class EvalRExpression extends Thread
{
    Rengine rengine;
    String expression;
    public EvalRExpression(Rengine re,String s)
    {
        rengine = re;
        expression = s;
    }
    
    public void run()
    {
        
        rengine.eval(expression);
        
        
    }
}
