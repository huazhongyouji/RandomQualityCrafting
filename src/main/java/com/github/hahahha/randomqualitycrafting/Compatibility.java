package com.github.hahahha.randomqualitycrafting;

public class Compatibility {
    public static final boolean HAS_ITE;

    static {
        boolean conflict = false;
        try {
            Class.forName("net.xiaoyu233.mitemod.miteite.MITEITEMod");
            conflict = true;
        } catch (ClassNotFoundException ignored) {}
        HAS_ITE = conflict;
    }
}