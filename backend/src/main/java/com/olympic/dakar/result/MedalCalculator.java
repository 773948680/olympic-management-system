package com.olympic.dakar.result;

public final class MedalCalculator {

    private MedalCalculator() {
    }

    public static MedalType forPosition(Integer position) {
        if (position == null) {
            return MedalType.NONE;
        }
        return switch (position) {
            case 1 -> MedalType.GOLD;
            case 2 -> MedalType.SILVER;
            case 3 -> MedalType.BRONZE;
            default -> MedalType.NONE;
        };
    }
}
