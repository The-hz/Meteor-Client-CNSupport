/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.TranslationStorage;
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

public class Translator {
    private static final Translator INSTANCE = new Translator();
    public static Translator getInstance() { return INSTANCE; }

    private final JsonObject langJson = new JsonObject();
    private TranslationStorage mcEnglish;
    private Map<String, String> currentLangStrings;

    public String Translate(String key, String name) {
        String value = this.currentLangStrings != null ? this.currentLangStrings.get(key) : null;
        if(value != null){
            return value;
        } else {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            langJson.addProperty(key, name);
            Path path = Paths.get("lang.json");
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                gson.toJson(langJson, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public void reload(ResourceManager manager) {
        if (manager == null) return;
        mcEnglish = TranslationStorage.load(manager, Lists.newArrayList("en_us.json"), false);

        HashMap<String, String> currentLangStrings = new HashMap<>();
        loadTranslations(manager, getCurrentLangCodes(), currentLangStrings::put);
        this.currentLangStrings = Collections.unmodifiableMap(currentLangStrings);
    }

    private Iterable<String> getCurrentLangCodes() {
        String mainLangCode = MinecraftClient.getInstance().getLanguageManager().getLanguage().toLowerCase();
        ArrayList<String> langCodes = new ArrayList<>();
        langCodes.add("en_us.json");
        if(!"en_us.json".equals(mainLangCode)) langCodes.add(mainLangCode);
        return langCodes;
    }

    public String translateMc(String key, Object... args) {
        if(I18n.hasTranslation(key)) return I18n.translate(key, args);
        return key;
    }

    private void loadTranslations(ResourceManager manager, Iterable<String> langCodes, BiConsumer<String, String> entryConsumer) {
        for(String langCode : langCodes) {
            String langFilePath = "lang/" + langCode + ".json";
            // 将原本的 yalu 改为 meteor-client，使其读取本体的语言文件
            Identifier langId = Identifier.of(MeteorClient.MOD_ID, langFilePath);
            for(Resource resource : manager.getAllResources(langId)) {
                try(InputStream stream = resource.getInputStream()) {
                    Language.load(stream, entryConsumer);
                } catch(IOException e) {
                    System.out.println("Failed to load translations for " + langCode + " from pack " + resource.getPackId());
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, String> getCurrentLangStrings() { return currentLangStrings; }
    public TranslationStorage getMcEnglish() { return mcEnglish; }
}
