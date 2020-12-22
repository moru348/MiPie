package me.moru3.mipie.SubCommand;

public enum SelectorType {
    NEAREST_PLAYER("@p"),
    RANDOM_PLAYER("@r"),
    ALL_PLAYERS("@a"),
    EXECUTING_ENTITY("@s"),
    ;
    String name;
    SelectorType(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return name;
    }
}
