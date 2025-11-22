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
        ),

        /**
         * Turn start of AI dialog
         */
        TURN_START_AI(
            5,
            "Hum, let some time to the AI to think."
        ),

        /**
         * Rader selected dialog
         */
        RADAR_SELECTED(
            6,
            "Oh so you want to use the radar.\nAlright let's see where the enemy hides his ship."
        ),

        /**
         * Radar found nothing dialog
         */
        RADAR_NOTHING(
            7,
            "Hahaha, too bad !\nYou miss a big opportunity."
        ),

        /**
         * Radar found ship dialog
         */
        RADAR_FOUND(
            8,
            "You found it !\nAll you have to do is to not miss hehehe."
        ),

        /**
         * Unavailable power dialog
         */
        UNAVAILABLE_POWER(
            9,
            "You cannot used this power for now. So now attack !"
        ),

        /**
         * Bomb selected dialog
         */
        BOMB_SELECTED(
            10,
            "Hahaha, it's time to throw a big one on the stage !"
        ),

        /**
         * Bomb shot dialog
         */
        BOMB_SHOT(
            11,
            "EXPLOOOOOOSION !"
        ),

        /**
         * Konami code dialog
         */
        KONAMI_CODE(
            12,
            "You !\nLittle cheater ><. How did you know this secret ?"
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
