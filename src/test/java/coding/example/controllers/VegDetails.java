package coding.example.controllers;


public class VegDetails {
    String vegName;
    String displayName;
    String imageName;
    int expected;

    VegDetails(String vegName, String imageName){
        this(vegName, vegName, imageName, 0);
    }

    VegDetails(String vegName, String displayName, String imageName){
        this(vegName, displayName, imageName, 0);
    }

    public VegDetails(String vegName, String displayName, String imageName, int expected) {
        this.vegName = vegName;
        this.displayName = displayName;
        this.imageName = imageName;
        this.expected = expected;
    }

    public int incExpected(){
        expected++;
        return expected;
    }

    public VegDetails initExpected(){
        expected = 0;
        return this;
    }

    public String getVegName() {
        return vegName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageName() {
        return imageName;
    }

    public int getExpected() {
        return expected;
    }
}
