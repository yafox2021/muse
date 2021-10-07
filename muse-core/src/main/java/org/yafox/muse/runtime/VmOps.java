/**
 * 
 */
package org.yafox.muse.runtime;

import org.objectweb.asm52.Opcodes;

/**
 * @author 86158
 *
 */
public enum VmOps {

    I(Opcodes.ILOAD, Opcodes.ISTORE, Opcodes.IRETURN), 
    B(Opcodes.ILOAD, Opcodes.ISTORE, Opcodes.IRETURN),
    C(Opcodes.ILOAD, Opcodes.ISTORE, Opcodes.IRETURN),
    S(Opcodes.ILOAD, Opcodes.ISTORE, Opcodes.IRETURN),
    Z(Opcodes.ILOAD, Opcodes.ISTORE, Opcodes.IRETURN),
    J(Opcodes.LLOAD, Opcodes.LSTORE, Opcodes.LRETURN),
    F(Opcodes.FLOAD, Opcodes.FSTORE, Opcodes.FRETURN),
    D(Opcodes.DLOAD, Opcodes.DSTORE, Opcodes.DRETURN),
    A(Opcodes.ALOAD, Opcodes.ASTORE, Opcodes.ARETURN);
    
    public int xLoad;
    
    public int xStore;
    
    public int xReturn;

    private VmOps(int xLoad, int xStore, int xReturn) {
        this.xLoad = xLoad;
        this.xStore = xStore;
        this.xReturn = xReturn;
        
    }
    
}
