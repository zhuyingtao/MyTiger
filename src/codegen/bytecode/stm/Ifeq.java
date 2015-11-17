package codegen.bytecode.stm;

import codegen.bytecode.Visitor;
import util.Label;

public class Ifeq extends T {

    public Label l;

    public Ifeq(Label l) {
        this.l = l;
    }

    @Override
    public void accept(Visitor v) {
        // TODO Auto-generated method stub
        v.visit(this);
    }

}
