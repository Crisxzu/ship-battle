package com.par_28.ship_battle.view.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.par_28.ship_battle.controller.gui.GuiControllerEnum;
import com.par_28.ship_battle.controller.gui.ScreenController;
import com.par_28.ship_battle.controller.gui.SettingsController;


/**
 * Settings menu
 */
public class SettingsView extends GuiView<SettingsController> {

    /**
     * Title label
     */
    private Label titleLabel;
    /**
     * Sound volume label
     */
    private Label soundVolumeLabel;
    /**
     * Music volume label
     */
    private Label musicVolumeLabel;

    /**
     * Sound volume slider
     */
    private Slider soundVolumeSlider;

    /**
     * Music volume slider
     */
    private Slider musicVolumeSlider;

    /**
     * Settings data
     */
    private SettingsHandler data;

    /**
     * Initialize settings menu
     *
     * @param parent screen manager
     * @param controller settings menu controller
     */
    public SettingsView(ScreenController parent, SettingsController controller) {
        super(parent, controller);
        buildUI();
    }

    @Override
    protected void buildUI() {
        super.buildUI();
        data = this.parent.app.settingsHandler;
        Table root = new Table();
        root.setFillParent(true);

        titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(2.5f);

        root.add(titleLabel)
            .expandX()
            .height(Value.percentHeight(0.2f, root))
            .row();

        Table buttons = new Table();
        buttons.defaults()
            .width(Value.percentWidth(0.35f, root))
            .height(Value.percentHeight(0.1f, root))
            .pad(Value.percentHeight(0.02f, root));

        VerticalGroup soundVolumeGroup = new VerticalGroup();

        soundVolumeLabel = new Label("", skin);
        soundVolumeLabel.setText(String.format("Sound volume: %.0f%%", data.getSoundVolume() * 100));
        soundVolumeGroup.addActor(soundVolumeLabel);

        soundVolumeSlider = new Slider(0, 100, 10, false, skin);
        soundVolumeSlider.setValue(data.getSoundVolume() * 100);
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolumeLabel.setText(String.format("Sound volume : %.0f%%", soundVolumeSlider.getValue()));
                SoundHandler.playSound(
                    SoundHandler.SoundID.ALREADY_HIT,
                    0.2f * (soundVolumeSlider.getValue() / 100)
                );
            }
        });
        soundVolumeGroup.addActor(soundVolumeSlider);

        buttons.add(soundVolumeGroup).row();

        VerticalGroup musicVolumeGroup = new VerticalGroup();

        musicVolumeLabel = new Label("", skin);
        musicVolumeLabel.setText(String.format("Music volume : %.0f%%", data.getMusicVolume() * 100));
        musicVolumeGroup.addActor(musicVolumeLabel);

        musicVolumeSlider = new Slider(0, 100, 10, false, skin);
        musicVolumeSlider.setValue(data.getMusicVolume() * 100);
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolumeLabel.setText(String.format("Music volume : %.0f%%", musicVolumeSlider.getValue()));
                SoundHandler.playTrack(
                    SoundHandler.TrackID.MENU_THEME,
                    0.2f * (musicVolumeSlider.getValue() / 100),
                    true
                );
            }
        });
        musicVolumeGroup.addActor(musicVolumeSlider);

        buttons.add(musicVolumeGroup).row();

        addMenuButton(buttons, "Save", () -> {
            controller.changeMusicVolume(musicVolumeSlider.getValue() / 100);
            controller.changeSoundVolume(soundVolumeSlider.getValue() / 100);
            controller.saveSettings();
            this.parent.changeController(GuiControllerEnum.MAIN_MENU);
        });
        addMenuButton(buttons, "Return to title", () -> this.parent.changeController(GuiControllerEnum.MAIN_MENU));

        root.add(buttons)
            .expand()
            .fill()
            .center()
            .row();

        stage.addActor(root);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setFontScale(base / 200f);
        soundVolumeLabel.setFontScale(base / 350f);
        musicVolumeLabel.setFontScale(base / 350f);
    }
}
