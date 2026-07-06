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
        if (Translator.languageCodeFromEvent != null) {
            if(!Translator.languageCodeFromEvent.equals(languageCode)){
                Translator.languageCodeFromEvent = languageCode;
            }
        }
        Translator.reload(mc.getResourceManager());
        for (Module module : modules) {
            module.title = translateName(module.name, TranslationType.Module);
            module.description = translateDescription(module.name, TranslationType.Module);
            for (var group : module.settings) {
                for (var setting : group) {
                    setting.title = translateName(setting.name, TranslationType.Setting);
                    setting.description = translateDescription(setting.name, TranslationType.Setting);
                }
            }
        }
    }

    public String getLanguageCode() {
        return languageCode;
    }

    private String translateName(String name, TranslationType type){
        if(type == TranslationType.Setting){
            String settingKey = "Setting.Meteor." + name;
            return Translator.Translate(settingKey, name);
        }else if(type == TranslationType.Module){
            String moduleKey = "Module.Meteor." + name;
            return Translator.Translate(moduleKey, name);
        }
        return name + "Error Msg";
    }

    private String translateDescription(String name, TranslationType type){
        if(type == TranslationType.Setting){
            String settingKey = "Setting.Meteor." + name + ".Description";
            return Translator.Translate(settingKey, name);
        } else if (type == TranslationType.Module) {
            String moduleKey = "Module.Meteor." + name + ".Description";
            return Translator.Translate(moduleKey, name);
        }
        return name + "Error Msg";
    }

    public enum TranslationType{
        Module,
        Setting
    }
}
