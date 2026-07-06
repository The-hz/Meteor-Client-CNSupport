/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.game;

import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Translator;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LanguageChangeEvent extends Cancellable {
    private final String languageCode;
    public LanguageChangeEvent(String languageCode) {
        this.languageCode = languageCode;
        List<Module> modules = Modules.get().getList();
        Translator translator = Translator.getInstance();
        translator.reload(mc.getResourceManager());
        for (Module module : modules) {
            module.changeLanguage(languageCode);
            for (var group : module.settings) {
                for (var setting : group) {
                    setting.changeLanguage(languageCode);
                }
            }
        }
    }
    public String getLanguageCode() {
        return languageCode;
    }
}
