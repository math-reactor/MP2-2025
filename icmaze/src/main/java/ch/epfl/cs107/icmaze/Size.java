package ch.epfl.cs107.icmaze;

public final class Size {
    
    private Size(){
    }
    
    public static final int SMALL = 8;
    public static final int MEDIUM = 16;
    public static final int LARGE = 32;

    public static int getSize(String areaSize){
        if (areaSize == "SmallArea"){
            return SMALL;
        } else if (areaSize == "MediumArea") {
            return MEDIUM;
        } else if (areaSize == "LargeArea") {
            return LARGE;
        }
        return 0;
    }
}
