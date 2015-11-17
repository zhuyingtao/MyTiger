package cfg.stm;

import cfg.Visitor;

import java.util.ArrayList;

public class Block extends T {

    public ArrayList<Object> stmOrTransfer;

    public Block(ArrayList<Object> stmOrTransfer) {
        // TODO Auto-generated constructor stub
        this.stmOrTransfer = stmOrTransfer;
    }

    @Override
    public void accept(Visitor v) {
        // TODO Auto-generated method stub

    }

}
