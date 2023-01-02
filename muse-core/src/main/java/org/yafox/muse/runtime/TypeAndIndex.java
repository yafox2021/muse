/**
 * 
 */
package org.yafox.muse.runtime;

/**
 * @author 86158
 *
 */
public class TypeAndIndex {

    private int index;

    private String type;

    private String name;

    private int xload;

    private int xstore;

    private int xreturn;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXload() {
        return xload;
    }

    public void setXload(int xload) {
        this.xload = xload;
    }

    public int getXstore() {
        return xstore;
    }

    public void setXstore(int xstore) {
        this.xstore = xstore;
    }

    public int getXreturn() {
        return xreturn;
    }

    public void setXreturn(int xreturn) {
        this.xreturn = xreturn;
    }

}
