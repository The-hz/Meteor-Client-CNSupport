/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 *
 * Derived from dingzhen-vape's Meteor-I18n-Support-plugin (CC0 1.0)
 * Original: https://github.com/dingzhen-vape/Meteor-I18n-Support-plugin
 */

//其实基本都是我拿格调改的，被我改的爹妈认不出来了💦

package meteordevelopment.meteorclient.utils.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Translator {
    private static final JsonObject langJson = new JsonObject();
    private static Map<String, String> currentLangStrings;

    public static String languageCodeFromEvent = "zh_cn";

    private Translator() {}

    public static String Translate(String key, String name) {
        String value = null;
        if (currentLangStrings != null) {
            value = currentLangStrings.get(key);
            if (!languageCodeFromEvent.equals("zh_cn")) {
                if (!key.contains(".Description")) {
                    value = Utils.nameToTitle(name);
                }else{
                    value = name;
                }
            }
        }

        if (value != null) {
            return value;
        } else {//when missing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            langJson.addProperty(key, name);
            Path path = Paths.get("lang.json");
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                gson.toJson(langJson, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(key.contains(".Description")){
                value = name;
            }else{
                value = Utils.nameToTitle(name);
            }
            return value;
        }
    }

    public static void initTranslator() {
        languageCodeFromEvent = mc.getLanguageManager().getLanguage();
    }

    public static void reload(ResourceManager manager) {
        if (manager == null) return;
        HashMap<String, String> currentLangStrings = new HashMap<>();
        loadTranslations(manager, getCurrentLangCode(), currentLangStrings::put);
        Translator.currentLangStrings = Collections.unmodifiableMap(currentLangStrings);
    }

    private static String getCurrentLangCode() {
        return languageCodeFromEvent;
    }

    private static void loadTranslations(ResourceManager manager, String langCode, BiConsumer<String, String> entryConsumer) {
        String langFilePath = "lang/" + langCode + ".json";

        Identifier langId = Identifier.of(MeteorClient.MOD_ID, langFilePath);

        for (Resource resource : manager.getAllResources(langId)) {
            try (InputStream stream = resource.getInputStream()) {
                Language.load(stream, entryConsumer);
            } catch (IOException e) {
                System.out.println("Failed to load translations for " + langCode + " from pack " + resource.getPackId());
                e.printStackTrace();
            }
        }
    }
}
