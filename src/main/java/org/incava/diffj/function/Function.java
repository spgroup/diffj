package org.incava.diffj.function;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.diffj.code.Code;
import org.incava.diffj.element.CodedElement;
import org.incava.diffj.element.Differences;
import org.incava.diffj.params.Parameters;
import org.incava.pmdx.FunctionUtil;

public abstract class Function extends CodedElement {
    private final AbstractJavaNode node;
    
    public Function(AbstractJavaNode node) {
        super(node);
        this.node = node;
    }

    protected ASTNameList getThrowsList() {
        return FunctionUtil.getThrowsList(node);
    }

    abstract protected ASTFormalParameters getFormalParameters();

    protected Parameters getParameters() {
        return new Parameters(getFormalParameters());
    }

    protected int compareParameters(Function toFunction, Differences differences) {
        String old = differences.toString();
        Parameters fromParams = getParameters();
        Parameters toParams = toFunction.getParameters();
        fromParams.diff(toParams, differences);
        return old.compareTo(differences.toString());
    }
    
    protected int compareThrows(Function toFunction, Differences differences) {
        String old = differences.toString();
        Throws fromThrows = getThrows();
        Throws toThrows = toFunction.getThrows();
        fromThrows.diff(toThrows, differences);
        return old.compareTo(differences.toString());
    }

    protected Throws getThrows() {
        return new Throws(node, getThrowsList());
    }
}
