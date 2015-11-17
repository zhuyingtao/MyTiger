package codegen.dalvik.stm;

import codegen.dalvik.Visitor;
import util.Label;

public class Goto32 extends T {
    public Label l;

    public Goto32(Label l) {
        this.l = l;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
