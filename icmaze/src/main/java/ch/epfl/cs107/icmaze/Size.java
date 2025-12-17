package ch.epfl.cs107.icmaze;

public final class Size {
    
    private Size(){
    }
    
    public static final int SMALL = 8;
    public static final int MEDIUM = 16;
    public static final int LARGE = 32;

    /**
    *method to get the area's size based on its name
     * @param areaSize the area's name
     * @return the quadratic area's size, an int
    */
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
