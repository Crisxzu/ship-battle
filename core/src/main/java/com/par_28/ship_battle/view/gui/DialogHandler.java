package com.par_28.ship_battle.view.gui;

/**
 * Handler for dialog display in the GUI.
 */
public class DialogHandler implements Handler {
    /**
     * Dialog identifiers.
     */
    public enum DialogID {
        /**
         * Turn start dialog
         */
        TURN_START(
            0,
            "It's your turn !\n" +
                "Choose a case on tracking grid and beat the shit out of them !"
        ),
        /**
         * Miss shot dialog
         */
        MISS(
            1,
            "YOU MISSED ! TOO BAD >< !"
        ),
        /**
         * Hit shot dialog
         */
        HIT(
            2,
            "YOU HIT YOUR OPPONENT BOAT ! CONTINUE ON THAT !"
        ),
        /**
         * Sunk shot dialog
         */
        SUNK(
            3,
            "Ouch, I wouldn't liked that. You are a monster !"
        ),

        /**
         * Already hit dialog
         */
        ALREADY_HIT(
            4,
            "Baka ! You have already hit this case."
        );

        /**
         * Index of dialog
         */
        private final int value;

        /**
         * Dialog text
         */
        private final String text;

        /**
         * Initialize dialog
         * @param value dialog index
         * @param text dialog text
         */
        DialogID(int value, String text) {
            this.value = value;
            this.text = text;
        }

    }

    /**
     * Current dialog text
     */
    private static String dialogText;
    /**
     * Current dialog duration
     */
    private static float dialogDuration = 0;

    /**
     * Play a dialog 
     * 
     * @param dialogID specified dialog
     * @param duration duration to display dialog
     */
    public static void playDialog(DialogID dialogID, float duration) {
        dialogText = dialogID.text;
        dialogDuration = duration;
    }

    /**
     * Get the number of characters to display this frame
     * @param elapsed elapsed time since dialog started
     * @return number of characters to display
     */
    public static int getCharCountThisFrame(float elapsed) {
        return (int) ((elapsed / dialogDuration) * dialogText.length());
    }

    /**
     * Get dialog duration
     * 
     * @return dialog duration
     */
    public static float getDialogDuration() {
        return dialogDuration;
    }

    /**
     * Get dialog text
     * 
     * @return dialog text
     */
    public static String getDialogText() {
        return dialogText;
    }

    /**
     * Dispose resources
     */
    public void dispose() {

    }
}
