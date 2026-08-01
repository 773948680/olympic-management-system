package com.olympic.dakar.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedalCalculatorTest {

    @Test
    void firstPositionReceivesGold() {
        assertEquals(MedalType.GOLD, MedalCalculator.forPosition(1));
    }

    @Test
    void secondPositionReceivesSilver() {
        assertEquals(MedalType.SILVER, MedalCalculator.forPosition(2));
    }

    @Test
    void thirdPositionReceivesBronze() {
        assertEquals(MedalType.BRONZE, MedalCalculator.forPosition(3));
    }

    @Test
    void positionBelowPodiumReceivesNone() {
        assertEquals(MedalType.NONE, MedalCalculator.forPosition(4));
    }

    @Test
    void nullPositionReceivesNone() {
        assertEquals(MedalType.NONE, MedalCalculator.forPosition(null));
    }
}
