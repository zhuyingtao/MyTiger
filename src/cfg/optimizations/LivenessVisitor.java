package cfg.optimizations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LivenessVisitor implements cfg.Visitor {
    // gen, kill for one statement
    private java.util.HashSet<String> oneStmGen;
    private java.util.HashSet<String> oneStmKill;

    // gen, kill for one transfer
    private java.util.HashSet<String> oneTransferGen;
    private java.util.HashSet<String> oneTransferKill;

    // gen, kill for statements
    private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmGen;
    private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmKill;

    // gen, kill for transfers
    private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferGen;
    private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferKill;

    // gen, kill for blocks
    private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockGen;
    private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockKill;

    // liveIn, liveOut for blocks
    private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveIn;
    private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveOut;

    // liveIn, liveOut for statements
    public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveIn;
    public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveOut;

    // liveIn, liveOut for transfer
    public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveIn;
    public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveOut;

    // As you will walk the tree for many times, so
    // it will be useful to recored which is which:
    enum Liveness_Kind_t {
        None, StmGenKill, BlockGenKill, BlockInOut, StmInOut,
    }

    private Liveness_Kind_t kind = Liveness_Kind_t.None;

    public LivenessVisitor() {
        this.oneStmGen = new java.util.HashSet<>();
        this.oneStmKill = new java.util.HashSet<>();

        this.oneTransferGen = new java.util.HashSet<>();
        this.oneTransferKill = new java.util.HashSet<>();

        this.stmGen = new java.util.HashMap<>();
        this.stmKill = new java.util.HashMap<>();

        this.transferGen = new java.util.HashMap<>();
        this.transferKill = new java.util.HashMap<>();

        this.blockGen = new java.util.HashMap<>();
        this.blockKill = new java.util.HashMap<>();

        this.blockLiveIn = new java.util.HashMap<>();
        this.blockLiveOut = new java.util.HashMap<>();

        this.stmLiveIn = new java.util.HashMap<>();
        this.stmLiveOut = new java.util.HashMap<>();

        this.transferLiveIn = new java.util.HashMap<>();
        this.transferLiveOut = new java.util.HashMap<>();

        this.kind = Liveness_Kind_t.None;
    }

    // /////////////////////////////////////////////////////
    // utilities

    private java.util.HashSet<String> getOneStmGenAndClear() {
        java.util.HashSet<String> temp = this.oneStmGen;
        this.oneStmGen = new java.util.HashSet<>();
        return temp;
    }

    private java.util.HashSet<String> getOneStmKillAndClear() {
        java.util.HashSet<String> temp = this.oneStmKill;
        this.oneStmKill = new java.util.HashSet<>();
        return temp;
    }

    private java.util.HashSet<String> getOneTransferGenAndClear() {
        java.util.HashSet<String> temp = this.oneTransferGen;
        this.oneTransferGen = new java.util.HashSet<>();
        return temp;
    }

    private java.util.HashSet<String> getOneTransferKillAndClear() {
        java.util.HashSet<String> temp = this.oneTransferKill;
        this.oneTransferKill = new java.util.HashSet<>();
        return temp;
    }

    // /////////////////////////////////////////////////////
    // operand
    @Override
    public void visit(cfg.operand.Int operand) {
        return;
    }

    @Override
    public void visit(cfg.operand.Var operand) {
        if (fromTransfer) {
            this.oneTransferGen.add(operand.id);
        } else {
            this.oneStmGen.add(operand.id);
        }
        return;
    }

    // statements
    @Override
    public void visit(cfg.stm.Add s) {
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.left.accept(this);
        s.right.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.InvokeVirtual s) {
        this.oneStmKill.add(s.dst);
        this.oneStmGen.add(s.obj);
        for (cfg.operand.T arg : s.args) {
            arg.accept(this);
        }
        return;
    }

    @Override
    public void visit(cfg.stm.Lt s) {
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.left.accept(this);
        s.right.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.Move s) {
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.src.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.NewObject s) {
        this.oneStmKill.add(s.dst);
        return;
    }

    @Override
    public void visit(cfg.stm.Print s) {
        s.arg.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.Sub s) {
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.left.accept(this);
        s.right.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.Times s) {
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.left.accept(this);
        s.right.accept(this);
        return;
    }

    // transfer
    @Override
    public void visit(cfg.transfer.If s) {
        // Invariant: accept() of operand modifies "gen"
        fromTransfer = true;
        s.operand.accept(this);
        fromTransfer = false;
        return;
    }

    @Override
    public void visit(cfg.transfer.Goto s) {
        return;
    }

    boolean fromTransfer = false;

    @Override
    public void visit(cfg.transfer.Return s) {
        // Invariant: accept() of operand modifies "gen"
        fromTransfer = true;
        s.operand.accept(this);
        fromTransfer = false;
        return;
    }

    // type
    @Override
    public void visit(cfg.type.Class t) {
    }

    @Override
    public void visit(cfg.type.Int t) {
    }

    @Override
    public void visit(cfg.type.IntArray t) {
    }

    // dec
    @Override
    public void visit(cfg.dec.Dec d) {
    }

    // utility functions:
    private void calculateStmTransferGenKill(cfg.block.Block b) {

        // GEN[s] : The set of variables that are used in s before any
        // assignment.(in the right of "="),also known as Use;
        // KILL[s] : The set of variables that are assigned a value in s.
        // (in the left of "="),also known as Define;
        for (cfg.stm.T s : b.stms) {
            this.oneStmGen = new java.util.HashSet<>();
            this.oneStmKill = new java.util.HashSet<>();
            s.accept(this);
            // stmGen and stmKill store the stms of all blocks;
            this.stmGen.put(s, this.oneStmGen);
            this.stmKill.put(s, this.oneStmKill);
            if (control.Control.isTracing("liveness.step1")) {
                System.out.print("\ngen, kill for statement: " + s.toString());
                System.out.print("\ngen is:");
                for (String str : this.oneStmGen) {
                    System.out.print(str + ", ");
                }
                System.out.print("\nkill is:");
                for (String str : this.oneStmKill) {
                    System.out.print(str + ", ");
                }
            }
        }
        this.oneTransferGen = new java.util.HashSet<>();
        this.oneTransferKill = new java.util.HashSet<>();
        b.transfer.accept(this);
        // transferGen and transferKill store the transfers of all blocks;
        this.transferGen.put(b.transfer, this.oneTransferGen);
        this.transferKill.put(b.transfer, this.oneTransferKill);
        if (control.Control.isTracing("liveness.step1")) {
            System.out.print("\ngen, kill for transfer:"
                    + b.transfer.toString());
            System.out.print("\ngen is:");
            for (String str : this.oneTransferGen) {
                System.out.print(str + ", ");
            }
            System.out.println("\nkill is:");
            for (String str : this.oneTransferKill) {
                System.out.print(str + ", ");
            }
        }
        return;
    }

    private void calculateBlockGenKill(cfg.block.Block b) {
        // get the gen and kill for one block from the stmGen,
        // transferGen and stmKill,transferKill;
        HashSet<String> oneBlockGen = new HashSet<>();
        HashSet<String> oneBlockKill = new HashSet<>();

        // kill=kill[n]+kill[n-1]+...+kill[0];
        // gen=gen[n]+(gen[n-1]-kill[n])+(gen[n-2]-kill[n-1]-kill[n])+...
        // +(gen[0]-kill[1]-kill[2]-...-kill[n]);

        for (int i = 0; i < b.stms.size(); i++) {
            cfg.stm.T stm = b.stms.get(i);
            // get kill[n-1]...kill[0]
            HashSet<String> onestmKill = this.stmKill.get(stm);
            for (String string : onestmKill) {
                oneBlockKill.add(string);
            }
            // get gen[n-1]...gen[0]
            HashSet<String> onestmGen = this.stmGen.get(stm);
            for (String string : onestmGen) {
                if (oneBlockKill.contains(string))
                    continue;
                oneBlockGen.add(string);
            }

        }
        // get kill[n]
        HashSet<String> onetransferKill = this.transferKill.get(b.transfer);
        for (String string : onetransferKill) {
            oneBlockKill.add(string);
        }
        // get gen[n]
        HashSet<String> onetransferGen = this.transferGen.get(b.transfer);
        for (String string : onetransferGen) {
            if (oneBlockKill.contains(string))
                continue;
            oneBlockGen.add(string);
        }

        // #2: put the oneBlockGen and oneBlockKill to the whole map;
        blockGen.put(b, oneBlockGen);
        blockKill.put(b, oneBlockKill);

        if (control.Control.isTracing("liveness.step2")) {
            System.out.print("\ngen, kill for Block:" + b.label.toString());
            System.out.print("\ngen is:");
            for (String str : oneBlockGen) {
                System.out.print(str + ", ");
            }
            System.out.print("\nkill is:");
            for (String str : oneBlockKill) {
                System.out.print(str + ", ");
            }
            System.out.println();
        }
    }

    private void calculateBlockInOut(cfg.block.Block b) {
        // liveIn=liveUse V (liveOut - Def);
        // liveOut= V liveIn
        HashSet<String> oneLiveIn = new HashSet<>();
        HashSet<String> oneLiveOut = new HashSet<>();
        if (b.transfer instanceof cfg.transfer.Return) {
            oneLiveOut = null;
        } else if (b.transfer instanceof cfg.transfer.Goto) {
            util.Label label = ((cfg.transfer.Goto) b.transfer).label;
            for (int i = 0; i < blockLiveIn.size(); i++) {
            }
        }

    }

    private void calculateStmInOut(cfg.block.Block b) {

    }

    // block
    @Override
    public void visit(cfg.block.Block b) {
        switch (this.kind) {
            case StmGenKill:
                calculateStmTransferGenKill(b);
                break;
            case BlockGenKill:
                calculateBlockGenKill(b);
                break;
            case BlockInOut:
                calculateBlockInOut(b);
                break;
            case StmInOut:
                calculateStmInOut(b);
                break;
            default:
                // Your code here:
                System.out.println("Error : Liveness Kind Error !");
                return;
        }
    }

    // method
    @Override
    public void visit(cfg.method.Method m) {
        // Four steps:
        // Step 1: calculate the "gen" and "kill" sets for each
        // statement and transfer
        this.kind = Liveness_Kind_t.StmGenKill;
        for (cfg.block.T block : m.blocks) {
            block.accept(this);
        }

        // Step 2: calculate the "gen" and "kill" sets for each block.
        // For this, you should visit statements and transfers in a
        // block in a reverse order(block order).
        // Your code here:
        this.kind = Liveness_Kind_t.BlockGenKill;
        for (int i = m.blocks.size() - 1; i >= 0; i--) {
            m.blocks.get(i).accept(this);
        }

        // Step 3: calculate the "liveIn" and "liveOut" sets for each block
        // Note that to speed up the calculation, you should first
        // calculate a reverse topo-sort order of the CFG blocks, and
        // crawl through the blocks in that order.
        // And also you should loop until a fix-point is reached.
        // Your code here:

        this.kind = Liveness_Kind_t.BlockInOut;
        // reverse topoSort
        LinkedList<cfg.block.T> newBlocks = this.topoSort(m.blocks);
        for (cfg.block.T t : newBlocks) {
            t.accept(this);
        }

        // Step 4: calculate the "liveIn" and "liveOut" sets for each
        // statement and transfer
        // Your code here:
        this.kind = Liveness_Kind_t.StmInOut;
        for (cfg.block.T block : newBlocks) {
            block.accept(this);
        }
    }

    @Override
    public void visit(cfg.mainMethod.MainMethod m) {
        // Four steps:
        // Step 1: calculate the "gen" and "kill" sets for each
        // statement and transfer
        this.kind = Liveness_Kind_t.StmGenKill;
        for (cfg.block.T block : m.blocks) {
            block.accept(this);
        }

        // Step 2: calculate the "gen" and "kill" sets for each block.
        // For this, you should visit statements and transfers in a
        // block in a reverse order.
        // Your code here:
        this.kind = Liveness_Kind_t.BlockGenKill;
        for (int i = m.blocks.size() - 1; i >= 0; i--) {
            m.blocks.get(i).accept(this);
        }

        // Step 3: calculate the "liveIn" and "liveOut" sets for each block
        // Note that to speed up the calculation, you should first
        // calculate a reverse topo-sort order of the CFG blocks, and
        // crawl through the blocks in that order.
        // And also you should loop until a fix-point is reached.
        // Your code here:
        this.kind = Liveness_Kind_t.BlockInOut;
        // reverse topoSort
        LinkedList<cfg.block.T> newBlocks = this.topoSort(m.blocks);
        for (cfg.block.T t : newBlocks) {
            t.accept(this);
        }

        // Step 4: calculate the "liveIn" and "liveOut" sets for each
        // statement and transfer
        // Your code here:
        this.kind = Liveness_Kind_t.StmInOut;
        for (cfg.block.T block : newBlocks) {
            block.accept(this);
        }
    }

    // vtables
    @Override
    public void visit(cfg.vtable.Vtable v) {
    }

    // class
    @Override
    public void visit(cfg.classs.Class c) {
    }

    // program
    @Override
    public void visit(cfg.program.Program p) {
        p.mainMethod.accept(this);
        for (cfg.method.T mth : p.methods) {
            mth.accept(this);
        }
        return;
    }

    @Override
    public void visit(cfg.stm.And s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.left.accept(this);
        s.right.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.ArraySelect s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.array.accept(this);
        s.index.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.Length s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        s.array.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.NewIntArray s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.exp.accept(this);
        return;

    }

    @Override
    public void visit(cfg.stm.Not s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.exp.accept(this);
        return;
    }

    @Override
    public void visit(cfg.stm.MoveArray s) {
        // TODO Auto-generated method stub
        this.oneStmKill.add(s.dst);
        // Invariant: accept() of operand modifies "gen"
        s.index.accept(this);
        s.exp.accept(this);
        return;
    }

    public LinkedList<cfg.block.T> topoSort(LinkedList<cfg.block.T> blocks) {
        LinkedList<cfg.block.T> newBlocks = new LinkedList<>();
        for (cfg.block.T t : blocks) {
            newBlocks.add(t);
        }

        // calculate the degree of all the blocks;
        HashMap<cfg.block.T, Integer> degree = new HashMap<cfg.block.T, Integer>();
        for (cfg.block.T t : newBlocks) {
            cfg.block.Block b = (cfg.block.Block) t;
            if (b.transfer instanceof cfg.transfer.If) {
                cfg.transfer.If transfer = (cfg.transfer.If) b.transfer;
                for (cfg.block.T t2 : newBlocks) {
                    cfg.block.Block temp = (cfg.block.Block) t2;
                    if (transfer.truee.toString().equals(temp.label.toString())) {
                        Integer integer = degree.get(temp);
                        int i = 0;
                        if (integer != null)
                            i = integer;
                        degree.put(temp, ++i);
                        break;
                    }
                }

                for (cfg.block.T t2 : newBlocks) {
                    cfg.block.Block temp = (cfg.block.Block) t2;
                    if (transfer.falsee.toString()
                            .equals(temp.label.toString())) {
                        Integer integer = degree.get(temp);
                        int i = 0;
                        if (integer != null)
                            i = integer;
                        degree.put(temp, ++i);
                        break;
                    }
                }
            } else if (b.transfer instanceof cfg.transfer.Goto) {
                cfg.transfer.Goto transfer = (cfg.transfer.Goto) b.transfer;
                for (cfg.block.T t2 : newBlocks) {
                    cfg.block.Block temp = (cfg.block.Block) t2;
                    if (transfer.label.toString().equals(temp.label.toString())) {
                        Integer integer = degree.get(temp);
                        int i = 0;
                        if (integer != null)
                            i = integer;
                        degree.put(temp, ++i);
                        break;
                    }
                }
            }
        }
        return newBlocks;
    }
}
