package org.minevale.bunkers.core.chat;

public enum ChatType {

    GLOBAL,
    LOCAL;

    public String id() {
        return name().toLowerCase();
    }

    public static ChatType parse(String source) {
        for (ChatType type : ChatType.values()) {
            if (source.toUpperCase().startsWith(type.name().substring(0, 1))) {
                return type;
            }
        }
        return null;
    }

}
