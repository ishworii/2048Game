package core;

public class Tile {
    private int value;
    private boolean merged;

    Tile(){
        this.value = 0;
        this.merged = false;
    }
    Tile(int newValue){
        this.value = newValue;
        this.merged = false;
    }

    public boolean isEmpty(){
        return this.value == 0;
    }

    public int getValue(){
        return this.value;
    }

    public void setValue(int newValue){
        this.value = newValue;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }
    @Override
    public String toString(){
        return this.value == 0 ? "." : String.valueOf(this.value);
    }
}
