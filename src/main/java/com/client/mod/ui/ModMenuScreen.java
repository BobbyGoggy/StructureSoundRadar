package com.client.mod.ui;

import com.client.mod.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModMenuScreen extends Screen {
    private int selectedTab = 0;
    private boolean waitingForKey = false;
    private String bindingFeature = null;

    public ModMenuScreen() {
        super(Text.translatable("mod.clientmod.title"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Функции"), button -> {
            selectedTab = 0;
            rebuildWidgets();
        }).dimensions(centerX - 110, centerY - 80, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Бинд клавиш"), button -> {
            selectedTab = 1;
            rebuildWidgets();
        }).dimensions(centerX + 10, centerY - 80, 100, 20).build());

        if (selectedTab == 0) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Сканер структур: " + (Config.structureScannerEnabled ? "§aВКЛ" : "§cВЫКЛ")),
                    button -> {
                        Config.structureScannerEnabled = !Config.structureScannerEnabled;
                        button.setMessage(Text.literal("Сканер структур: " + (Config.structureScannerEnabled ? "§aВКЛ" : "§cВЫКЛ")));
                    }
            ).dimensions(centerX - 100, centerY - 40, 200, 20).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Радар звуков: " + (Config.soundRadarEnabled ? "§aВКЛ" : "§cВЫКЛ")),
                    button -> {
                        Config.soundRadarEnabled = !Config.soundRadarEnabled;
                        button.setMessage(Text.literal("Радар звуков: " + (Config.soundRadarEnabled ? "§aВКЛ" : "§cВЫКЛ")));
                    }
            ).dimensions(centerX - 100, centerY - 10, 200, 20).build());

        } else {
            String menuKeyName = org.lwjgl.glfw.GLFW.glfwGetKeyName(Config.menuKey, 0);
            if (menuKeyName == null) menuKeyName = "Правый Enter / Key " + Config.menuKey;

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Меню: " + menuKeyName.toUpperCase()),
                    button -> {
                        waitingForKey = true;
                        bindingFeature = "menu";
                        button.setMessage(Text.literal("Нажмите клавишу..."));
                    }
            ).dimensions(centerX - 100, centerY - 40, 200, 20).build());
        }

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("mod.clientmod.close"),
                button -> this.close()
        ).dimensions(centerX - 100, centerY + 60, 200, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            if (bindingFeature != null && bindingFeature.equals("menu")) {
                Config.menuKey = keyCode;
            }
            waitingForKey = false;
            bindingFeature = null;
            rebuildWidgets();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
