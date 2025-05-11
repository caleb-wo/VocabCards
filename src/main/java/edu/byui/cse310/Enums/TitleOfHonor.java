package edu.byui.cse310.Enums;

public enum TitleOfHonor {
    /*
     * These list out the different 'Titles of Honor' a user can have.
     * Users earn a new title after reached a certain point number.
     * */
    GUPPIE(0, "Guppie"),
    NOVICE(10, "Novice"),
    MR_CONSISTENT(20, "Mr. Consistent"),
    JR_DEVELOPER(30, "Jr. Developer"),
    SUPER_CODER(50, "Super Coder"),
    PLANETARY_PROGRAMMER(100, "Planetary Programmer"),
    SKYNET(200, "Skynet"),
    GALACTIC_ENGINEER(350, "Galactic Engineer"),
    ULTRON(400, "Ultron"),
    INTERGALACTIC_BROGRAMMER(500, "Intergalactic Brogrammer");

    private final int value;
    private final String stringVal;

    TitleOfHonor(int value, String stringVal) {
        this.value = value;
        this.stringVal = stringVal;
    }

    public int getValue() {
        return value;
    }

    public String getStringVal() {
        return stringVal;
    }
}
