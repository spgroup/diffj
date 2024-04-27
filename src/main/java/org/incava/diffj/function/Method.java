package org.incava.diffj.function;

import java.util.StringTokenizer;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration; 
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diffj.code.Block;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.params.Parameters;
import org.incava.diffj.util.Messages;
import org.incava.diffj.app.MethodDiff;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.Node;

public class Method extends Function implements Diffable<Method> {
    public static final Message METHOD_REMOVED = new Message("method removed: {0}");
    public static final Message METHOD_ADDED = new Message("method added: {0}");
    public final static Messages METHOD_MSGS = new Messages(METHOD_ADDED, null, METHOD_REMOVED);

    public static final Message RETURN_TYPE_CHANGED = new Message("return type changed from {0} to {1}");
    public static final Message METHOD_BLOCK_ADDED = new Message("method block added");
    public static final Message METHOD_BLOCK_REMOVED = new Message("method block removed");

    private final ASTClassOrInterfaceDeclaration clasz;
    private final ASTMethodDeclaration method;
    private final Block block;
    private final String name;

    enum BlockChanged { CHANGED, ADDED, REMOVED, NONE }

    public Method(ASTMethodDeclaration method) {
        super(method);
        this.method = method;
        this.clasz = classDeclaration();
        ASTBlock astBlk = Node.of(method).findChild(ASTBlock.class);
        this.name = MethodUtil.getFullName(method);
        this.block = astBlk == null ? null : new Block(name, astBlk);
    }

    public void diff(Method toMethod, Differences differences) {
        Boolean changed = false;
        changed = (compareAccess(toMethod, differences) != 0) || changed;
        changed = (compareModifiers(toMethod, differences) != 0) || changed;
        changed = (compareReturnTypes(toMethod, differences) != 0) || changed;
        changed = (compareParameters(toMethod, differences) != 0) || changed;
        changed = (compareThrows(toMethod, differences) != 0) || changed;
        changed = (!compareBodies(toMethod, differences).equals(BlockChanged.NONE)) || changed;
        
        if (changed) {
            MethodDiff.instance().addChangedMethod("(changed)" + ppMethodName());
        }
    }

    protected ASTFormalParameters getFormalParameters() {
        return MethodUtil.getParameters(method);
    }

    protected Block getBlock() {
        return block;
    }

    protected AbstractJavaNode getReturnType() {
        return Node.of(method).findChild();
    }

    /**
     * This returns the full method name/signature, including parameters.
     */
    public String getName() {
        return name;
    }

    protected MethodModifiers getModifiers() {
        return new MethodModifiers(getParent());
    }

    protected int compareModifiers(Method toMethod, Differences differences) {
        String old = differences.toString();
        MethodModifiers fromMods = getModifiers();
        MethodModifiers toMods = toMethod.getModifiers();
        fromMods.diff(toMods, differences);
        return old.compareTo(differences.toString());
    }


    protected boolean hasBlock() {
        return block != null;
    }

    protected BlockChanged compareBodies(Method toMethod, Differences differences) {
        BlockChanged res = BlockChanged.NONE;
        if (hasBlock()) {
            if (toMethod.hasBlock()) {
                if(block.compareCode(toMethod.block, differences) != 0) {
                    res = BlockChanged.CHANGED;
                }
            }
            else {
                differences.changed(this, toMethod, METHOD_BLOCK_REMOVED);
                res = BlockChanged.REMOVED;
            }
        }
        else if (toMethod.hasBlock()) {
            differences.changed(this, toMethod, METHOD_BLOCK_ADDED);
            res = BlockChanged.ADDED;
        }
        return res;
    }

    protected int compareReturnTypes(Method toMethod, Differences differences) {
        Node<AbstractJavaNode> fromRetType = Node.of(getReturnType());
        Node<AbstractJavaNode> toRetType   = Node.of(toMethod.getReturnType());
        String     fromRetTypeStr = fromRetType.toString();
        String     toRetTypeStr   = toRetType.toString();

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            differences.changed(fromRetType.astNode(), toRetType.astNode(), RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
            return 1;
        }
        return 0;
    }

    /**
     * This returns only the method name, without the parameters.
     */
    public String getMethodName() {
        ASTMethodDeclarator decl = MethodUtil.getDeclarator(method);
        return Node.of(decl).getFirstToken().image;
    }

    public double getMatchScore(Method toMethod) {
        String fromName = getMethodName();
        String toName = toMethod.getMethodName();

        if (!fromName.equals(toName)) {
            return 0;
        }

        Parameters fromParams = getParameters();
        Parameters toParams = toMethod.getParameters();

        return fromParams.getMatchScore(toParams);
    }

    public Message getAddedMessage() {
        return METHOD_MSGS.getAdded();
    }

    public Message getRemovedMessage() {
        return METHOD_MSGS.getDeleted();
    }

    private ASTClassOrInterfaceDeclaration classDeclaration() {
        ASTClassOrInterfaceBodyDeclaration bodyDeclaration = (ASTClassOrInterfaceBodyDeclaration)(Node.of(method).getParent());
        ASTClassOrInterfaceBody body = (ASTClassOrInterfaceBody)(Node.of(bodyDeclaration).getParent());
        return (ASTClassOrInterfaceDeclaration)(Node.of(body).getParent());
    }

    public String ppMethodName() {
        Node<AbstractJavaNode> fromRetType = Node.of(getReturnType());
        String qualifiedName = clasz.getQualifiedName().toString();
        StringTokenizer tokenizer = new StringTokenizer(qualifiedName, ".");
        if(tokenizer.countTokens() == 1) {
            qualifiedName = tokenizer.nextToken();
        }
        
        return "<" + qualifiedName + " " + fromRetType.toString() + " " + name + ">";
    }
}
