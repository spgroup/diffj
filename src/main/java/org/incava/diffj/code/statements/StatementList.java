package org.incava.diffj.code.statements;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.code.Tkn;
import org.incava.diffj.code.TokenList;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;
import static org.incava.ijdk.util.IUtil.*;

public class StatementList {
    protected static final boolean log = Boolean.getBoolean("diffj.debug.statementlist");

    private final List<Statement> statements;
    private final Block blk;
    
    public StatementList(Block blk) {
        this.blk = blk;
        this.statements = blk.getStatements();
        tr.Ace.stack("statements", statements);
    }

    public Block getBlock() {
        return blk;
    }

    public String toString() {
        return statements.toString();
    }

    public Statement get(int idx) {
        log("idx", idx);
        return ListExt.get(statements, idx);
    }

    public List<TokenList> getTokenLists() {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : statements) {
            tokenLists.add(stmt.getTokenList());
        }
        return tokenLists;
    }

    public TokenList getAsTokenList(Integer from, Integer to) {
        List<TokenList> tokenLists = getTokenLists();
        if (to == Difference.NONE) {
            return tokenLists.get(from);
        }
        
        int idx = from;
        TokenList list = tokenLists.get(idx++);
        while (idx <= to) {
            list.add(tokenLists.get(idx++));
        }

        return list;
    }

    /**
     * Returns the range for the first token of the statement at the given
     * index.
     */
    public LocationRange getRangeAt(int idx) {
        tr.Ace.log("this", this);
        log("idx", idx);
        tr.Ace.log("statements", statements);
        Statement stmt = get(idx);
        log("stmt", stmt);
        if (stmt == null) {
            tr.Ace.onRed("stmt", stmt);
            tr.Ace.onRed("blk", blk);
            Token token = blk.getLastToken();
            List<Token> tokens = list(token);
            tr.Ace.log("tokens", tokens);
            TokenList tokenList = new TokenList(tokens);
            LocationRange rg = tokenList.getTokenLocationRange(-1);
            tr.Ace.log("rg", rg);
            return rg;
        }
        TokenList tokenList = stmt.getTokenList();
        log("tokenList", tokenList);
        return tokenList.getTokenLocationRange(0);
    }

    /**
     * Returns the range for the given statements within from and to, inclusive.
     */
    public LocationRange getRangeOf(int from, int to) {
        Statement fromStmt = get(from);
        Statement toStmt = get(to);
        Tkn startTkn = fromStmt.getTkn(0);
        Tkn endTkn = toStmt.getTkn(-1);
        return startTkn.getLocationRange(endTkn);
    }

    public void log(String msg, Object obj) {
        if (log) {
            tr.Ace.log(msg, obj);
        }
    }
}