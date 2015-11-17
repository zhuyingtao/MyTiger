package codegen.dalvik.stm;

import codegen.dalvik.Visitor;
import util.Label;

public class Ifnez extends T {
    public String left;
    public Label l;

    public Ifnez(String left, Label l) {
        this.left = left;
        this.l = l;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
