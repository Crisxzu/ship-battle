package com.par_28.ship_battle.view.gui;

public class DialogHandler implements Handler{
    public enum DialogID {
        TURN_START(
            0,
            "It's your turn !\n" +
                "Choose a case on tracking grid and beat the shit out of them !"
        ),
        MISS(
            1,
            "YOU MISSED ! TOO BAD >< !"
        ),
        HIT(
            2,
            "YOU HIT YOUR OPPONENT BOAT ! CONTINUE ON THAT !"
        ),
        SUNK(
            3,
            "Ouch, I wouldn't liked that. You are a monster !"
        ),;

        private final int value;
        private final String text;

        DialogID(int value, String text) {
            this.value = value;
            this.text = text;
        }

    }

    private static String dialogText;
    private static float dialogDuration = 0;

    public static void playDialog(DialogID dialogID, float duration) {
        dialogText = dialogID.text;
        dialogDuration = duration;
    }

    public static int getCharCountThisFrame(float elapsed) {
        return (int) ((elapsed / dialogDuration) * dialogText.length());
    }

    public static float getDialogDuration() {
        return dialogDuration;
    }

    public static String getDialogText() {
        return dialogText;
    }

    public void dispose() {

    }
}
