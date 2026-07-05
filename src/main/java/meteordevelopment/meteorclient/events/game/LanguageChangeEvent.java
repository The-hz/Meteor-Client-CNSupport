/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.game;

import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.util.List;

public class LanguageChangeEvent extends Cancellable {
    private final String languageCode;
    public LanguageChangeEvent(String languageCode) {
        this.languageCode = languageCode;
        List<Module> modules = Modules.get().getList();
        for (Module module : modules) {
            module.changeLanguage();
        }
    }
    public String getLanguageCode() {
        return languageCode;
    }
}
