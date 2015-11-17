package codegen.dalvik.stm;

import codegen.dalvik.Visitor;
import util.Label;

public class Iflt extends T {
    public String left, right;
    public Label l;

    public Iflt(String left, String right, Label l) {
        this.left = left;
        this.right = right;
        this.l = l;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
