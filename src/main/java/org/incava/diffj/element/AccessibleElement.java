package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.pmdx.Node;

public class AccessibleElement extends Element {
    public AccessibleElement(AbstractJavaNode node) {
        super(node);
    }

    public int compareAccess(AccessibleElement toElement, Differences differences) {
        String old = differences.toString();
        Access acc = new Access(getParent());
        acc.diff(toElement.getParent(), differences);
        return old.compareTo(differences.toString());
    }

    public AbstractJavaNode getParent() {
        Node<AbstractJavaNode> node = Node.of(getNode());
        return node.getParent();
    }
}
